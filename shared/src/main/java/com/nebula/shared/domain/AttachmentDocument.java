package com.nebula.shared.domain;

import com.google.cloud.firestore.annotation.PropertyName;

import java.time.Instant;
import java.util.Map;

/**
 * Firestore subdocument for Attachment persistence within Messages
 */
public class AttachmentDocument {
    
    @PropertyName("id")
    private String id;
    
    @PropertyName("fileName")
    private String fileName;
    
    @PropertyName("originalFileName")
    private String originalFileName;
    
    @PropertyName("fileType")
    private String fileType;
    
    @PropertyName("mimeType")
    private String mimeType;
    
    @PropertyName("fileSize")
    private Long fileSize;
    
    @PropertyName("storageUrl")
    private String storageUrl;
    
    @PropertyName("storagePath")
    private String storagePath;
    
    @PropertyName("thumbnailUrl")
    private String thumbnailUrl;
    
    @PropertyName("uploadedAt")
    private Instant uploadedAt;
    
    @PropertyName("uploadedBy")
    private String uploadedBy;
    
    @PropertyName("description")
    private String description;
    
    @PropertyName("metadata")
    private Map<String, Object> metadata;
    
    @PropertyName("isProcessed")
    private Boolean isProcessed;
    
    @PropertyName("processedAt")
    private Instant processedAt;
    
    @PropertyName("processingStatus")
    private String processingStatus; // PENDING, PROCESSING, COMPLETED, FAILED

    // Default constructor for Firestore
    public AttachmentDocument() {
        this.id = java.util.UUID.randomUUID().toString();
        this.uploadedAt = Instant.now();
        this.isProcessed = false;
        this.processingStatus = "PENDING";
    }

    // Constructor with required fields
    public AttachmentDocument(String fileName, String fileType, Long fileSize, String uploadedBy) {
        this();
        this.fileName = fileName;
        this.originalFileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedBy = uploadedBy;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
        if (isProcessed) {
            this.processedAt = Instant.now();
            this.processingStatus = "COMPLETED";
        }
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
        if ("COMPLETED".equals(processingStatus)) {
            this.isProcessed = true;
            this.processedAt = Instant.now();
        }
    }

    // Utility methods
    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown";
        
        long bytes = fileSize;
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public boolean isDocument() {
        return mimeType != null && (
            mimeType.startsWith("application/pdf") ||
            mimeType.startsWith("application/msword") ||
            mimeType.startsWith("application/vnd.openxmlformats-officedocument") ||
            mimeType.startsWith("text/")
        );
    }

    @Override
    public String toString() {
        return "AttachmentDocument{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", processingStatus='" + processingStatus + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}