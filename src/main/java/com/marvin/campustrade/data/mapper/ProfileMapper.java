package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.user.ProfileResponse;
import com.marvin.campustrade.data.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(target = "universityId", source = "university.id")
    @Mapping(target = "universityName", source = "university.name")
    ProfileResponse toResponse(Users user);
}
