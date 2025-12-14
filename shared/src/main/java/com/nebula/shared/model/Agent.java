package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nebula.shared.enums.AgentType;
import com.nebula.shared.enums.ProgrammingLanguage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;

/**
 * Represents an agent in the Nebula platform.
 * Agents can be part of control plane or data plane.
 */
public class Agent {
    
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$")
    @JsonProperty("agentId")
    private String agentId;
    
    @NotBlank
    @JsonProperty("name")
    private String name;
    
    @NotNull
    @JsonProperty("type")
    private AgentType type;
    
    @NotNull
    @JsonProperty("language")
    private ProgrammingLanguage language;
    
    @NotEmpty
    @JsonProperty("capabilities")
    private List<String> capabilities;
    
    @JsonProperty("dependencies")
    private List<String> dependencies;
    
    @JsonProperty("configuration")
    private Map<String, Object> configuration;
    
    @Valid
    @JsonProperty("tools")
    private List<Tool> tools;
    
    @JsonProperty("status")
    private AgentStatus status;
    
    @JsonProperty("generatedCode")
    private String generatedCode;
    
    @JsonProperty("prompt")
    private String prompt;
    
    // Constructors
    public Agent() {
        this.status = AgentStatus.CREATED;
    }
    
    public Agent(String agentId, String name, AgentType type, ProgrammingLanguage language, List<String> capabilities) {
        this.agentId = agentId;
        this.name = name;
        this.type = type;
        this.language = language;
        this.capabilities = capabilities;
        this.status = AgentStatus.CREATED;
    }
    
    // Getters and Setters
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public AgentType getType() { return type; }
    public void setType(AgentType type) { this.type = type; }
    
    public ProgrammingLanguage getLanguage() { return language; }
    public void setLanguage(ProgrammingLanguage language) { this.language = language; }
    
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, Object> getConfiguration() { return configuration; }
    public void setConfiguration(Map<String, Object> configuration) { this.configuration = configuration; }
    
    public List<Tool> getTools() { return tools; }
    public void setTools(List<Tool> tools) { this.tools = tools; }
    
    public AgentStatus getStatus() { return status; }
    public void setStatus(AgentStatus status) { this.status = status; }
    
    public String getGeneratedCode() { return generatedCode; }
    public void setGeneratedCode(String generatedCode) { this.generatedCode = generatedCode; }
    
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    
    /**
     * Agent execution status
     */
    public enum AgentStatus {
        CREATED,
        GENERATING,
        GENERATED,
        COMPILING,
        COMPILED,
        READY,
        RUNNING,
        COMPLETED,
        FAILED,
        STOPPED
    }
}