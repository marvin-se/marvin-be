package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.message.ConversationDTO;
import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.data.entity.Conversation;
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
            expression = "java(resolveChatPartnerId(conversation, currentUserId))"
    )
    @Mapping(target = "lastMessage", ignore = true)
    ConversationDTO toConversationDTO(
            Conversation conversation,
            @Context Long currentUserId
    );

    default Long resolveChatPartnerId(Conversation conversation, Long currentUserId) {
        if (conversation.getUser1() != null
                && conversation.getUser1().getId().equals(currentUserId)) {
            return conversation.getUser2() != null
                    ? conversation.getUser2().getId()
                    : null;
        }
        return conversation.getUser1() != null
                ? conversation.getUser1().getId()
                : null;
    }
}
