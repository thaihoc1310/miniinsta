package com.thaihoc.miniinsta.service.role;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.IdInvalidException;

@Service
public interface PermissionService {

    public boolean isPermissionExist(Permission permission);

    public Permission handleCreatePermission(Permission permission) throws IdInvalidException;

    public Permission handleUpdatePermission(Permission permission) throws IdInvalidException;

    public Permission getPermissionById(Long id);

    public ResultPaginationDTO handleGetAllPermissions(Specification<Permission> spec, Pageable pageable);

    public Set<Permission> getPermissionsByIds(Set<Long> permissionIds);

    public void handleDeletePermission(Long id);

    public Permission getPermissionByName(String name);
}
