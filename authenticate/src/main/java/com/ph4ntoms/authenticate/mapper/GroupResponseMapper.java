package com.ph4ntoms.authenticate.mapper;

import com.ph4ntoms.authenticate.model.Group;
import com.ph4ntoms.authenticate.model.Role;
import com.ph4ntoms.authenticate.model.User;
import com.ph4ntoms.authenticate.response.entity.GroupResponse;
import com.ph4ntoms.authenticate.response.entity.UserResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RoleResponseMapper.class})
public interface GroupResponseMapper extends AMapperBasic<Group, GroupResponse> {
    @AfterMapping
    default void afterMapping(Group group, @MappingTarget GroupResponse groupResponse) {
        Set<Role> roleShorts = group.getRoles().stream()
                .map(role -> {
                    Role dto = new Role();
                    dto.setId(role.getId());
                    dto.setName(role.getName());
                    dto.setCode(role.getCode());
                    dto.setEnabled(role.getEnabled());
                    return dto;
                })
                .collect(Collectors.toSet());

        groupResponse.setRoles(roleShorts);
    }
} 