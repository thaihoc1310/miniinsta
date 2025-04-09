package com.thaihoc.miniinsta.service.user;

import java.util.ArrayList;
import java.util.List;
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
import com.thaihoc.miniinsta.service.auth.PermissionService;
import com.thaihoc.miniinsta.service.auth.RoleService;
import com.thaihoc.miniinsta.util.SecurityUtil;

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
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserResponse handleGetUserByEmail(String email) throws IdInvalidException {
        User user = this.getUserByEmail(email);
        if (user != null) {
            return this.convertToUserResponse(user);
        }
        throw new IdInvalidException("User not found");
    }

    @Override
    public void handleUpdateUserToken(String email, String token) {
        User user = this.getUserByEmail(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
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
        User user = User.builder()
                .email(request.getEmail())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .id(UUID.randomUUID())
                .build();
        Profile profile = Profile.builder()
                .username(request.getUsername())
                .displayName(request.getName())
                .user(user)
                .followers(new ArrayList<>())
                .following(new ArrayList<>())
                .followersCount(0)
                .followingCount(0)
                .postsCount(0)
                .build();
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
        if (user != null) {
            return this.convertToUserResponse(user);
        }
        throw new IdInvalidException("User not found");
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
    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return userRepository.findByRefreshTokenAndEmail(token, email).orElse(null);
    }
}
