package com.thaihoc.miniinsta.controller.auth;

import com.thaihoc.miniinsta.model.Permission;
import com.thaihoc.miniinsta.service.auth.PermissionService;
import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.util.annotation.ApiMessage;
import com.thaihoc.miniinsta.exception.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException, MethodArgumentNotValidException {
        Permission newPermission = this.permissionService.handleCreatePermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    @PutMapping
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException, MethodArgumentNotValidException {
        Permission updatedPermission = this.permissionService.handleUpdatePermission(permission);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPermission);
    }

    @GetMapping
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.handleGetAllPermissions(spec, pageable));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) throws IdInvalidException {
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
