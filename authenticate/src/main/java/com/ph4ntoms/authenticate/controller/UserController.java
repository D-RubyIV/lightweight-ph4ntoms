package com.ph4ntoms.authenticate.controller;

import com.ph4ntoms.authenticate.group.CreateMethod;
import com.ph4ntoms.authenticate.group.UpdateMethod;
import com.ph4ntoms.authenticate.mapper.UserResponseMapper;
import com.ph4ntoms.authenticate.pageable.CustomPage;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.request.batch.BatchDeleteRequest;
import com.ph4ntoms.authenticate.request.entity.UserRequest;
import com.ph4ntoms.authenticate.response.entity.UserResponse;
import com.ph4ntoms.authenticate.service.entity.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserResponseMapper mapper;

    @PostMapping("/search")
    public ResponseEntity<?> searchUsers(@Valid @RequestBody PageableObject pageableObject) {
        System.out.println(pageableObject);
        return ResponseEntity.ok(new CustomPage<UserResponse>(userService.searchEntities(pageableObject).map(mapper::toDTO)));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Validated(CreateMethod.class) @RequestBody UserRequest request) {
        return ResponseEntity.ok(mapper.toDTO(userService.createEntity(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Validated(UpdateMethod.class) @RequestBody UserRequest request) {
        return ResponseEntity.ok(mapper.toDTO(userService.updateEntityById(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(userService.findEntityById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.removeEntityById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteUsers(@Valid @RequestBody BatchDeleteRequest request) {
        userService.removeEntitiesByIds(request.getIds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllEntities().stream().map(mapper::toDTO).toList());
    }
}
