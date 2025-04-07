package com.thaihoc.miniinsta.service.role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.model.Role;
import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.repository.PermissionRepository;
import com.thaihoc.miniinsta.exception.IdInvalidException;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }

    public Permission handleCreatePermission(Permission permission) throws IdInvalidException {
        if (isPermissionExist(permission)) {
            throw new IdInvalidException("Permission already exists");

        }
        return this.permissionRepository.save(permission);
    }

    @Override
    public Permission handleUpdatePermission(Permission permission) throws IdInvalidException {
        Permission permissionInDB = this.getPermissionById(permission.getId());
        if (permissionInDB == null) {
            throw new IdInvalidException("Permission with id = " + permission.getId() + " does not exist");
        }
        if (!(permissionInDB.getModule().equals(permission.getModule()) &&
                permissionInDB.getApiPath().equals(permission.getApiPath()) &&
                permissionInDB.getMethod().equals(permission.getMethod()))
                && isPermissionExist(permission)) {
            throw new IdInvalidException("Permission already exist");
        }
        permissionInDB.setName(permission.getName());
        permissionInDB.setApiPath(permission.getApiPath());
        permissionInDB.setMethod(permission.getMethod());
        permissionInDB.setModule(permission.getModule());
        return this.permissionRepository.save(permissionInDB);
    }

    @Override
    public Permission getPermissionById(Long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }

    @Override
    public ResultPaginationDTO handleGetAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());
        rs.setMeta(mt);
        List<Permission> listPermission = pagePermission.getContent()
                .stream().map(Permission -> this.getPermissionById(Permission.getId()))
                .collect(Collectors.toList());
        rs.setResult(listPermission);
        return rs;
    }

    @Override
    public Set<Permission> getPermissionsByIds(Set<Long> permissionIds) {
        return this.permissionRepository.findAllById(permissionIds).stream().collect(Collectors.toSet());
    }

    @Override
    public void handleDeletePermission(Long id) {
        Permission permission = this.getPermissionById(id);
        List<Role> roles = permission.getRoles();
        roles.forEach(role -> {
            role.getPermissions().remove(permission);
        });
        this.permissionRepository.deleteById(id);
    }

    @Override
    public Permission getPermissionByName(String name) {
        return this.permissionRepository.findByName(name);
    }

}
