package com.marvin.campustrade.data.mapper;
import com.marvin.campustrade.data.dto.auth.RegisterRequest;
import com.marvin.campustrade.data.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "university", ignore = true)
    Users toEntity(RegisterRequest request);
}
