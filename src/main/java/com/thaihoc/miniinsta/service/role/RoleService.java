package com.thaihoc.miniinsta.service.role;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Role;

@Service
public interface RoleService {
    public Role handleCreateRole(Role role) throws IdInvalidException;

    public Role handleUpdateRole(Role role) throws IdInvalidException;

    public Role getRoleById(long id);

    public void handleDeleteRole(long id);

    public Role handleGetRoleById(Long id) throws IdInvalidException;

    public ResultPaginationDTO handleGetAllRoles(Specification<Role> spec, Pageable pageable);

    public List<Role> getRolesByIds(List<Long> roleIds);
}
