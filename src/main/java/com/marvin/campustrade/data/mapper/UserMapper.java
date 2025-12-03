package com.marvin.campustrade.data.mapper;
import com.marvin.campustrade.data.dto.auth.RegisterRequest;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "university", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "profilePicUrl", ignore = true)
    Users toEntity(RegisterRequest request);

    UserResponse toResponse(Users user);

}
