package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.response.chat.MessageResponse;
import com.travelplatform.domain.model.chat.ChatMessage;
import com.travelplatform.domain.model.chat.DirectMessage;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for Chat domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface ChatMapper {

    default MessageResponse toMessageResponse(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        MessageResponse response = new MessageResponse();
        response.setId(chatMessage.getId());
        response.setSenderId(chatMessage.getSenderId());
        response.setSenderName(null);
        response.setSenderPhotoUrl(null);
        response.setMessage(chatMessage.getMessage());
        response.setMessageType(chatMessage.getMessageType() != null ? chatMessage.getMessageType().name() : null);
        response.setAttachmentUrl(chatMessage.getAttachmentUrl());
        response.setCreatedAt(chatMessage.getCreatedAt());
        return response;
    }

    default MessageResponse toMessageResponse(DirectMessage directMessage) {
        if (directMessage == null) {
            return null;
        }
        MessageResponse response = new MessageResponse();
        response.setId(directMessage.getId());
        response.setSenderId(directMessage.getSenderId());
        response.setSenderName(null);
        response.setSenderPhotoUrl(null);
        response.setMessage(directMessage.getMessage());
        response.setMessageType("DIRECT");
        response.setAttachmentUrl(directMessage.getAttachmentUrl());
        response.setCreatedAt(directMessage.getCreatedAt());
        return response;
    }

    default List<MessageResponse> toMessageResponseList(List<ChatMessage> chatMessages) {
        if (chatMessages == null) {
            return null;
        }
        List<MessageResponse> responses = new ArrayList<>(chatMessages.size());
        for (ChatMessage message : chatMessages) {
            responses.add(toMessageResponse(message));
        }
        return responses;
    }

    default List<MessageResponse> toDirectMessageResponseList(List<DirectMessage> directMessages) {
        if (directMessages == null) {
            return null;
        }
        List<MessageResponse> responses = new ArrayList<>(directMessages.size());
        for (DirectMessage directMessage : directMessages) {
            responses.add(toMessageResponse(directMessage));
        }
        return responses;
    }
}
