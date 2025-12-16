package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nebula.shared.enums.AgentType;
import com.nebula.shared.enums.ProgrammingLanguage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an agent in the Nebula platform.
 * Agents can be part of control plane or data plane.
 */
public class Agent {

    @NotBlank(message = "Agent ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Agent ID can only contain alphanumeric characters, hyphens, and underscores")
    @JsonProperty("agentId")
    private String agentId;

    @NotBlank(message = "Agent name is required")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Agent type is required")
    @JsonProperty("type")
    private AgentType type;

    @NotNull(message = "Programming language is required")
    @JsonProperty("language")
    private ProgrammingLanguage language;

    @NotEmpty(message = "At least one capability is required")
    private List<@NotBlank String> capabilities;

    @JsonProperty("dependencies")
    private List<@Pattern(regexp = "^[a-zA-Z0-9-_]+$") String> dependencies;

    @Valid
    @JsonProperty("configuration")
    private Map<String, Object> configuration;

    @Valid
    @JsonProperty("tools")
    private List<Tool> tools = new ArrayList<>();
    
    @JsonProperty("generatedCode")
    private String generatedCode;

    // Constructors
    public Agent() {
    }

    public Agent(String agentId, String name, AgentType type,
                 ProgrammingLanguage language, List<String> capabilities) {
        this.agentId = agentId;
        this.name = name;
        this.type = type;
        this.language = language;
        this.capabilities = capabilities;
        // tools already initialized with default empty list
    }

    // Getters and Setters
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AgentType getType() {
        return type;
    }

    public void setType(AgentType type) {
        this.type = type;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public List<Tool> getTools() {
        return tools != null ? tools : new ArrayList<>();
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools != null ? tools : new ArrayList<>();
    }
    
    public String getGeneratedCode() {
        return generatedCode;
    }
    
    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }
}