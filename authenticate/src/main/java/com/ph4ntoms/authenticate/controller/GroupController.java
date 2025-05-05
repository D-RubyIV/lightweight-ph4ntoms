package com.ph4ntoms.authenticate.controller;

import com.ph4ntoms.authenticate.mapper.GroupResponseMapper;
import com.ph4ntoms.authenticate.pageable.CustomPage;
import com.ph4ntoms.authenticate.pageable.PageableObject;
import com.ph4ntoms.authenticate.request.batch.BatchDeleteRequest;
import com.ph4ntoms.authenticate.request.entity.GroupRequest;
import com.ph4ntoms.authenticate.response.entity.GroupResponse;
import com.ph4ntoms.authenticate.service.entity.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupResponseMapper mapper;

    @PostMapping("/search")
    public ResponseEntity<?> searchGroups(@Valid @RequestBody PageableObject pageableObject) {
        return ResponseEntity.ok(new CustomPage<GroupResponse>(groupService.searchEntities(pageableObject).map(mapper::toDTO)));
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(mapper.toDTO(groupService.createEntity(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> updateGroup(@PathVariable UUID id, @Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(mapper.toDTO(groupService.updateEntityById(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDTO(groupService.findEntityById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        groupService.removeEntityById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteGroups(@Valid @RequestBody BatchDeleteRequest request) {
        groupService.removeEntitiesByIds(request.getIds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        return ResponseEntity.ok(groupService.findAllEntities().stream().map(mapper::toDTO).toList());
    }
} 