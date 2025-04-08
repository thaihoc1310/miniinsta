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
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.model.Role;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.repository.UserRepository;
import com.thaihoc.miniinsta.service.role.RoleService;
import com.thaihoc.miniinsta.service.role.PermissionService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder,
            PermissionService permissionService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.permissionService = permissionService;
    }

    @Override
    public User getUserById(UUID id) throws IdInvalidException {
        Optional<User> user = userRepository.findById(id);
        return this.getUserFromOptional(user);
    }

    @Override
    public User getUserByUsername(String username) throws IdInvalidException {
        Optional<User> user = userRepository.findByUsername(username);
        return this.getUserFromOptional(user);
    }

    @Override
    public User getUserByEmail(String email) throws IdInvalidException {
        Optional<User> user = userRepository.findByEmail(email);
        return this.getUserFromOptional(user);
    }

    private User getUserFromOptional(Optional<User> user) throws IdInvalidException {
        if (user.isPresent()) {
            if (user.get().isDeleted()) {
                throw new IdInvalidException("User not found");
            }
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
    public UserResponse handleUpdateUser(User user) throws IdInvalidException {
        User userUpdate = this.getUserById(user.getId());
        if (userUpdate != null) {
            // check role
            Role curRole = user.getRole();
            if (curRole != null) {
                Role dbRole = this.roleService.getRoleById(curRole.getId());
                userUpdate.setRole(dbRole);
            }
            if (user.getName() != null) {
                userUpdate.setName(user.getName());
            }
            if (user.getDateOfBirth() != null) {
                userUpdate.setDateOfBirth(user.getDateOfBirth());
            }
            if (user.getPhoneNumber() != null) {
                userUpdate.setPhoneNumber(user.getPhoneNumber());
            }
            if (user.getAddress() != null) {
                userUpdate.setAddress(user.getAddress());
            }
            if (user.getProvider() != null) {
                userUpdate.setProvider(user.getProvider());
            }
            if (user.getProviderId() != null) {
                userUpdate.setProviderId(user.getProviderId());
            }
            User updatedUser = this.userRepository.save(userUpdate);
            return this.convertToUserResponse(updatedUser);
        }
        throw new IdInvalidException("User not found");
    }

    @Override
    public UserResponse handleCreateUser(User user) throws IdInvalidException {
        if (this.existsByUsername(user.getUsername())) {
            throw new IdInvalidException("Username already exists");
        }
        if (this.existsByEmail(user.getEmail())) {
            throw new IdInvalidException("Email already exists");
        }
        if (user.getRole() != null) {
            Role role = this.roleService.getRoleById(user.getRole().getId());
            user.setRole(role);
        }
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
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
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
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
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void handleHardDeleteUserById(UUID id) throws IdInvalidException {
        this.userRepository.deleteById(id);
    }

    public void handleSoftDeleteUserById(UUID id) throws IdInvalidException {
        User user = this.getUserById(id);
        user.setDeleted(true);
        this.userRepository.save(user);
    }

    @Override
    public void handleDeleteUserById(UUID id, boolean permanent) throws IdInvalidException {
        if (permanent) {
            User user = this.getUserById(id);
            Role role = user.getRole();
            if (role != null) {
                Permission permission = permissionService.getPermissionByName("user:soft-delete");
                if (permission != null && role.getPermissions().contains(permission)) {
                    this.handleHardDeleteUserById(id);
                }
                this.handleSoftDeleteUserById(id);
            } else {
                this.handleSoftDeleteUserById(id);
            }
        } else {
            this.handleSoftDeleteUserById(id);
        }
    }

    @Override
    public void handleRestoreUserById(UUID id) throws IdInvalidException {
        User user = this.getUserById(id);
        user.setDeleted(false);
        this.userRepository.save(user);
    }

}
