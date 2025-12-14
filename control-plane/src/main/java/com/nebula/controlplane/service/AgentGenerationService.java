package com.nebula.controlplane.service;

import com.nebula.shared.model.Agent;
import com.nebula.shared.model.Tool;
import com.nebula.shared.enums.AgentType;
import com.nebula.shared.enums.ProgrammingLanguage;
import com.nebula.shared.enums.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for generating agents dynamically using LLM
 */
@Service
public class AgentGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentGenerationService.class);
    
    @Autowired
    private LLMService llmService;
    
    // In-memory storage for generated agents (replace with database in production)
    private final Map<String, Agent> generatedAgents = new HashMap<>();
    
    /**
     * Generate agents based on user prompt and requirements
     */
    public List<Agent> generateAgents(String prompt, Map<String, Object> context) {
        logger.info("Generating agents for prompt: {}", prompt);
        
        try {
            // Analyze the prompt to determine required agents
            List<AgentRequirement> requirements = analyzePromptForAgents(prompt, context);
            
            List<Agent> agents = new ArrayList<>();
            
            for (AgentRequirement requirement : requirements) {
                Agent agent = generateSingleAgent(requirement, prompt, context);
                if (agent != null) {
                    agents.add(agent);
                    generatedAgents.put(agent.getAgentId(), agent);
                }
            }
            
            logger.info("Generated {} agents successfully", agents.size());
            return agents;
            
        } catch (Exception e) {
            logger.error("Error generating agents: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate agents: " + e.getMessage());
        }
    }
    
    /**
     * Generate a single agent based on specific requirements
     */
    public Agent generateSingleAgent(String agentType, String description, Map<String, Object> context) {
        logger.info("Generating single agent of type: {}", agentType);
        
        AgentRequirement requirement = new AgentRequirement();
        requirement.setType(AgentType.valueOf(agentType.toUpperCase()));
        requirement.setDescription(description);
        requirement.setRequiredCapabilities(extractCapabilities(description));
        
        return generateSingleAgent(requirement, description, context);
    }
    
    /**
     * Get generated agent by ID
     */
    public Agent getGeneratedAgent(String agentId) {
        logger.debug("Retrieving generated agent: {}", agentId);
        return generatedAgents.get(agentId);
    }
    
    /**
     * Get all generated agents
     */
    public List<Agent> getAllGeneratedAgents() {
        logger.debug("Retrieving all generated agents");
        return new ArrayList<>(generatedAgents.values());
    }
    
    /**
     * Update generated agent
     */
    public Agent updateGeneratedAgent(String agentId, Agent updatedAgent) {
        logger.info("Updating generated agent: {}", agentId);

        if (!generatedAgents.containsKey(agentId)) {
            throw new RuntimeException("Generated agent not found: " + agentId);
        }

        updatedAgent.setAgentId(agentId);
        generatedAgents.put(agentId, updatedAgent);

        logger.info("Generated agent updated successfully: {}", agentId);
        return updatedAgent;
    }
    
    /**
     * Delete generated agent
     */
    public boolean deleteGeneratedAgent(String agentId) {
        logger.info("Deleting generated agent: {}", agentId);
        
        Agent removed = generatedAgents.remove(agentId);
        boolean success = removed != null;
        
        logger.info("Generated agent deletion {}: {}", success ? "successful" : "failed", agentId);
        return success;
    }
    
    /**
     * Generate tools for an agent
     */
    public List<Tool> generateToolsForAgent(Agent agent, List<String> requiredCapabilities) {
        logger.info("Generating tools for agent: {}", agent.getName());
        
        List<Tool> tools = new ArrayList<>();
        
        for (String capability : requiredCapabilities) {
            Tool tool = generateToolForCapability(capability, agent);
            if (tool != null) {
                tools.add(tool);
            }
        }
        
        logger.info("Generated {} tools for agent {}", tools.size(), agent.getName());
        return tools;
    }
    
    /**
     * Analyze prompt to determine required agents
     */
    private List<AgentRequirement> analyzePromptForAgents(String prompt, Map<String, Object> context) {
        logger.debug("Analyzing prompt for agent requirements");
        
        List<AgentRequirement> requirements = new ArrayList<>();
        
        // Use LLM to analyze the prompt and determine required agents
        String analysisPrompt = buildAgentAnalysisPrompt(prompt, context);
        String llmResponse = llmService.generateResponse(analysisPrompt, context);
        
        // Parse LLM response to extract agent requirements
        requirements = parseLLMResponseForAgents(llmResponse);
        
        // Add default agents if none specified
        if (requirements.isEmpty()) {
            requirements.add(createDefaultAgentRequirement());
        }
        
        logger.debug("Identified {} agent requirements", requirements.size());
        return requirements;
    }
    
    /**
     * Generate a single agent based on requirements
     */
    private Agent generateSingleAgent(AgentRequirement requirement, String originalPrompt, Map<String, Object> context) {
        logger.debug("Generating agent for requirement: {}", requirement.getType());

        try {
            Agent agent = new Agent();
            agent.setAgentId(generateAgentId());
            agent.setName(generateAgentName(requirement));
            agent.setType(requirement.getType());
            agent.setLanguage(determineProgrammingLanguage(requirement, context));

            // Generate agent code using LLM
            String agentCode = generateAgentCode(agent, requirement, originalPrompt, context);
            agent.setGeneratedCode(agentCode);

            // Generate tools for the agent
            List<Tool> tools = generateToolsForAgent(agent, requirement.getRequiredCapabilities());
            agent.setTools(tools);

            // Set agent configuration
            Map<String, Object> config = new HashMap<>();
            config.put("maxRetries", 3);
            config.put("timeout", 30000);
            config.put("memoryLimit", "512MB");
            agent.setConfiguration(config);

            logger.debug("Agent generated successfully: {}", agent.getName());
            return agent;

        } catch (Exception e) {
            logger.error("Error generating agent for requirement {}: {}", requirement.getType(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate agent code using LLM
     */
    private String generateAgentCode(Agent agent, AgentRequirement requirement, String originalPrompt, Map<String, Object> context) {
        logger.debug("Generating code for agent: {}", agent.getName());
        
        String codeGenerationPrompt = buildCodeGenerationPrompt(agent, requirement, originalPrompt, context);
        return llmService.generateResponse(codeGenerationPrompt, context);
    }
    
    /**
     * Generate tool for specific capability
     */
    private Tool generateToolForCapability(String capability, Agent agent) {
        logger.debug("Generating tool for capability: {}", capability);
        
        Tool tool = new Tool();
        tool.setToolId(generateToolId());
        tool.setName(capability.toLowerCase().replace(" ", "_") + "_tool");
        tool.setDescription("Tool for " + capability);
        tool.setType(determineToolType(capability));
        tool.setCreatedAt(LocalDateTime.now());
        
        // Generate tool implementation
        Map<String, Object> implementation = new HashMap<>();
        implementation.put("language", agent.getLanguage().toString());
        implementation.put("code", generateToolCode(capability, agent));
        implementation.put("dependencies", generateToolDependencies(capability));
        tool.setImplementation(implementation);
        
        return tool;
    }
    
    /**
     * Build prompt for agent analysis
     */
    private String buildAgentAnalysisPrompt(String userPrompt, Map<String, Object> context) {
        return String.format("""
            Analyze the following user prompt and determine what types of agents are needed to accomplish the task:
            
            User Prompt: %s
            
            Context: %s
            
            Please identify the required agents and their capabilities. Consider these agent types:
            - DATA_AGENT: For data processing, analysis, and transformation
            - TOOL_AGENT: For external API calls, file operations, database operations
            - CONTROL_AGENT: For workflow coordination and decision making
            - HUMAN_INTERFACE_AGENT: For human interaction and approval workflows
            
            Respond with a structured list of required agents, their types, descriptions, and required capabilities.
            """, userPrompt, context.toString());
    }
    
    /**
     * Build prompt for code generation
     */
    private String buildCodeGenerationPrompt(Agent agent, AgentRequirement requirement, String originalPrompt, Map<String, Object> context) {
        return String.format("""
            Generate %s code for an agent with the following specifications:
            
            Agent Name: %s
            Agent Type: %s
            Description: %s
            Required Capabilities: %s
            
            Original User Prompt: %s
            Context: %s
            
            Generate complete, production-ready code that implements the agent's functionality.
            Include error handling, logging, and proper documentation.
            """,
            agent.getLanguage(),
            agent.getName(),
            agent.getType(),
            requirement.getDescription(),
            requirement.getRequiredCapabilities(),
            originalPrompt,
            context.toString());
    }
    
    /**
     * Parse LLM response for agent requirements
     */
    private List<AgentRequirement> parseLLMResponseForAgents(String llmResponse) {
        // Simple parsing - in production, use more sophisticated parsing
        List<AgentRequirement> requirements = new ArrayList<>();
        
        // For now, create a default set based on common patterns
        if (llmResponse.toLowerCase().contains("data")) {
            AgentRequirement req = new AgentRequirement();
            req.setType(AgentType.DATA_AGENT);
            req.setDescription("Data processing agent");
            req.setRequiredCapabilities(Arrays.asList("data_processing", "data_analysis"));
            requirements.add(req);
        }
        
        if (llmResponse.toLowerCase().contains("api") || llmResponse.toLowerCase().contains("external")) {
            AgentRequirement req = new AgentRequirement();
            req.setType(AgentType.TOOL_AGENT);
            req.setDescription("External integration agent");
            req.setRequiredCapabilities(Arrays.asList("api_calls", "external_integration"));
            requirements.add(req);
        }
        
        return requirements;
    }
    
    /**
     * Helper methods
     */
    private String generateAgentId() {
        return "agent-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String generateToolId() {
        return "tool-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String generateAgentName(AgentRequirement requirement) {
        return requirement.getType().toString().toLowerCase().replace("_", " ") + " Agent";
    }
    
    private ProgrammingLanguage determineProgrammingLanguage(AgentRequirement requirement, Map<String, Object> context) {
        // Default to Java for now - could be made configurable
        return ProgrammingLanguage.JAVA;
    }
    
    private ToolType determineToolType(String capability) {
        if (capability.toLowerCase().contains("database")) {
            return ToolType.DATABASE;
        } else if (capability.toLowerCase().contains("api")) {
            return ToolType.API;
        } else if (capability.toLowerCase().contains("file")) {
            return ToolType.FILE;
        }
        return ToolType.FUNCTION;
    }
    
    private List<String> extractCapabilities(String description) {
        // Simple capability extraction - could be enhanced with NLP
        List<String> capabilities = new ArrayList<>();
        
        if (description.toLowerCase().contains("database")) {
            capabilities.add("database_operations");
        }
        if (description.toLowerCase().contains("api")) {
            capabilities.add("api_calls");
        }
        if (description.toLowerCase().contains("file")) {
            capabilities.add("file_operations");
        }
        if (description.toLowerCase().contains("data")) {
            capabilities.add("data_processing");
        }
        
        return capabilities;
    }
    
    private String generateToolCode(String capability, Agent agent) {
        // Generate basic tool code template
        return String.format("""
            // Tool implementation for %s
            public class %sTool {
                public Object execute(Map<String, Object> parameters) {
                    // Implementation for %s
                    return null;
                }
            }
            """, capability, capability.replace(" ", ""), capability);
    }
    
    private List<String> generateToolDependencies(String capability) {
        List<String> dependencies = new ArrayList<>();
        
        if (capability.toLowerCase().contains("database")) {
            dependencies.add("jdbc-driver");
        }
        if (capability.toLowerCase().contains("api")) {
            dependencies.add("http-client");
        }
        
        return dependencies;
    }
    
    private AgentRequirement createDefaultAgentRequirement() {
        AgentRequirement requirement = new AgentRequirement();
        requirement.setType(AgentType.ORCHESTRATION_AGENT);
        requirement.setDescription("Default control agent");
        requirement.setRequiredCapabilities(Arrays.asList("task_coordination"));
        return requirement;
    }
    
    /**
     * Inner class for agent requirements
     */
    private static class AgentRequirement {
        private AgentType type;
        private String description;
        private List<String> requiredCapabilities;
        
        public AgentType getType() { return type; }
        public void setType(AgentType type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getRequiredCapabilities() { return requiredCapabilities; }
        public void setRequiredCapabilities(List<String> requiredCapabilities) { this.requiredCapabilities = requiredCapabilities; }
    }
}