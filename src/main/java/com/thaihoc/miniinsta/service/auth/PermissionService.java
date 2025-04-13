package com.thaihoc.miniinsta.service.auth;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.dto.auth.CreatePermissionRequest;

public interface PermissionService {

    public boolean isPermissionExist(Permission permission);

    public Permission handleCreatePermission(CreatePermissionRequest request) throws IdInvalidException;

    public Permission handleUpdatePermission(Permission permission) throws IdInvalidException;

    public Permission getPermissionById(Long id);

    public ResultPaginationDTO handleGetAllPermissions(Specification<Permission> spec, Pageable pageable);

    public List<Permission> getPermissionsByIds(List<Long> permissionIds);

    public void handleDeletePermission(Long id);

    public Permission getPermissionByName(String name);
}
