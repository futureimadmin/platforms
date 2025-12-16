package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the retry policy for execution steps.
 */
public class RetryPolicy {

    @NotNull
    @Min(0)
    @JsonProperty("maxAttempts")
    private Integer maxAttempts = 3;

    @Min(0)
    @JsonProperty("initialDelayMs")
    private Long initialDelayMs = 1000L;

    @Min(0)
    @JsonProperty("maxDelayMs")
    private Long maxDelayMs = 60000L;

    @JsonProperty("backoffMultiplier")
    private Double backoffMultiplier = 2.0;

    @JsonProperty("retryOn")
    private List<String> retryOn = new ArrayList<>();

    // Constructors
    public RetryPolicy() {}

    public RetryPolicy(Integer maxAttempts, Long initialDelayMs, Long maxDelayMs, Double backoffMultiplier) {
        this.maxAttempts = maxAttempts;
        this.initialDelayMs = initialDelayMs;
        this.maxDelayMs = maxDelayMs;
        this.backoffMultiplier = backoffMultiplier;
    }

    // Getters and Setters
    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Long getInitialDelayMs() {
        return initialDelayMs;
    }

    public void setInitialDelayMs(Long initialDelayMs) {
        this.initialDelayMs = initialDelayMs;
    }

    public Long getMaxDelayMs() {
        return maxDelayMs;
    }

    public void setMaxDelayMs(Long maxDelayMs) {
        this.maxDelayMs = maxDelayMs;
    }

    public Double getBackoffMultiplier() {
        return backoffMultiplier;
    }

    public void setBackoffMultiplier(Double backoffMultiplier) {
        this.backoffMultiplier = backoffMultiplier;
    }

    public List<String> getRetryOn() {
        return retryOn;
    }

    public void setRetryOn(List<String> retryOn) {
        this.retryOn = retryOn != null ? retryOn : new ArrayList<>();
    }
}
