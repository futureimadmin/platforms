package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a conditional branch in the execution flow.
 * Used for conditional execution paths in the workflow.
 */
public class ConditionalBranch {
    
    @NotNull
    @JsonProperty("condition")
    private String condition;
    
    @NotEmpty
    @Valid
    @JsonProperty("steps")
    private List<ExecutionStep> steps = new ArrayList<>();
    
    // Constructors
    public ConditionalBranch() {}
    
    public ConditionalBranch(String condition, List<ExecutionStep> steps) {
        this.condition = condition;
        this.steps = steps != null ? steps : new ArrayList<>();
    }
    
    // Getters and Setters
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public List<ExecutionStep> getSteps() {
        return steps;
    }
    
    public void setSteps(List<ExecutionStep> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }
}
