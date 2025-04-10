package com.thaihoc.miniinsta.controller.auth;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import com.thaihoc.miniinsta.model.Role;
import com.thaihoc.miniinsta.service.auth.RoleService;
import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.auth.CreateRoleRequest;
import com.thaihoc.miniinsta.util.annotation.ApiMessage;
import com.thaihoc.miniinsta.exception.IdInvalidException;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createNewRole(@Valid @RequestBody CreateRoleRequest request)
            throws IdInvalidException, MethodArgumentNotValidException {
        Role newRole = this.roleService.handleCreateRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PutMapping
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role)
            throws IdInvalidException, MethodArgumentNotValidException {
        Role updatedRole = this.roleService.handleUpdateRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRole);
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch a role by id")
    public ResponseEntity<Role> fetchRoleById(@PathVariable Long id) throws IdInvalidException {
        return ResponseEntity.ok(this.roleService.handleGetRoleById(id));
    }

    @GetMapping
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDTO> fetchAllRoles(@Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.roleService.handleGetAllRoles(spec, pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) throws IdInvalidException {
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
