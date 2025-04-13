package com.thaihoc.miniinsta.service.auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.model.Role;
import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.auth.CreateRoleRequest;
import com.thaihoc.miniinsta.repository.RoleRepository;
import com.thaihoc.miniinsta.exception.IdInvalidException;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    public Role handleCreateRole(CreateRoleRequest request) throws IdInvalidException {
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(request.isActive())
                .build();
        if (isRoleExist(role)) {
            throw new IdInvalidException("Role name already exists");

        }
        List<Long> permissionIds = request.getPermissionIds().stream().map(permissionId -> {
            return permissionId;
        }).toList();
        List<Permission> permissions = this.permissionService.getPermissionsByIds(permissionIds);
        role.setPermissions(permissions);
        return this.roleRepository.save(role);
    }

    private boolean isRoleExist(Role role) {
        return this.roleRepository.existsByName(role.getName());
    }

    public Role handleUpdateRole(Role role) throws IdInvalidException {
        Role roleInDB = this.getRoleById(role.getId());
        if (roleInDB == null) {
            throw new IdInvalidException("Role with id = " + role.getId() + " does not exist");
        }
        if (!roleInDB.getName().equals(role.getName()) && isRoleExist(role)) {
            throw new IdInvalidException("Role name already exist");
        }
        roleInDB.setName(role.getName());
        roleInDB.setDescription(role.getDescription());
        roleInDB.setActive(role.isActive());
        List<Long> permissionIds = role.getPermissions().stream().map(permission -> {
            return permission.getId();
        }).toList();
        List<Permission> permissions = this.permissionService.getPermissionsByIds(permissionIds);
        roleInDB.setPermissions(permissions);
        return this.roleRepository.save(roleInDB);
    }

    public Role getRoleById(long id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public Role handleGetRoleById(Long id) throws IdInvalidException {
        Role role = this.getRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role with id = " + id + " does not exist");
        }
        return role;
    }

    public ResultPaginationDTO handleGetAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());
        rs.setMeta(mt);
        List<Role> listRole = pageRole.getContent()
                .stream().map(role -> this.getRoleById(role.getId()))
                .collect(Collectors.toList());
        rs.setResult(listRole);
        return rs;
    }

    public List<Role> getRolesByIds(List<Long> roleIds) {
        return this.roleRepository.findAllById(roleIds);
    }
}
