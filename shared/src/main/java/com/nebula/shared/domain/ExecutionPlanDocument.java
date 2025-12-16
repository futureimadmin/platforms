package com.nebula.shared.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.nebula.shared.model.ExecutionPlan;

import java.time.Instant;
import java.util.Map;

/**
 * Firestore document wrapper for ExecutionPlan that stores the plan as raw JSON
 */
public class ExecutionPlanDocument {

    @DocumentId
    private String id;

    @PropertyName("planId")
    private String planId;

    @PropertyName("planData")
    private Map<String, Object> planData;

    @PropertyName("createdAt")
    private Instant createdAt;

    @PropertyName("updatedAt")
    private Instant updatedAt;

    // Constructors
    public ExecutionPlanDocument() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Factory method to create from ExecutionPlan
    public static ExecutionPlanDocument from(ExecutionPlan plan) {
        if (plan == null) {
            return null;
        }

        ExecutionPlanDocument doc = new ExecutionPlanDocument();
        doc.setPlanId(plan.getPlanId());

        // Convert plan to map using Jackson or your preferred JSON library
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> planMap = objectMapper.convertValue(plan, new TypeReference<Map<String, Object>>() {});
        doc.setPlanData(planMap);

        return doc;
    }

    // Convert back to ExecutionPlan
    public ExecutionPlan toExecutionPlan() {
        if (planData == null) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(planData, ExecutionPlan.class);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Map<String, Object> getPlanData() {
        return planData;
    }

    public void setPlanData(Map<String, Object> planData) {
        this.planData = planData;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}