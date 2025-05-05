package com.ph4ntoms.authenticate.controller;

import com.ph4ntoms.authenticate.mapper.RoleResponseMapper;
import com.ph4ntoms.authenticate.pageable.CustomPage;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.request.batch.BatchDeleteRequest;
import com.ph4ntoms.authenticate.request.entity.RoleRequest;
import com.ph4ntoms.authenticate.response.entity.RoleResponse;
import com.ph4ntoms.authenticate.service.entity.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final RoleResponseMapper mapper;

    @PostMapping("/search")
    public ResponseEntity<?> searchRoles(@Valid @RequestBody PageableObject pageableObject) {
        return ResponseEntity.ok(new CustomPage<RoleResponse>(roleService.searchEntities(pageableObject).map(mapper::toDTO)));
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(mapper.toDTO(roleService.createEntity(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(mapper.toDTO(roleService.updateEntityById(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(roleService.findEntityById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.removeEntityById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteRoles(@Valid @RequestBody BatchDeleteRequest request) {
        roleService.removeEntitiesByIds(request.getIds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleService.findAllEntities().stream().map(mapper::toDTO).toList());
    }
} 