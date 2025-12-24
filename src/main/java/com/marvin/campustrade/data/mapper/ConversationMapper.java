package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.message.ConversationDTO;
import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Users;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(target = "id", source = "conversation.id")
    @Mapping(target = "product", source = "conversation.product")
    @Mapping(
            target = "userId",
            expression = "java(resolveChatPartner(conversation, currentUserId).getId())"
    )
    @Mapping(
            target = "username",
            expression = "java(resolveChatPartner(conversation, currentUserId).getFullName())"
    )
    @Mapping(target = "lastMessage", ignore = true)
    ConversationDTO toConversationDTO(
            Conversation conversation,
            @Context Long currentUserId
    );

    default Users resolveChatPartner(Conversation conversation, Long currentUserId) {
        if (conversation.getUser1().getId().equals(currentUserId)) {
            return conversation.getUser2();
        }
        return conversation.getUser1();
    }
}
