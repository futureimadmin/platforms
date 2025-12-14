package com.nebula.shared.domain;

import com.google.cloud.firestore.annotation.PropertyName;

import java.time.Instant;
import java.util.Map;

/**
 * Firestore subdocument for Message persistence within Conversations
 */
public class MessageDocument {
    
    @PropertyName("id")
    private String id;
    
    @PropertyName("content")
    private String content;
    
    @PropertyName("role")
    private String role; // USER, AGENT, SYSTEM
    
    @PropertyName("senderId")
    private String senderId;
    
    @PropertyName("senderName")
    private String senderName;
    
    @PropertyName("timestamp")
    private Instant timestamp;
    
    @PropertyName("messageType")
    private String messageType; // TEXT, IMAGE, FILE, COMMAND, RESULT
    
    @PropertyName("parentMessageId")
    private String parentMessageId;
    
    @PropertyName("attachments")
    private java.util.List<AttachmentDocument> attachments;
    
    @PropertyName("metadata")
    private Map<String, Object> metadata;
    
    @PropertyName("isEdited")
    private Boolean isEdited;
    
    @PropertyName("editedAt")
    private Instant editedAt;
    
    @PropertyName("isDeleted")
    private Boolean isDeleted;
    
    @PropertyName("deletedAt")
    private Instant deletedAt;

    // Default constructor for Firestore
    public MessageDocument() {
        this.timestamp = Instant.now();
        this.isEdited = false;
        this.isDeleted = false;
        this.messageType = "TEXT";
    }

    // Constructor with required fields
    public MessageDocument(String content, String role, String senderId) {
        this();
        this.id = java.util.UUID.randomUUID().toString();
        this.content = content;
        this.role = role;
        this.senderId = senderId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if (this.timestamp != null) { // Only mark as edited if not a new message
            this.isEdited = true;
            this.editedAt = Instant.now();
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(String parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public java.util.List<AttachmentDocument> getAttachments() {
        return attachments;
    }

    public void setAttachments(java.util.List<AttachmentDocument> attachments) {
        this.attachments = attachments;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public Instant getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Instant editedAt) {
        this.editedAt = editedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        if (isDeleted) {
            this.deletedAt = Instant.now();
        }
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Utility methods
    public void addAttachment(AttachmentDocument attachment) {
        if (this.attachments == null) {
            this.attachments = new java.util.ArrayList<>();
        }
        this.attachments.add(attachment);
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "MessageDocument{" +
                "id='" + id + '\'' +
                ", role='" + role + '\'' +
                ", senderId='" + senderId + '\'' +
                ", messageType='" + messageType + '\'' +
                ", timestamp=" + timestamp +
                ", isDeleted=" + isDeleted +
                '}';
    }
}