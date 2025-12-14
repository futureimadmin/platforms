package com.nebula.controlplane.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nebula.shared.domain.ConversationDocument;
import com.nebula.shared.domain.MessageDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Repository for Conversation document operations in Firestore
 */
@Repository
public class ConversationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ConversationRepository.class);
    private static final String COLLECTION_NAME = "conversations";

    private final Firestore firestore;

    @Autowired
    public ConversationRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Save a conversation document
     */
    public ConversationDocument save(ConversationDocument conversation) throws ExecutionException, InterruptedException {
        logger.debug("Saving conversation: {}", conversation.getTitle());
        
        CollectionReference collection = firestore.collection(COLLECTION_NAME);
        DocumentReference docRef;
        
        if (conversation.getId() == null || conversation.getId().isEmpty()) {
            // Create new document with auto-generated ID
            docRef = collection.document();
            conversation.setId(docRef.getId());
        } else {
            // Update existing document
            docRef = collection.document(conversation.getId());
        }
        
        ApiFuture<WriteResult> future = docRef.set(conversation);
        WriteResult result = future.get();
        
        logger.debug("Conversation saved successfully with ID: {} at {}", conversation.getId(), result.getUpdateTime());
        return conversation;
    }

    /**
     * Find conversation by ID
     */
    public Optional<ConversationDocument> findById(String id) throws ExecutionException, InterruptedException {
        logger.debug("Finding conversation by ID: {}", id);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            ConversationDocument conversation = document.toObject(ConversationDocument.class);
            logger.debug("Found conversation: {}", conversation.getTitle());
            return Optional.of(conversation);
        } else {
            logger.debug("Conversation not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Find conversations by plan ID
     */
    public List<ConversationDocument> findByPlanId(String planId) throws ExecutionException, InterruptedException {
        logger.debug("Finding conversations by plan ID: {}", planId);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("planId", planId)
                .orderBy("lastMessageAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ConversationDocument> conversations = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            conversations.add(document.toObject(ConversationDocument.class));
        }
        
        logger.debug("Found {} conversations for plan ID: {}", conversations.size(), planId);
        return conversations;
    }

    /**
     * Find conversations by agent ID
     */
    public List<ConversationDocument> findByAgentId(String agentId) throws ExecutionException, InterruptedException {
        logger.debug("Finding conversations by agent ID: {}", agentId);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("agentId", agentId)
                .orderBy("lastMessageAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ConversationDocument> conversations = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            conversations.add(document.toObject(ConversationDocument.class));
        }
        
        logger.debug("Found {} conversations for agent ID: {}", conversations.size(), agentId);
        return conversations;
    }

    /**
     * Find conversations by user ID
     */
    public List<ConversationDocument> findByUserId(String userId) throws ExecutionException, InterruptedException {
        logger.debug("Finding conversations by user ID: {}", userId);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("lastMessageAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ConversationDocument> conversations = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            conversations.add(document.toObject(ConversationDocument.class));
        }
        
        logger.debug("Found {} conversations for user ID: {}", conversations.size(), userId);
        return conversations;
    }

    /**
     * Find active conversations
     */
    public List<ConversationDocument> findActiveConversations() throws ExecutionException, InterruptedException {
        logger.debug("Finding active conversations");
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", "ACTIVE")
                .orderBy("lastMessageAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ConversationDocument> conversations = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            conversations.add(document.toObject(ConversationDocument.class));
        }
        
        logger.debug("Found {} active conversations", conversations.size());
        return conversations;
    }

    /**
     * Find conversations by session ID
     */
    public List<ConversationDocument> findBySessionId(String sessionId) throws ExecutionException, InterruptedException {
        logger.debug("Finding conversations by session ID: {}", sessionId);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("sessionId", sessionId)
                .orderBy("createdAt", Query.Direction.ASCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ConversationDocument> conversations = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            conversations.add(document.toObject(ConversationDocument.class));
        }
        
        logger.debug("Found {} conversations for session ID: {}", conversations.size(), sessionId);
        return conversations;
    }

    /**
     * Add message to conversation
     */
    public void addMessage(String conversationId, MessageDocument message) throws ExecutionException, InterruptedException {
        logger.debug("Adding message to conversation: {}", conversationId);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(conversationId);
        ApiFuture<WriteResult> future = docRef.update(
                "messages", FieldValue.arrayUnion(message),
                "messageCount", FieldValue.increment(1),
                "lastMessageAt", Instant.now(),
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Message added to conversation at: {}", result.getUpdateTime());
    }

    /**
     * Update conversation status
     */
    public void updateStatus(String conversationId, String status) throws ExecutionException, InterruptedException {
        logger.debug("Updating conversation status: {} to {}", conversationId, status);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(conversationId);
        ApiFuture<WriteResult> future = docRef.update(
                "status", status,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Conversation status updated at: {}", result.getUpdateTime());
    }

    /**
     * Update conversation title
     */
    public void updateTitle(String conversationId, String title) throws ExecutionException, InterruptedException {
        logger.debug("Updating conversation title: {} to {}", conversationId, title);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(conversationId);
        ApiFuture<WriteResult> future = docRef.update(
                "title", title,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Conversation title updated at: {}", result.getUpdateTime());
    }

    /**
     * Update last message timestamp
     */
    public void updateLastMessageAt(String conversationId, Instant timestamp) throws ExecutionException, InterruptedException {
        logger.debug("Updating last message timestamp for conversation: {}", conversationId);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(conversationId);
        ApiFuture<WriteResult> future = docRef.update(
                "lastMessageAt", timestamp,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Last message timestamp updated at: {}", result.getUpdateTime());
    }

    /**
     * Delete conversation by ID
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        logger.debug("Deleting conversation by ID: {}", id);
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        WriteResult result = future.get();
        
        logger.debug("Conversation deleted at: {}", result.getUpdateTime());
    }

    /**
     * Archive conversation
     */
    public void archiveConversation(String conversationId) throws ExecutionException, InterruptedException {
        logger.debug("Archiving conversation: {}", conversationId);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(conversationId);
        ApiFuture<WriteResult> future = docRef.update(
                "status", "ARCHIVED",
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Conversation archived at: {}", result.getUpdateTime());
    }

    /**
     * Check if conversation exists
     */
    public boolean existsById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        return document.exists();
    }

    /**
     * Count total conversations
     */
    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = future.get();
        
        return querySnapshot.size();
    }

    /**
     * Count conversations by status
     */
    public long countByStatus(String status) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", status);
        
        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot querySnapshot = future.get();
        
        return querySnapshot.size();
    }

    /**
     * Count conversations by user ID
     */
    public long countByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId);
        
        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot querySnapshot = future.get();
        
        return querySnapshot.size();
    }
}