package com.ph4ntoms.authenticate.service.entity;

import com.ph4ntoms.authenticate.group.CreateMethod;
import com.ph4ntoms.authenticate.group.UpdateMethod;
import com.ph4ntoms.authenticate.model.Role;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.repo.PermissionRepository;
import com.ph4ntoms.authenticate.repo.RoleRepository;
import com.ph4ntoms.authenticate.request.entity.RoleRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements AService<Role, RoleRequest, UUID> {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<Role> findAllEntities() {
        return roleRepository.findAll();
    }

    @Override
    public Page<Role> searchEntities(PageableObject pageableObject) {
        return roleRepository.searchEntities(pageableObject.getQuery(), pageableObject.toPageRequest());
    }

    @Override
    @Transactional
    public Role createEntity(@Validated(CreateMethod.class) RoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Role with this name already exists");
        }
        Role role = new Role();
        mapRequestToEntity(role, request);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateEntityById(UUID id, @Validated(UpdateMethod.class) RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));

        roleRepository.findByName(request.getName())
                .filter(existingRole -> !existingRole.getId().equals(id))
                .ifPresent(r -> {
                    throw new IllegalArgumentException("Role with this name already exists");
                });

        mapRequestToEntity(role, request);
        return roleRepository.save(role);
    }

    @Override
    public Role findEntityById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional
    public void removeEntityById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        role.setEnabled(false);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void removeEntitiesByIds(List<UUID> ids) {
        List<Role> roles = roleRepository.findAllById(ids);
        roles.forEach(role -> role.setEnabled(false));
        roleRepository.saveAll(roles);
    }

    @Override
    @Transactional
    public void mapRequestToEntity(Role entity, RoleRequest request) {
        entity.setName(request.getName());
        entity.setCode(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEnabled(true);

        if (request.getPermissionIds() != null) {
            entity.setPermissions(request.getPermissionIds().stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId)))
                    .collect(Collectors.toSet()));
        }
    }
} 