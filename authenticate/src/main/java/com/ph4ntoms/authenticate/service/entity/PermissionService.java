package com.ph4ntoms.authenticate.service.entity;

import com.ph4ntoms.authenticate.group.CreateMethod;
import com.ph4ntoms.authenticate.group.UpdateMethod;
import com.ph4ntoms.authenticate.model.Permission;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.repo.PermissionRepository;
import com.ph4ntoms.authenticate.request.entity.PermissionRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService implements AService<Permission, PermissionRequest, UUID> {
    private final PermissionRepository permissionRepository;

    @Override
    public List<Permission> findAllEntities() {
        return permissionRepository.findAll();
    }

    @Override
    public Page<Permission> searchEntities(PageableObject pageableObject) {
        return permissionRepository.searchEntities(pageableObject.getQuery(), pageableObject.toPageRequest());
    }

    @Override
    @Transactional
    public Permission createEntity(@Validated(CreateMethod.class) PermissionRequest request) {
        if (permissionRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Permission with this name already exists");
        }
        Permission permission = new Permission();
        mapRequestToEntity(permission, request);
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Permission updateEntityById(UUID id, @Validated(UpdateMethod.class) PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        permissionRepository.findByName(request.getName())
                .filter(existingPermission -> !existingPermission.getId().equals(id))
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Permission with this name already exists");
                });

        mapRequestToEntity(permission, request);
        return permissionRepository.save(permission);
    }

    @Override
    public Permission findEntityById(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));
    }

    @Override
    @Transactional
    public void removeEntityById(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));
        permission.setEnabled(false);
        permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public void removeEntitiesByIds(List<UUID> ids) {
        List<Permission> permissions = permissionRepository.findAllById(ids);
        permissions.forEach(permission -> permission.setEnabled(false));
        permissionRepository.saveAll(permissions);
    }

    @Override
    @Transactional
    public void mapRequestToEntity(Permission entity, PermissionRequest request) {
        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
    }
} 