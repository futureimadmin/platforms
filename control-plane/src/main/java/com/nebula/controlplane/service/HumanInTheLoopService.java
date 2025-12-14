package com.nebula.controlplane.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service for handling human-in-the-loop interactions
 */
@Service
public class HumanInTheLoopService {

    private static final Logger logger = LoggerFactory.getLogger(HumanInTheLoopService.class);

    // In-memory storage for approval requests
    private final Map<String, ApprovalRequest> approvalRequests = new ConcurrentHashMap<>();
    private final Map<String, Lock> requestLocks = new ConcurrentHashMap<>();
    private final Map<String, Condition> requestConditions = new ConcurrentHashMap<>();

    /**
     * Request human approval for a specific step
     * This method will block until approval is received or timeout occurs
     */
    public boolean requestApproval(String planId, String stepId, String description, Map<String, Object> context) {
        String requestId = generateRequestId(planId, stepId);
        logger.info("Requesting human approval for: {}", requestId);

        ApprovalRequest request = new ApprovalRequest();
        request.setRequestId(requestId);
        request.setPlanId(planId);
        request.setStepId(stepId);
        request.setDescription(description);
        request.setContext(context);
        request.setStatus("PENDING");

        // Store the request
        approvalRequests.put(requestId, request);

        // Set up locks and conditions for waiting
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        requestLocks.put(requestId, lock);
        requestConditions.put(requestId, condition);

        lock.lock();
        try {
            // Wait for approval
            logger.debug("Waiting for approval signal for: {}", requestId);
            boolean signaled = condition.await(5, TimeUnit.MINUTES); // 5 minute timeout

            if (!signaled) {
                logger.warn("Approval request timed out for: {}", requestId);
                request.setStatus("TIMED_OUT");
                return false;
            }

            // Check the approval status
            ApprovalRequest completedRequest = approvalRequests.get(requestId);
            boolean approved = "APPROVED".equals(completedRequest.getStatus());
            logger.info("Approval decision for {}: {}", requestId, approved ? "APPROVED" : "DENIED");
            return approved;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while waiting for approval for: {}", requestId, e);
            request.setStatus("ERROR");
            return false;
        } finally {
            lock.unlock();
            // Clean up
            approvalRequests.remove(requestId);
            requestLocks.remove(requestId);
            requestConditions.remove(requestId);
        }
    }

    /**
     * Provide approval decision for a pending request
     */
    public boolean provideApproval(String planId, String stepId, boolean approved) {
        String requestId = generateRequestId(planId, stepId);
        logger.info("Providing approval decision for {}: {}", requestId, approved);

        ApprovalRequest request = approvalRequests.get(requestId);
        if (request == null) {
            logger.warn("No pending approval request found for: {}", requestId);
            return false;
        }

        // Update request status
        request.setStatus(approved ? "APPROVED" : "DENIED");

        // Signal the waiting thread
        Lock lock = requestLocks.get(requestId);
        Condition condition = requestConditions.get(requestId);

        if (lock != null && condition != null) {
            lock.lock();
            try {
                logger.debug("Signaling approval for: {}", requestId);
                condition.signal();
            } finally {
                lock.unlock();
            }
            return true;
        } else {
            logger.error("Could not find lock or condition for: {}", requestId);
            return false;
        }
    }
    
    /**
     * Get a pending approval request
     */
    public ApprovalRequest getPendingRequest(String planId, String stepId) {
        String requestId = generateRequestId(planId, stepId);
        return approvalRequests.get(requestId);
    }

    private String generateRequestId(String planId, String stepId) {
        return planId + ":" + stepId;
    }

    /**
     * Inner class for approval request
     */
    public static class ApprovalRequest {
        private String requestId;
        private String planId;
        private String stepId;
        private String description;
        private Map<String, Object> context;
        private String status; // PENDING, APPROVED, DENIED, TIMED_OUT, ERROR

        // Getters and setters
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }

        public String getPlanId() { return planId; }
        public void setPlanId(String planId) { this.planId = planId; }

        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}