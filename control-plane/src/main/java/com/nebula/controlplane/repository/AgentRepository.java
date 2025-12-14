package com.nebula.controlplane.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nebula.shared.domain.AgentDocument;
import com.nebula.shared.enums.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Repository for Agent document operations in Firestore
 */
@Repository
public class AgentRepository {

    private static final Logger logger = LoggerFactory.getLogger(AgentRepository.class);
    private static final String COLLECTION_NAME = "agents";

    private final Firestore firestore;

    @Autowired
    public AgentRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Save an agent document
     */
    public AgentDocument save(AgentDocument agent) throws ExecutionException, InterruptedException {
        logger.debug("Saving agent: {}", agent.getName());
        
        CollectionReference collection = firestore.collection(COLLECTION_NAME);
        DocumentReference docRef;
        
        if (agent.getId() == null || agent.getId().isEmpty()) {
            // Create new document with auto-generated ID
            docRef = collection.document();
            agent.setId(docRef.getId());
        } else {
            // Update existing document
            docRef = collection.document(agent.getId());
        }
        
        ApiFuture<WriteResult> future = docRef.set(agent);
        WriteResult result = future.get();
        
        logger.debug("Agent saved successfully with ID: {} at {}", agent.getId(), result.getUpdateTime());
        return agent;
    }

    /**
     * Find agent by ID
     */
    public Optional<AgentDocument> findById(String id) throws ExecutionException, InterruptedException {
        logger.debug("Finding agent by ID: {}", id);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            AgentDocument agent = document.toObject(AgentDocument.class);
            logger.debug("Found agent: {}", agent.getName());
            return Optional.of(agent);
        } else {
            logger.debug("Agent not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Find all agents
     */
    public List<AgentDocument> findAll() throws ExecutionException, InterruptedException {
        logger.debug("Finding all agents");
        
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<AgentDocument> agents = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            agents.add(document.toObject(AgentDocument.class));
        }
        
        logger.debug("Found {} agents", agents.size());
        return agents;
    }

    /**
     * Find agents by plan ID
     */
    public List<AgentDocument> findByPlanId(String planId) throws ExecutionException, InterruptedException {
        logger.debug("Finding agents by plan ID: {}", planId);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("planId", planId);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<AgentDocument> agents = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            agents.add(document.toObject(AgentDocument.class));
        }
        
        logger.debug("Found {} agents for plan ID: {}", agents.size(), planId);
        return agents;
    }

    /**
     * Find agents by type
     */
    public List<AgentDocument> findByType(AgentType type) throws ExecutionException, InterruptedException {
        logger.debug("Finding agents by type: {}", type);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("type", type);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<AgentDocument> agents = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            agents.add(document.toObject(AgentDocument.class));
        }
        
        logger.debug("Found {} agents of type: {}", agents.size(), type);
        return agents;
    }

    /**
     * Find agents by status
     */
    public List<AgentDocument> findByStatus(String status) throws ExecutionException, InterruptedException {
        logger.debug("Finding agents by status: {}", status);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", status);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<AgentDocument> agents = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            agents.add(document.toObject(AgentDocument.class));
        }
        
        logger.debug("Found {} agents with status: {}", agents.size(), status);
        return agents;
    }

    /**
     * Find active agents for a plan
     */
    public List<AgentDocument> findActiveAgentsByPlanId(String planId) throws ExecutionException, InterruptedException {
        logger.debug("Finding active agents for plan ID: {}", planId);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("planId", planId)
                .whereIn("status", List.of("ACTIVE", "RUNNING", "BUSY"));
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<AgentDocument> agents = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            agents.add(document.toObject(AgentDocument.class));
        }
        
        logger.debug("Found {} active agents for plan ID: {}", agents.size(), planId);
        return agents;
    }

    /**
     * Update agent status
     */
    public void updateStatus(String agentId, String status) throws ExecutionException, InterruptedException {
        logger.debug("Updating agent status: {} to {}", agentId, status);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(agentId);
        ApiFuture<WriteResult> future = docRef.update("status", status, "updatedAt", java.time.Instant.now());
        WriteResult result = future.get();
        
        logger.debug("Agent status updated at: {}", result.getUpdateTime());
    }

    /**
     * Increment execution count for an agent
     */
    public void incrementExecutionCount(String agentId) throws ExecutionException, InterruptedException {
        logger.debug("Incrementing execution count for agent: {}", agentId);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(agentId);
        ApiFuture<WriteResult> future = docRef.update(
                "executionCount", FieldValue.increment(1),
                "lastActiveAt", java.time.Instant.now(),
                "updatedAt", java.time.Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Agent execution count incremented at: {}", result.getUpdateTime());
    }

    /**
     * Delete agent by ID
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        logger.debug("Deleting agent by ID: {}", id);
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        WriteResult result = future.get();
        
        logger.debug("Agent deleted at: {}", result.getUpdateTime());
    }

    /**
     * Check if agent exists
     */
    public boolean existsById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        return document.exists();
    }

    /**
     * Count total agents
     */
    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = future.get();
        
        return querySnapshot.size();
    }

    /**
     * Count agents by plan ID
     */
    public long countByPlanId(String planId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("planId", planId);
        
        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot querySnapshot = future.get();
        
        return querySnapshot.size();
    }
}