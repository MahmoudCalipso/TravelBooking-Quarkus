package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.response.chat.MessageResponse;
import com.travelplatform.domain.model.chat.ChatMessage;
import com.travelplatform.domain.model.chat.DirectMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * Mapper for Chat domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface ChatMapper {

    // ChatMessage to Response DTO
    MessageResponse toMessageResponse(ChatMessage chatMessage);

    // DirectMessage to Response DTO
    MessageResponse toMessageResponse(DirectMessage directMessage);

    java.util.List<MessageResponse> toMessageResponseList(java.util.List<ChatMessage> chatMessages);

    default java.util.List<MessageResponse> toDirectMessageResponseList(java.util.List<DirectMessage> directMessages) {
        if (directMessages == null) {
            return null;
        }

        java.util.List<MessageResponse> list = new java.util.ArrayList<MessageResponse>(directMessages.size());
        for (DirectMessage directMessage : directMessages) {
            list.add(toMessageResponse(directMessage));
        }

        return list;
    }

    @Named("senderId")
    default UUID mapSenderId(ChatMessage chatMessage) {
        return chatMessage != null ? chatMessage.getSenderId() : null;
    }

    @Named("senderId")
    default UUID mapSenderId(DirectMessage directMessage) {
        return directMessage != null ? directMessage.getSenderId() : null;
    }

    @Named("senderName")
    default String mapSenderName(ChatMessage chatMessage) {
        // TODO: Fix domain model navigation or fetch external data. Entity only has
        // senderId.
        return null;
        /*
         * return chatMessage != null && chatMessage.getSender() != null &&
         * chatMessage.getSender().getProfile() != null
         * ? chatMessage.getSender().getProfile().getFullName()
         * : null;
         */
    }

    @Named("senderName")
    default String mapSenderName(DirectMessage directMessage) {
        // TODO: Fix domain model navigation or fetch external data. Entity only has
        // senderId.
        return null;
        /*
         * return directMessage != null && directMessage.getSender() != null &&
         * directMessage.getSender().getProfile() != null
         * ? directMessage.getSender().getProfile().getFullName()
         * : null;
         */
    }

    @Named("senderPhotoUrl")
    default String mapSenderPhotoUrl(ChatMessage chatMessage) {
        // TODO: Fix domain model navigation or fetch external data. Entity only has
        // senderId.
        return null;
        /*
         * return chatMessage != null && chatMessage.getSender() != null &&
         * chatMessage.getSender().getProfile() != null
         * ? chatMessage.getSender().getProfile().getPhotoUrl()
         * : null;
         */
    }

    @Named("senderPhotoUrl")
    default String mapSenderPhotoUrl(DirectMessage directMessage) {
        // TODO: Fix domain model navigation or fetch external data. Entity only has
        // senderId.
        return null;
        /*
         * return directMessage != null && directMessage.getSender() != null &&
         * directMessage.getSender().getProfile() != null
         * ? directMessage.getSender().getProfile().getPhotoUrl()
         * : null;
         */
    }
}
