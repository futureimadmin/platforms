package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public ExecutionFlow() {
    }

    public ExecutionFlow(ExecutionFlowType type, List<ExecutionStep> steps) {
        this.type = type;
        this.steps = steps;
    }

    // Getters and Setters
    public ExecutionFlowType getType() {
        return type;
    }

    public void setType(ExecutionFlowType type) {
        this.type = type;
    }

    public List<ExecutionStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ExecutionStep> steps) {
        this.steps = steps;
    }

    public ErrorHandling getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(ErrorHandling errorHandling) {
        this.errorHandling = errorHandling;
    }

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
        public ErrorHandling() {
        }

        public ErrorHandling(ErrorStrategy strategy, Integer maxRetries, String retryDelay) {
            this.strategy = strategy;
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
        }

        // Getters and Setters
        public ErrorStrategy getStrategy() {
            return strategy;
        }

        public void setStrategy(ErrorStrategy strategy) {
            this.strategy = strategy;
        }

        public Integer getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
        }

        public String getRetryDelay() {
            return retryDelay;
        }

        public void setRetryDelay(String retryDelay) {
            this.retryDelay = retryDelay;
        }

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