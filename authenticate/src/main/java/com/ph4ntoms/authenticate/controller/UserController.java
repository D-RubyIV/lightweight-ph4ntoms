package com.ph4ntoms.authenticate.controller;

import com.ph4ntoms.authenticate.pageable.CustomPage;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.request.entity.UserRequest;
import com.ph4ntoms.authenticate.model.User;
import com.ph4ntoms.authenticate.service.entity.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/search")
    public ResponseEntity<?> searchUsers(@Valid @RequestBody PageableObject pageableObject) {
        System.out.println(pageableObject);
        return ResponseEntity.ok(new CustomPage<User>(userService.searchEntities(pageableObject)));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createEntity(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateEntityById(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findEntityById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.removeEntityById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteUsers(@RequestBody List<UUID> ids) {
        userService.removeEntitiesByIds(ids);
        return ResponseEntity.noContent().build();
    }
}
