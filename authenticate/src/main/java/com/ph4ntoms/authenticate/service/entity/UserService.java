package com.ph4ntoms.authenticate.service.entity;

import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.request.entity.UserRequest;
import com.ph4ntoms.authenticate.group.CreateMethod;
import com.ph4ntoms.authenticate.group.UpdateMethod;
import com.ph4ntoms.authenticate.model.User;
import com.ph4ntoms.authenticate.repo.GroupRepository;
import com.ph4ntoms.authenticate.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements AService<User, UserRequest, UUID> {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAllEntities() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> searchEntities(PageableObject pageableObject) {
        return userRepository.searchEntities(pageableObject.getQuery(), pageableObject.toPageRequest());
    }

    @Override
    @Transactional
    public User createEntity(@Validated(CreateMethod.class) UserRequest request) {
        if (userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        User user = new User();
        mapRequestToEntity(user, request);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateEntityById(UUID id, @Validated(UpdateMethod.class) UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Username or email already exists");
                });

        mapRequestToEntity(user, request);
        return userRepository.save(user);
    }

    @Override
    public User findEntityById(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + uuid));
    }

    @Override
    @Transactional
    public void removeEntityById(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + uuid));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeEntitiesByIds(List<UUID> uuids) {
        List<User> users = userRepository.findAllById(uuids);
        users.forEach(user -> user.setEnabled(false));
        userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public void mapRequestToEntity(User entity, UserRequest request) {
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setUsername(request.getUsername());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setDob(request.getDob());
        entity.setEnabled(request.getEnabled());

        if (request.getGroupIds() != null) {
            entity.setGroups(request.getGroupIds().stream()
                    .map(groupId -> groupRepository.findById(groupId)
                            .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId)))
                    .collect(Collectors.toSet()));
        }
    }

    public User findEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }
}
