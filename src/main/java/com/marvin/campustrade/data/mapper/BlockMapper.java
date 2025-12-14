package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.user.BlockResponse;
import com.marvin.campustrade.data.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface  BlockMapper {
    BlockResponse toBlock(Users user);
}
