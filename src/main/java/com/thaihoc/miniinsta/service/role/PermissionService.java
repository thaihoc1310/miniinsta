package com.thaihoc.miniinsta.service.role;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.dto.ResultPaginationDTO;

@Service
public interface PermissionService {

    public boolean isPermissionExist(Permission permission);

    public Permission handleCreatePermission(Permission permission);

    public Permission handleUpdatePermission(Permission permission);

    public Permission getPermissionById(Long id);

    public ResultPaginationDTO handleGetAllPermissions(Specification<Permission> spec, Pageable pageable);

    public List<Permission> getPermissionsByIds(List<Long> permissionIds);

    public void handleDeletePermission(Long id);
}
