package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nebula.shared.enums.ToolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a tool that can be used by agents in the Nebula platform.
 * Tools provide specific capabilities like database access, API calls, file operations, etc.
 */
public class Tool {
    
    @NotBlank
    @JsonProperty("toolId")
    private String toolId;
    
    @NotBlank
    @JsonProperty("name")
    private String name;
    
    @NotNull
    @JsonProperty("type")
    private ToolType type;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("configuration")
    private Map<String, Object> configuration;
    
    @JsonProperty("generatedCode")
    private String generatedCode;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("implementation")
    private Map<String, Object> implementation;

    @JsonProperty("parameters")
    private Map<String, Object> parameters;
    
    // Constructors
    public Tool() {
    }

    public Tool(String toolId, String name, ToolType type) {
        this.toolId = toolId;
        this.name = name;
        this.type = type;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Map<String, Object> getImplementation() {
        return implementation;
    }

    public void setImplementation(Map<String, Object> implementation) {
        this.implementation = implementation;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getToolId() { return toolId; }
    public void setToolId(String toolId) { this.toolId = toolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ToolType getType() { return type; }
    public void setType(ToolType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getConfiguration() { return configuration; }
    public void setConfiguration(Map<String, Object> configuration) { this.configuration = configuration; }

    public String getGeneratedCode() { return generatedCode; }
    public void setGeneratedCode(String generatedCode) { this.generatedCode = generatedCode; }
}