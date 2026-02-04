package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.dto.response.chat.MessageResponse;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.service.chat.ChatService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * REST controller for chat operations.
 * Handles group chats and direct messages.
 */
@Path("/api/v1/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Chat", description = "Chat and messaging")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Inject
    private ChatService chatService;

    /**
     * Create chat group.
     *
     * @param securityContext The security context
     * @param name The group name
     * @param referenceType The reference type (EVENT, TRAVEL_PROGRAM, CUSTOM)
     * @param referenceId The reference ID
     * @return Success response
     */
    @POST
    @Path("/groups")
    @Authenticated
    @RolesAllowed({"SUPPLIER_SUBSCRIBER", "ASSOCIATION_MANAGER"})
    @Operation(summary = "Create chat group", description = "Create a new chat group")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Chat group created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid input"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response createChatGroup(
            @Context SecurityContext securityContext,
            @FormParam("name") String name,
            @FormParam("referenceType") String referenceType,
            @FormParam("referenceId") UUID referenceId) {
        try {
            String creatorId = securityContext.getUserPrincipal().getName();
            log.info("Create chat group request by user: {}", creatorId);
            
            UUID groupId = chatService.createChatGroup(
                    UUID.fromString(creatorId), name, referenceType, referenceId);
            
            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse<>(groupId, "Chat group created successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Chat group creation failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error creating chat group", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get my chat groups.
     *
     * @param securityContext The security context
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of chat groups
     */
    @GET
    @Path("/groups")
    @Authenticated
    @Operation(summary = "Get my chat groups", description = "Get current user's chat groups")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Chat groups retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getMyChatGroups(
            @Context SecurityContext securityContext,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get my chat groups request for user: {}", userId);
            
            var groups = chatService.getUserChatGroups(UUID.fromString(userId), page, pageSize);
            PageResponse<?> response = buildPageResponse(groups, page, pageSize);
            
            return Response.ok()
                    .entity(response)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting chat groups", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get chat group messages.
     *
     * @param securityContext The security context
     * @param groupId The group ID
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of messages
     */
    @GET
    @Path("/groups/{groupId}/messages")
    @Authenticated
    @Operation(summary = "Get chat group messages", description = "Get messages from a chat group")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Messages retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Chat group not found")
    })
    public Response getChatGroupMessages(
            @Context SecurityContext securityContext,
            @PathParam("groupId") UUID groupId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get chat group messages request for group: {} by user: {}", groupId, userId);
            
            var messages = chatService.getChatGroupMessages(groupId, page, pageSize);
            PageResponse<MessageResponse> response = buildPageResponse(messages, page, pageSize);
            
            return Response.ok()
                    .entity(response)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Chat group not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("GROUP_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting chat group messages", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Send group message.
     *
     * @param securityContext The security context
     * @param groupId The group ID
     * @param message The message content
     * @param messageType The message type (TEXT, IMAGE, FILE, LOCATION)
     * @param attachmentUrl The attachment URL
     * @return Success response
     */
    @POST
    @Path("/groups/{groupId}/messages")
    @Authenticated
    @Operation(summary = "Send group message", description = "Send a message to a chat group")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Message sent successfully"),
        @APIResponse(responseCode = "400", description = "Invalid input"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Chat group not found")
    })
    public Response sendGroupMessage(
            @Context SecurityContext securityContext,
            @PathParam("groupId") UUID groupId,
            @FormParam("message") String message,
            @FormParam("messageType") String messageType,
            @FormParam("attachmentUrl") String attachmentUrl) {
        try {
            String senderId = securityContext.getUserPrincipal().getName();
            log.info("Send group message request to group: {} by user: {}", groupId, senderId);
            
            MessageResponse messageResponse = chatService.sendGroupMessage(
                    UUID.fromString(senderId), groupId, message, messageType, attachmentUrl);
            
            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse<>(messageResponse, "Message sent successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Message sending failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error sending group message", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Add member to chat group.
     *
     * @param securityContext The security context
     * @param groupId The group ID
     * @param userId The user ID to add
     * @return Success response
     */
    @POST
    @Path("/groups/{groupId}/members")
    @Authenticated
    @RolesAllowed({"SUPPLIER_SUBSCRIBER", "ASSOCIATION_MANAGER"})
    @Operation(summary = "Add member to group", description = "Add a member to a chat group")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Member added successfully"),
        @APIResponse(responseCode = "400", description = "Invalid input"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Chat group not found")
    })
    public Response addGroupMember(
            @Context SecurityContext securityContext,
            @PathParam("groupId") UUID groupId,
            @FormParam("userId") UUID userId) {
        try {
            String creatorId = securityContext.getUserPrincipal().getName();
            log.info("Add member to group request: {} by user: {}", groupId, creatorId);
            
            chatService.addGroupMember(groupId, userId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Member added successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Member addition failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error adding group member", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Remove member from chat group.
     *
     * @param securityContext The security context
     * @param groupId The group ID
     * @param userId The user ID to remove
     * @return Success response
     */
    @DELETE
    @Path("/groups/{groupId}/members/{userId}")
    @Authenticated
    @RolesAllowed({"SUPPLIER_SUBSCRIBER", "ASSOCIATION_MANAGER"})
    @Operation(summary = "Remove member from group", description = "Remove a member from a chat group")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Member removed successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Chat group not found")
    })
    public Response removeGroupMember(
            @Context SecurityContext securityContext,
            @PathParam("groupId") UUID groupId,
            @PathParam("userId") UUID userId) {
        try {
            String creatorId = securityContext.getUserPrincipal().getName();
            log.info("Remove member from group request: {} by user: {}", groupId, creatorId);
            
            chatService.removeGroupMember(groupId, userId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Member removed successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error removing group member", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get conversations.
     *
     * @param securityContext The security context
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of conversations
     */
    @GET
    @Path("/conversations")
    @Authenticated
    @Operation(summary = "Get conversations", description = "Get current user's direct message conversations")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Conversations retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getConversations(
            @Context SecurityContext securityContext,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get conversations request for user: {}", userId);
            
            var conversations = chatService.getUserConversations(UUID.fromString(userId), page, pageSize);
            PageResponse<?> response = buildPageResponse(conversations, page, pageSize);
            
            return Response.ok()
                    .entity(response)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting conversations", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get conversation messages.
     *
     * @param securityContext The security context
     * @param conversationId The conversation ID
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of messages
     */
    @GET
    @Path("/conversations/{conversationId}/messages")
    @Authenticated
    @Operation(summary = "Get conversation messages", description = "Get messages from a conversation")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Messages retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Conversation not found")
    })
    public Response getConversationMessages(
            @Context SecurityContext securityContext,
            @PathParam("conversationId") UUID conversationId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get conversation messages request for conversation: {} by user: {}", conversationId, userId);
            
            var messages = chatService.getConversationMessages(conversationId, page, pageSize);
            PageResponse<MessageResponse> response = buildPageResponse(messages, page, pageSize);
            
            return Response.ok()
                    .entity(response)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Conversation not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("CONVERSATION_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting conversation messages", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Send direct message.
     *
     * @param securityContext The security context
     * @param recipientId The recipient user ID
     * @param message The message content
     * @return Success response
     */
    @POST
    @Path("/messages/send")
    @Authenticated
    @Operation(summary = "Send direct message", description = "Send a direct message to a user")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Message sent successfully"),
        @APIResponse(responseCode = "400", description = "Invalid input"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response sendDirectMessage(
            @Context SecurityContext securityContext,
            @FormParam("recipientId") UUID recipientId,
            @FormParam("message") String message) {
        try {
            String senderId = securityContext.getUserPrincipal().getName();
            log.info("Send direct message request from: {} to: {}", senderId, recipientId);
            
            UUID conversationId = chatService.getOrCreateConversation(UUID.fromString(senderId), recipientId);
            MessageResponse messageResponse = chatService.sendDirectMessage(UUID.fromString(senderId), conversationId, message);
            
            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse<>(messageResponse, "Message sent successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Direct message sending failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error sending direct message", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Mark messages as read.
     *
     * @param securityContext The security context
     * @param conversationId The conversation ID
     * @return Success response
     */
    @PUT
    @Path("/conversations/{conversationId}/read")
    @Authenticated
    @Operation(summary = "Mark messages as read", description = "Mark all messages in a conversation as read")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Messages marked as read successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Conversation not found")
    })
    public Response markMessagesAsRead(
            @Context SecurityContext securityContext,
            @PathParam("conversationId") UUID conversationId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Mark messages as read request for conversation: {} by user: {}", conversationId, userId);
            
            chatService.markMessagesAsRead(UUID.fromString(userId), conversationId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Messages marked as read successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Conversation not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("CONVERSATION_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error marking messages as read", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get unread message count.
     *
     * @param securityContext The security context
     * @return Unread count response
     */
    @GET
    @Path("/unread-count")
    @Authenticated
    @Operation(summary = "Get unread message count", description = "Get current user's unread message count")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Unread count retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getUnreadCount(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get unread count request for user: {}", userId);
            
            int unreadCount = chatService.getUnreadMessageCount(UUID.fromString(userId));
            
            return Response.ok()
                    .entity(new SuccessResponse<>(unreadCount, "Unread count retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting unread count", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Delete conversation.
     *
     * @param securityContext The security context
     * @param conversationId The conversation ID
     * @return Success response
     */
    @DELETE
    @Path("/conversations/{conversationId}")
    @Authenticated
    @Operation(summary = "Delete conversation", description = "Delete a conversation")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Conversation deleted successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Conversation not found")
    })
    public Response deleteConversation(
            @Context SecurityContext securityContext,
            @PathParam("conversationId") UUID conversationId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Delete conversation request: {} by user: {}", conversationId, userId);
            
            chatService.deleteConversation(UUID.fromString(userId), conversationId);
            
            return Response.noContent().build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Conversation deletion failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("DELETION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error deleting conversation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    private <T> PageResponse<T> buildPageResponse(java.util.List<T> data, int page, int pageSize) {
        PageResponse.PaginationInfo pagination =
                new PageResponse.PaginationInfo(page, pageSize, (long) data.size());
        return new PageResponse<>(data, pagination);
    }
}
