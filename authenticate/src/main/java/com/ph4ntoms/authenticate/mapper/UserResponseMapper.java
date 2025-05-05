package com.ph4ntoms.authenticate.mapper;

import com.ph4ntoms.authenticate.model.Base;
import com.ph4ntoms.authenticate.model.Group;
import com.ph4ntoms.authenticate.model.User;
import com.ph4ntoms.authenticate.response.entity.UserResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserResponseMapper extends AMapperBasic<User, UserResponse> {
    @AfterMapping
    default void afterMapping(User user, @MappingTarget UserResponse userResponse) {
        Set<Group> groupShorts = user.getGroups().stream()
                .map(group -> {
                    Group dto = new Group();
                    dto.setId(group.getId());
                    dto.setName(group.getName());
                    dto.setCode(group.getCode());
                    dto.setEnabled(group.getEnabled());
                    return dto;
                })
                .collect(Collectors.toSet());

        userResponse.setGroups(groupShorts);
    }
}
