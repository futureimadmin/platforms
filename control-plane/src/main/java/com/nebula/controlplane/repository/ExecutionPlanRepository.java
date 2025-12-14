package com.nebula.controlplane.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.nebula.shared.domain.ExecutionPlanDocument;
import com.nebula.shared.enums.ExecutionStatus;
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
 * Repository for ExecutionPlan document operations in Firestore
 */
@Repository
public class ExecutionPlanRepository {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionPlanRepository.class);
    private static final String COLLECTION_NAME = "execution_plans";

    private final Firestore firestore;

    @Autowired
    public ExecutionPlanRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Save an execution plan document
     */
    public ExecutionPlanDocument save(ExecutionPlanDocument plan) throws ExecutionException, InterruptedException {
        logger.debug("Saving execution plan: {}", plan.getName());
        
        CollectionReference collection = firestore.collection(COLLECTION_NAME);
        DocumentReference docRef;
        
        if (plan.getId() == null || plan.getId().isEmpty()) {
            // Create new document with auto-generated ID
            docRef = collection.document();
            plan.setId(docRef.getId());
        } else {
            // Update existing document
            docRef = collection.document(plan.getId());
        }
        
        ApiFuture<WriteResult> future = docRef.set(plan);
        WriteResult result = future.get();
        
        logger.debug("Execution plan saved successfully with ID: {} at {}", plan.getId(), result.getUpdateTime());
        return plan;
    }

    /**
     * Find execution plan by ID
     */
    public Optional<ExecutionPlanDocument> findById(String id) throws ExecutionException, InterruptedException {
        logger.debug("Finding execution plan by ID: {}", id);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            ExecutionPlanDocument plan = document.toObject(ExecutionPlanDocument.class);
            logger.debug("Found execution plan: {}", plan.getName());
            return Optional.of(plan);
        } else {
            logger.debug("Execution plan not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Find all execution plans
     */
    public List<ExecutionPlanDocument> findAll() throws ExecutionException, InterruptedException {
        logger.debug("Finding all execution plans");
        
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ExecutionPlanDocument> plans = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            plans.add(document.toObject(ExecutionPlanDocument.class));
        }
        
        logger.debug("Found {} execution plans", plans.size());
        return plans;
    }

    /**
     * Find execution plans by status
     */
    public List<ExecutionPlanDocument> findByStatus(ExecutionStatus status) throws ExecutionException, InterruptedException {
        logger.debug("Finding execution plans by status: {}", status);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", status)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ExecutionPlanDocument> plans = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            plans.add(document.toObject(ExecutionPlanDocument.class));
        }
        
        logger.debug("Found {} execution plans with status: {}", plans.size(), status);
        return plans;
    }

    /**
     * Find execution plans by created by user
     */
    public List<ExecutionPlanDocument> findByCreatedBy(String createdBy) throws ExecutionException, InterruptedException {
        logger.debug("Finding execution plans by created by: {}", createdBy);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("createdBy", createdBy)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ExecutionPlanDocument> plans = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            plans.add(document.toObject(ExecutionPlanDocument.class));
        }
        
        logger.debug("Found {} execution plans created by: {}", plans.size(), createdBy);
        return plans;
    }

    /**
     * Find active execution plans
     */
    public List<ExecutionPlanDocument> findActivePlans() throws ExecutionException, InterruptedException {
        logger.debug("Finding active execution plans");
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereIn("status", List.of(ExecutionStatus.PENDING, ExecutionStatus.RUNNING))
                .orderBy("priority", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.ASCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ExecutionPlanDocument> plans = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            plans.add(document.toObject(ExecutionPlanDocument.class));
        }
        
        logger.debug("Found {} active execution plans", plans.size());
        return plans;
    }

    /**
     * Find execution plans created within a time range
     */
    public List<ExecutionPlanDocument> findByCreatedAtBetween(Instant startTime, Instant endTime) 
            throws ExecutionException, InterruptedException {
        logger.debug("Finding execution plans created between {} and {}", startTime, endTime);
        
        Query query = firestore.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("createdAt", startTime)
                .whereLessThanOrEqualTo("createdAt", endTime)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        
        List<ExecutionPlanDocument> plans = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            plans.add(document.toObject(ExecutionPlanDocument.class));
        }
        
        logger.debug("Found {} execution plans in time range", plans.size());
        return plans;
    }

    /**
     * Update execution plan status
     */
    public void updateStatus(String planId, ExecutionStatus status) throws ExecutionException, InterruptedException {
        logger.debug("Updating execution plan status: {} to {}", planId, status);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(planId);
        ApiFuture<WriteResult> future = docRef.update(
                "status", status,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Execution plan status updated at: {}", result.getUpdateTime());
    }

    /**
     * Update execution plan progress
     */
    public void updateProgress(String planId, int completedSteps, int currentStepIndex) 
            throws ExecutionException, InterruptedException {
        logger.debug("Updating execution plan progress: {} - completed: {}, current: {}", 
                planId, completedSteps, currentStepIndex);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(planId);
        ApiFuture<WriteResult> future = docRef.update(
                "completedSteps", completedSteps,
                "currentStepIndex", currentStepIndex,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Execution plan progress updated at: {}", result.getUpdateTime());
    }

    /**
     * Update active agents count
     */
    public void updateActiveAgentsCount(String planId, int activeAgents) 
            throws ExecutionException, InterruptedException {
        logger.debug("Updating active agents count for plan: {} to {}", planId, activeAgents);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(planId);
        ApiFuture<WriteResult> future = docRef.update(
                "activeAgents", activeAgents,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Active agents count updated at: {}", result.getUpdateTime());
    }

    /**
     * Set execution plan start time
     */
    public void setStartTime(String planId, Instant startTime) throws ExecutionException, InterruptedException {
        logger.debug("Setting start time for execution plan: {} to {}", planId, startTime);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(planId);
        ApiFuture<WriteResult> future = docRef.update(
                "startTime", startTime,
                "status", ExecutionStatus.RUNNING,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Execution plan start time set at: {}", result.getUpdateTime());
    }

    /**
     * Set execution plan end time
     */
    public void setEndTime(String planId, Instant endTime) throws ExecutionException, InterruptedException {
        logger.debug("Setting end time for execution plan: {} to {}", planId, endTime);
        
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(planId);
        ApiFuture<WriteResult> future = docRef.update(
                "endTime", endTime,
                "updatedAt", Instant.now()
        );
        WriteResult result = future.get();
        
        logger.debug("Execution plan end time set at: {}", result.getUpdateTime());
    }

    /**
     * Delete execution plan by ID
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        logger.debug("Deleting execution plan by ID: {}", id);
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(id).delete();
        WriteResult result = future.get();
        
        logger.debug("Execution plan deleted at: {}", result.getUpdateTime());
    }

    /**
     * Check if execution plan exists
     */
    public boolean existsById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        return document.exists();
    }

    /**
     * Count total execution plans
     */
    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = future.get();
        
        return querySnapshot.size();
    }

    /**
     * Count execution plans by status
     */
    public long countByStatus(ExecutionStatus status) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", status);
        
        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot querySnapshot = future.get();
        
        return querySnapshot.size();
    }
}