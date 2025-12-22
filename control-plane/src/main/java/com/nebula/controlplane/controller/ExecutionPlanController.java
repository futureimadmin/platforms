package com.nebula.controlplane.controller;

import com.nebula.controlplane.service.ExecutionPlanService;
import com.nebula.shared.domain.ExecutionPlanDocument;
import com.nebula.shared.model.ExecutionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST Controller for Execution Plan management.
 * Provides endpoints for:
 * 1. Fetching execution plans from Firestore
 * 2. Managing execution plan lifecycle
 * 3. Retrieving execution plan details
 */
@RestController
@RequestMapping("/api/v1/execution-plans")
@CrossOrigin(origins = "*")
public class ExecutionPlanController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutionPlanController.class);
    
    @Autowired
    private ExecutionPlanService executionPlanService;
    
    /**
     * Get all execution plans
     */
    @GetMapping
    public ResponseEntity<List<ExecutionPlan>> getAllExecutionPlans() {
        logger.info("Fetching all execution plans");
        
        try {
            List<ExecutionPlan> plans = executionPlanService.getAllExecutionPlans();
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            logger.error("Error fetching execution plans", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get execution plan by ID
     */
    @GetMapping("/{planId}")
    public ResponseEntity<ExecutionPlan> getExecutionPlan(@PathVariable String planId) {
        logger.info("Fetching execution plan: {}", planId);
        
        try {
            ExecutionPlan plan = executionPlanService.getExecutionPlan(planId);
            if (plan != null) {
                return ResponseEntity.ok(plan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching execution plan: {}", planId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Create a new execution plan
     */
    @PostMapping
    public ResponseEntity<ExecutionPlanDocument> createExecutionPlan(@RequestBody ExecutionPlan plan) {
        logger.info("Creating execution plan: {}", plan.getMetadata().getName());
        
        try {
            if (!executionPlanService.validateExecutionPlan(plan)) {
                return ResponseEntity.badRequest().build();
            }
            
            ExecutionPlanDocument document = executionPlanService.persistExecutionPlan(plan).block();
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            logger.error("Error creating execution plan", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Update an existing execution plan
     */
    @PutMapping("/{planId}")
    public ResponseEntity<ExecutionPlan> updateExecutionPlan(
            @PathVariable String planId, 
            @RequestBody ExecutionPlan plan) {
        logger.info("Updating execution plan: {}", planId);
        
        try {
            if (!executionPlanService.validateExecutionPlan(plan)) {
                return ResponseEntity.badRequest().build();
            }
            
            ExecutionPlan updatedPlan = executionPlanService.updateExecutionPlan(planId, plan);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            logger.error("Error updating execution plan: {}", planId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Delete an execution plan
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse> deleteExecutionPlan(@PathVariable String planId) {
        logger.info("Deleting execution plan: {}", planId);
        
        try {
            boolean deleted = executionPlanService.deleteExecutionPlan(planId);
            
            ApiResponse response = new ApiResponse();
            if (deleted) {
                response.setSuccess(true);
                response.setMessage("Execution plan deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.setSuccess(false);
                response.setMessage("Execution plan not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting execution plan: {}", planId, e);
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage("Error deleting execution plan: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Execute an execution plan
     */
    @PostMapping("/{planId}/execute")
    public ResponseEntity<ApiResponse> executeExecutionPlan(@PathVariable String planId) {
        logger.info("Executing execution plan: {}", planId);
        
        try {
            // For now, we'll just return a success response
            // In the future, this would trigger the actual execution
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage("Execution plan queued for execution");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error executing execution plan: {}", planId, e);
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage("Error executing execution plan: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    

    
    // Response DTOs
    
    public static class ApiResponse {
        private boolean success;
        private String message;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    

}