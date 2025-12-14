package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nebula.shared.enums.ExecutionFlowType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Represents the execution flow for an execution plan.
 * Defines how agents should be executed (sequential, parallel, conditional, loop, hybrid).
 */
public class ExecutionFlow {
    
    @NotNull
    @JsonProperty("type")
    private ExecutionFlowType type;
    
    @NotEmpty
    @Valid
    @JsonProperty("steps")
    private List<ExecutionStep> steps;
    
    @JsonProperty("errorHandling")
    private ErrorHandling errorHandling;
    
    // Constructors
    public ExecutionFlow() {}
    
    public ExecutionFlow(ExecutionFlowType type, List<ExecutionStep> steps) {
        this.type = type;
        this.steps = steps;
    }
    
    // Getters and Setters
    public ExecutionFlowType getType() { return type; }
    public void setType(ExecutionFlowType type) { this.type = type; }
    
    public List<ExecutionStep> getSteps() { return steps; }
    public void setSteps(List<ExecutionStep> steps) { this.steps = steps; }
    
    public ErrorHandling getErrorHandling() { return errorHandling; }
    public void setErrorHandling(ErrorHandling errorHandling) { this.errorHandling = errorHandling; }
    
    /**
     * Error handling configuration for execution flow
     */
    public static class ErrorHandling {
        @JsonProperty("strategy")
        private ErrorStrategy strategy;
        
        @JsonProperty("maxRetries")
        private Integer maxRetries;
        
        @JsonProperty("retryDelay")
        private String retryDelay;
        
        // Constructors
        public ErrorHandling() {}
        
        public ErrorHandling(ErrorStrategy strategy, Integer maxRetries, String retryDelay) {
            this.strategy = strategy;
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
        }
        
        // Getters and Setters
        public ErrorStrategy getStrategy() { return strategy; }
        public void setStrategy(ErrorStrategy strategy) { this.strategy = strategy; }
        
        public Integer getMaxRetries() { return maxRetries; }
        public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
        
        public String getRetryDelay() { return retryDelay; }
        public void setRetryDelay(String retryDelay) { this.retryDelay = retryDelay; }
        
        /**
         * Error handling strategies
         */
        public enum ErrorStrategy {
            FAIL_FAST,
            CONTINUE,
            RETRY,
            ROLLBACK
        }
    }
}

/**
 * Base class for execution steps
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SequentialStep.class, name = "sequential"),
    @JsonSubTypes.Type(value = ParallelStep.class, name = "parallel"),
    @JsonSubTypes.Type(value = ConditionalStep.class, name = "conditional"),
    @JsonSubTypes.Type(value = LoopStep.class, name = "loop")
})
public abstract class ExecutionStep {
    
    @NotNull
    @JsonProperty("stepId")
    private String stepId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("timeout")
    private String timeout;
    
    // Constructors
    public ExecutionStep() {}
    
    public ExecutionStep(String stepId, String name, String description) {
        this.stepId = stepId;
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTimeout() { return timeout; }
    public void setTimeout(String timeout) { this.timeout = timeout; }
    
    public abstract String getType();
}