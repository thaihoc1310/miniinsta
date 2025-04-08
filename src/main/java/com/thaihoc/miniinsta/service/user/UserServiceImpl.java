package com.thaihoc.miniinsta.service.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.user.CreateUserRequest;
import com.thaihoc.miniinsta.dto.user.UpdateUserRequest;
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.exception.PermissionDeniedException;
import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.Role;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.repository.UserRepository;
import com.thaihoc.miniinsta.service.role.RoleService;
import com.thaihoc.miniinsta.util.SecurityUtil;
import com.thaihoc.miniinsta.service.role.PermissionService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;
    private final ProfileService profileService;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder,
            PermissionService permissionService, ProfileService profileService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.permissionService = permissionService;
        this.profileService = profileService;
    }

    @Override
    public User getUserById(UUID id) throws IdInvalidException {
        Optional<User> user = userRepository.findById(id);
        return this.getUserFromOptional(user);
    }

    @Override
    public User getUserByEmail(String email) throws IdInvalidException {
        Optional<User> user = userRepository.findByEmail(email);
        return this.getUserFromOptional(user);
    }

    private User getUserFromOptional(Optional<User> user) throws IdInvalidException {
        if (user.isPresent()) {
            return user.get();
        }
        throw new IdInvalidException("User not found");
    }

    @Override
    public void handleUpdateUserToken(String email, String token) throws IdInvalidException {
        User user = this.getUserByEmail(email);
        user.setRefreshToken(token);
        this.userRepository.save(user);
    }

    @Override
    public UserResponse handleUpdateUser(UpdateUserRequest request) throws IdInvalidException {
        User userUpdate = this.getUserById(request.getId());
        if (userUpdate != null) {
            // check role
            Role curRole = userUpdate.getRole();
            if (curRole != null) {
                Role dbRole = this.roleService.getRoleById(curRole.getId());
                userUpdate.setRole(dbRole);
            }
            if (request.getName() != null && !userUpdate.getName().equals(request.getName())) {
                userUpdate.setName(request.getName());
            }
            if (request.getDateOfBirth() != null && !userUpdate.getDateOfBirth().equals(request.getDateOfBirth())) {
                userUpdate.setDateOfBirth(request.getDateOfBirth());
            }
            if (request.getPhoneNumber() != null && !userUpdate.getPhoneNumber().equals(request.getPhoneNumber())) {
                userUpdate.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getProvider() != null && !userUpdate.getProvider().equals(request.getProvider())) {
                userUpdate.setProvider(request.getProvider());
            }
            if (request.getProviderId() != null) {
                userUpdate.setProviderId(request.getProviderId());
            }
            User updatedUser = this.userRepository.save(userUpdate);
            return this.convertToUserResponse(updatedUser);
        }
        throw new IdInvalidException("User not found");
    }

    @Override
    public UserResponse handleCreateUser(CreateUserRequest request) throws IdInvalidException {
        if (this.existsByEmail(request.getEmail())) {
            throw new IdInvalidException("Email already exists");
        }
        if (this.profileService.existsByUsername(request.getUsername())) {
            throw new IdInvalidException("Username already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setDateOfBirth(request.getDateOfBirth());
        Profile profile = new Profile();
        profile.setUsername(request.getUsername());
        profile.setDisplayName(request.getName());
        profile.setUser(user);
        user.setProfile(profile);
        User createdUser = this.userRepository.save(user);
        return this.convertToUserResponse(createdUser);
    }

    @Override
    public ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);
        List<UserResponse> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToUserResponse(item))
                .toList();
        rs.setResult(listUser);
        return rs;
    }

    @Override
    public UserResponse handleGetUserResponseById(UUID id) throws IdInvalidException {
        User user = this.getUserById(id);
        return this.convertToUserResponse(user);
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .picture(user.getPicture())
                .role(user.getRole())
                .profile(user.getProfile())
                .build();
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) throws IdInvalidException {
        return this.getUserFromOptional(userRepository.findByRefreshTokenAndEmail(refreshToken, email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void handleDeleteUserById(UUID id) throws IdInvalidException {
        User user = this.getUserById(id);
        if (user.getEmail().equals(
                SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null)) {
            this.userRepository.deleteById(id);
            return;
        }
        Role role = user.getRole();
        if (role != null && role.isActive()) {
            Permission permission = permissionService.getPermissionByName("user:hard-delete");
            if (permission != null && role.getPermissions().contains(permission)) {
                this.userRepository.deleteById(id);
                return;
            }
        }
        throw new PermissionDeniedException("You don't have permission");
    }

    @Override
    public User handleGetUserByRefreshTokenAndEmail(String token, String email) throws IdInvalidException {
        return this.getUserFromOptional(userRepository.findByRefreshTokenAndEmail(token, email));
    }
}
