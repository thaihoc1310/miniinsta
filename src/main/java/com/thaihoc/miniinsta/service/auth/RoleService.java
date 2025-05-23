package com.thaihoc.miniinsta.service.auth;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.auth.CreateRoleRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Role;

public interface RoleService {
    public Role handleCreateRole(CreateRoleRequest request) throws IdInvalidException;

    public Role handleUpdateRole(Role role) throws IdInvalidException;

    public Role getRoleById(long id);

    public void handleDeleteRole(long id);

    public Role handleGetRoleById(Long id) throws IdInvalidException;

    public ResultPaginationDTO handleGetAllRoles(Specification<Role> spec, Pageable pageable);

    public List<Role> getRolesByIds(List<Long> roleIds);
}
