package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.message.MessageDTO;
import com.marvin.campustrade.data.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    MessageDTO toDTO(Message message);
}