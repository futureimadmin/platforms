package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Represents a parameter for a tool in the Nebula platform.
 */
public class ToolParameter {
    
    @NotBlank
    @JsonProperty("name")
    private String name;
    
    @NotBlank
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("required")
    private boolean required = true;
    
    @JsonProperty("defaultValue")
    private Object defaultValue;
    
    @JsonProperty("validation")
    private Map<String, Object> validation;
    
    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<String, Object> getValidation() {
        return validation;
    }

    public void setValidation(Map<String, Object> validation) {
        this.validation = validation;
    }
}
