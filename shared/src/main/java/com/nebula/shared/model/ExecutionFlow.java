package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nebula.shared.enums.ExecutionFlowType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the execution flow for an execution plan.
 * Defines how agents should be executed (sequential, parallel, conditional, loop, hybrid, hierarchical).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionFlow {

    @NotNull
    @JsonProperty("type")
    private ExecutionFlowType type;

    @Valid
    @JsonProperty("steps")
    private List<ExecutionStep> steps = new ArrayList<>();

    @Valid
    @JsonProperty("flows")
    private List<ExecutionFlow> flows = new ArrayList<>();

    @JsonProperty("maxConcurrent")
    private Integer maxConcurrent;

    @JsonProperty("maxIterations")
    private Integer maxIterations;

    @JsonProperty("condition")
    private String condition;

    @Valid
    @JsonProperty("errorHandling")
    private ErrorHandling errorHandling;

    @Valid
    @JsonProperty("branches")
    private List<ConditionalBranch> branches;

    @Valid
    @JsonProperty("defaultSteps")
    private List<ExecutionStep> defaultSteps;

    // Constructors
    public ExecutionFlow() {
    }

    public ExecutionFlow(ExecutionFlowType type, List<ExecutionStep> steps) {
        this.type = type;
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    // Getters and Setters
    public ExecutionFlowType getType() {
        return type;
    }

    public void setType(ExecutionFlowType type) {
        this.type = type;
    }

    public List<ExecutionStep> getSteps() {
        return steps != null ? steps : new ArrayList<>();
    }

    public void setSteps(List<ExecutionStep> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    public List<ExecutionFlow> getFlows() {
        return flows != null ? flows : new ArrayList<>();
    }

    public void setFlows(List<ExecutionFlow> flows) {
        this.flows = flows != null ? flows : new ArrayList<>();
    }

    public Integer getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(Integer maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public Integer getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(Integer maxIterations) {
        this.maxIterations = maxIterations;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public ErrorHandling getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(ErrorHandling errorHandling) {
        this.errorHandling = errorHandling;
    }

    public List<ConditionalBranch> getBranches() {
        return branches != null ? branches : new ArrayList<>();
    }

    public void setBranches(List<ConditionalBranch> branches) {
        this.branches = branches != null ? branches : new ArrayList<>();
    }

    public List<ExecutionStep> getDefaultSteps() {
        return defaultSteps != null ? defaultSteps : new ArrayList<>();
    }

    public void setDefaultSteps(List<ExecutionStep> defaultSteps) {
        this.defaultSteps = defaultSteps != null ? defaultSteps : new ArrayList<>();
    }
}