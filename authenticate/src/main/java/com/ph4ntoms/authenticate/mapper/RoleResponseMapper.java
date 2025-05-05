package com.ph4ntoms.authenticate.mapper;

import com.ph4ntoms.authenticate.model.Role;
import com.ph4ntoms.authenticate.response.entity.RoleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PermissionResponseMapper.class})
public interface RoleResponseMapper extends AMapperBasic<Role, RoleResponse> {
} 