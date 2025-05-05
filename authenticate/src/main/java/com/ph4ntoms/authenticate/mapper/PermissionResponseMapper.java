package com.ph4ntoms.authenticate.mapper;

import com.ph4ntoms.authenticate.model.Permission;
import com.ph4ntoms.authenticate.response.entity.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionResponseMapper extends AMapperBasic<Permission, PermissionResponse> {
} 