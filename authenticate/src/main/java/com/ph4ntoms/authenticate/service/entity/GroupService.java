package com.ph4ntoms.authenticate.service.entity;

import com.ph4ntoms.authenticate.group.CreateMethod;
import com.ph4ntoms.authenticate.group.UpdateMethod;
import com.ph4ntoms.authenticate.model.Group;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.repo.GroupRepository;
import com.ph4ntoms.authenticate.repo.RoleRepository;
import com.ph4ntoms.authenticate.repo.UserRepository;
import com.ph4ntoms.authenticate.request.entity.GroupRequest;
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
public class GroupService implements AService<Group, GroupRequest, UUID> {
    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<Group> findAllEntities() {
        return groupRepository.findAll();
    }

    @Override
    public Page<Group> searchEntities(PageableObject pageableObject) {
        return groupRepository.searchEntities(pageableObject.getQuery(), pageableObject.toPageRequest());
    }

    @Override
    @Transactional
    public Group createEntity(@Validated(CreateMethod.class) GroupRequest request) {
        if (groupRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Group with this name already exists");
        }
        Group group = new Group();
        mapRequestToEntity(group, request);
        return groupRepository.save(group);
    }

    @Override
    @Transactional
    public Group updateEntityById(UUID id, @Validated(UpdateMethod.class) GroupRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));

        groupRepository.findByName(request.getName())
                .filter(existingGroup -> !existingGroup.getId().equals(id))
                .ifPresent(g -> {
                    throw new IllegalArgumentException("Group with this name already exists");
                });

        mapRequestToEntity(group, request);
        return groupRepository.save(group);
    }

    @Override
    public Group findEntityById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));
    }

    @Override
    @Transactional
    public void removeEntityById(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));
        group.setEnabled(false);
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void removeEntitiesByIds(List<UUID> ids) {
        List<Group> groups = groupRepository.findAllById(ids);
        groups.forEach(group -> group.setEnabled(false));
        groupRepository.saveAll(groups);
    }

    @Override
    @Transactional
    public void mapRequestToEntity(Group entity, GroupRequest request) {
        entity.setName(request.getName());
        entity.setCode(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEnabled(true);

        if (request.getRoleIds() != null) {
            entity.setRoles(request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId)))
                    .collect(Collectors.toSet()));
        }
    }
} 