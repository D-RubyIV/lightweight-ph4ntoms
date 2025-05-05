package com.ph4ntoms.authenticate.controller;

import com.ph4ntoms.authenticate.mapper.PermissionResponseMapper;
import com.ph4ntoms.authenticate.pageable.CustomPage;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.request.batch.BatchDeleteRequest;
import com.ph4ntoms.authenticate.request.entity.PermissionRequest;
import com.ph4ntoms.authenticate.response.entity.PermissionResponse;
import com.ph4ntoms.authenticate.service.entity.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;
    private final PermissionResponseMapper mapper;

    @PostMapping("/search")
    public ResponseEntity<?> searchPermissions(@Valid @RequestBody PageableObject pageableObject) {
        return ResponseEntity.ok(new CustomPage<PermissionResponse>(permissionService.searchEntities(pageableObject).map(mapper::toDTO)));
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(mapper.toDTO(permissionService.createEntity(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable UUID id, @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(mapper.toDTO(permissionService.updateEntityById(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(permissionService.findEntityById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        permissionService.removeEntityById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deletePermissions(@Valid @RequestBody BatchDeleteRequest request) {
        permissionService.removeEntitiesByIds(request.getIds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {
        return ResponseEntity.ok(permissionService.findAllEntities().stream().map(mapper::toDTO).toList());
    }
} 