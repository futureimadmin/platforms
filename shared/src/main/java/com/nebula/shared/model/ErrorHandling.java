package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines error handling strategies for a sub-flow or execution step.
 * Specifies how to handle different types of errors that may occur during execution.
 */
public class ErrorHandling {

    /**
     * The strategy to use for handling errors.
     * Possible values: "retry", "continue", "stop", "custom"
     */
    @NotNull
    @JsonProperty("strategy")
    private String strategy;

    /**
     * Maximum number of retry attempts for retryable errors.
     * Only applicable when strategy is "retry".
     */
    @JsonProperty("maxRetries")
    private Integer maxRetries;

    /**
     * List of error codes to be handled by this error handling configuration.
     * If empty, all errors will be handled by this configuration.
     */
    @Valid
    @JsonProperty("errorCodes")
    private List<String> errorCodes = new ArrayList<>();

    /**
     * Custom error handler configuration.
     * Only applicable when strategy is "custom".
     */
    @Valid
    @JsonProperty("customHandler")
    private Object customHandler;

    // Getters and Setters
    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes != null ? errorCodes : new ArrayList<>();
    }

    public Object getCustomHandler() {
        return customHandler;
    }

    public void setCustomHandler(Object customHandler) {
        this.customHandler = customHandler;
    }

    // Builder pattern for fluent API
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ErrorHandling errorHandling = new ErrorHandling();

        public Builder strategy(String strategy) {
            errorHandling.strategy = strategy;
            return this;
        }

        public Builder maxRetries(Integer maxRetries) {
            errorHandling.maxRetries = maxRetries;
            return this;
        }

        public Builder errorCode(String errorCode) {
            if (errorHandling.errorCodes == null) {
                errorHandling.errorCodes = new ArrayList<>();
            }
            errorHandling.errorCodes.add(errorCode);
            return this;
        }

        public Builder errorCodes(List<String> errorCodes) {
            if (errorHandling.errorCodes == null) {
                errorHandling.errorCodes = new ArrayList<>();
            }
            errorHandling.errorCodes.addAll(errorCodes);
            return this;
        }

        public Builder customHandler(Object handler) {
            errorHandling.customHandler = handler;
            return this;
        }

        public ErrorHandling build() {
            return errorHandling;
        }
    }
}
