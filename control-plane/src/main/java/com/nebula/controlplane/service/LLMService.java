package com.nebula.controlplane.service;

import com.google.adk.agents.LlmAgent;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.BaseTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.nebula.controlplane.config.ExecutionPlanConfig;
import com.nebula.shared.model.Agent;
import com.nebula.shared.model.ExecutionPlan;
import com.nebula.shared.model.Tool;
import com.nebula.shared.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for integrating with Large Language Models (Gemini, Claude, etc.)
 * Responsible for:
 * 1. Creating execution plans from user prompts
 * 2. Generating agent code
 * 3. Creating tools for agents
 * 4. Deciding programming languages for agents
 */
@Service
public class LLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMService.class);
    
    @Value("${nebula.llm.provider:gemini}")
    private String llmProvider;
    
    @Value("${nebula.llm.gemini.api-key:}")
    private String geminiApiKey;
    
    @Value("${nebula.llm.gemini.project-id:}")
    private String geminiProjectId;
    
    @Value("${nebula.llm.claude.api-key:}")
    private String claudeApiKey;

    @Autowired
    private ExecutionPlanConfig executionPlanConfig;
    
    private final WebClient webClient;
    
    public LLMService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * Create an execution plan from user prompt using LLM
     */
    public ExecutionPlan generateExecutionPlan(String userPrompt, Map<String, Object> context) {
        logger.info("Creating execution plan for prompt: {}", userPrompt);
        
        try {
            String systemPrompt = buildExecutionPlanSystemPrompt();
            String userMessage = buildExecutionPlanUserMessage(userPrompt, context);
            
            String llmResponse = callLLM(systemPrompt, userMessage);
            
            // Parse the LLM response to create ExecutionPlan
            ExecutionPlan executionPlan = parseExecutionPlanFromLLMResponse(llmResponse);
            
            logger.info("Successfully created execution plan: {}", executionPlan.getPlanId());
            return executionPlan;
        } catch (Exception e) {
            logger.error("Error creating execution plan", e);
            throw new RuntimeException("Failed to create execution plan: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate agent code using LLM
     */
    public String generateAgentCode(Agent agent, ExecutionPlan executionPlan) {
        logger.info("Generating code for agent: {}", agent.getAgentId());
        
        try {
            String systemPrompt = buildAgentCodeSystemPrompt(agent.getLanguage());
            String userMessage = buildAgentCodeUserMessage(agent, executionPlan);
            
            String llmResponse = callLLM(systemPrompt, userMessage);
            
            // Extract code from LLM response
            String generatedCode = extractCodeFromLLMResponse(llmResponse, agent.getLanguage());
            
            logger.info("Successfully generated code for agent: {}", agent.getAgentId());
            return generatedCode;
            
        } catch (Exception e) {
            logger.error("Error generating agent code", e);
            throw new RuntimeException("Failed to generate agent code: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate tool code using LLM
     */
    public String generateToolCode(Tool tool, Agent agent) {
        logger.info("Generating code for tool: {}", tool.getToolId());
        
        try {
            String systemPrompt = buildToolCodeSystemPrompt(agent.getLanguage());
            String userMessage = buildToolCodeUserMessage(tool, agent);
            
            String llmResponse = callLLM(systemPrompt, userMessage);
            
            // Extract code from LLM response
            String generatedCode = extractCodeFromLLMResponse(llmResponse, agent.getLanguage());
            
            logger.info("Successfully generated code for tool: {}", tool.getToolId());
            return generatedCode;
            
        } catch (Exception e) {
            logger.error("Error generating tool code", e);
            throw new RuntimeException("Failed to generate tool code: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyze prompt and suggest agents using LLM
     */
    public List<Agent> suggestAgents(String userPrompt, Map<String, Object> context) {
        logger.info("Suggesting agents for prompt: {}", userPrompt);
        
        try {
            String systemPrompt = buildAgentSuggestionSystemPrompt();
            String userMessage = buildAgentSuggestionUserMessage(userPrompt, context);
            
            String llmResponse = callLLM(systemPrompt, userMessage);
            
            // Parse agents from LLM response
            List<Agent> suggestedAgents = parseAgentsFromLLMResponse(llmResponse);
            
            logger.info("Successfully suggested {} agents", suggestedAgents.size());
            return suggestedAgents;
            
        } catch (Exception e) {
            logger.error("Error suggesting agents", e);
            throw new RuntimeException("Failed to suggest agents: " + e.getMessage(), e);
        }
    }
    
    /**
     * Call the configured LLM with system and user messages
     */
    private String callLLM(String systemPrompt, String userMessage) {
        switch (llmProvider.toLowerCase()) {
            case "gemini":
                return callGemini(systemPrompt, userMessage);
            case "claude":
                return callClaude(systemPrompt, userMessage);
            default:
                throw new IllegalArgumentException("Unsupported LLM provider: " + llmProvider);
        }
    }

    /**
     * Call Google Gemini API
     */
    public String callGemini(String systemPrompt, String userMessage) {
        // Implementation for Gemini API call
        String modelName = "gemini-2.5-pro";
        String agentName = "Execution Plan Agent";
        String description = "Agent that generates execution plan based on user prompt";
        String prompt = systemPrompt + "\n\n" + userMessage;
        List<BaseTool> tools = new ArrayList<>();
        String outputKey = "output";
        LlmAgent llmAgent = LlmAgent.builder()
                .model(modelName)
                .name(agentName)
                .description(description)
                .instruction(prompt).tools(tools)
                .generateContentConfig(executionPlanConfig.generateContentConfig())
                .outputKey(outputKey)
                .build();
        return sendAndGetResponse(llmAgent);
    }

    /**
     * Call Claude API
     */
    private String callClaude(String systemPrompt, String userMessage) {
        // Implementation for Claude API call
        // This would use Anthropic's Claude API

        try {
            // For now, return a mock response - replace with actual Claude API call
            return generateMockExecutionPlan();
        } catch (Exception e) {
            logger.error("Error calling Claude API", e);
            throw new RuntimeException("Failed to call Claude API", e);
        }
    }

    public String sendAndGetResponse(LlmAgent agent) {
        logger.info("Sending message to LLM agent: " + agent.name());
        String runId = UUID.randomUUID().toString();
        InMemoryRunner runner = new InMemoryRunner(agent);
        Session session = runner.sessionService()
                .createSession(agent.name(), runId)
                .blockingGet();

        // Use the agent's instruction as the user message
        String userMessage = agent.instruction().toString();

        Content userMsg = Content.fromParts(Part.fromText(userMessage));
        logger.info("Sending message: " + userMessage);

        // Send the message and collect the response
        StringBuilder responseBuilder = new StringBuilder();
        runner.runAsync(runId, session.id(), userMsg)
                .blockingForEach(event -> {
                    if (event.content().isPresent()) {
                        Content content = event.content().get();
                        // Extract text from content parts
                        String response = extractTextFromContent(content);
                        if (!response.isEmpty()) {
                            responseBuilder.append(response).append("\n");
                        }
                    }
                });

        return responseBuilder.toString().trim();
    }

    /**
     * Extracts text from a Content object using multiple fallback methods
     */
    private String extractTextFromContent(Content content) {
        String llmResponse = "";

        // First try to get text from parts
        if (content.parts() != null && content.parts().isPresent()) {
            llmResponse = content.parts().get().stream()
                    .filter(part -> part != null && part.text() != null && part.text().isPresent())
                    .map(part -> part.text().get())
                    .collect(Collectors.joining("\n"));
        }

        // If no text from parts, try reflection
        if (llmResponse.isEmpty()) {
            try {
                java.lang.reflect.Method getTextMethod = content.getClass().getMethod("getText");
                if (getTextMethod != null) {
                    Object text = getTextMethod.invoke(content);
                    if (text instanceof String) {
                        llmResponse = (String) text;
                    }
                }
            } catch (Exception e) {
            }
        }

        // If still empty, use string representation
        if (llmResponse.isEmpty()) {
            llmResponse = content.toString();
        }

        return llmResponse;
    }
    /**
     * Build system prompt for execution plan creation
     */
    public String buildExecutionPlanSystemPrompt() {
        return """
            You are an expert AI agent orchestrator for the Nebula platform. Your task is to analyze user prompts and create comprehensive execution plans.
            
            You must respond with a valid JSON execution plan that follows the Nebula execution plan schema.
            
            Key responsibilities:
            1. Analyze the user prompt to understand the requirements
            2. Determine what agents are needed to fulfill the request
            3. Decide the programming language for each agent (Java preferred, but choose based on need)
            4. Create an execution flow (sequential, parallel, conditional, or loop)
            5. Identify required tools for each agent
            6. Determine if human-in-the-loop interaction is needed
            
            Always respond with a complete, valid JSON execution plan.
            """;
    }
    
    /**
     * Build user message for execution plan creation
     */
    public String buildExecutionPlanUserMessage(String userPrompt, Map<String, Object> context) {
        StringBuilder message = new StringBuilder();
        message.append("User Prompt: ").append(userPrompt).append("\n\n");
        
        if (context != null && !context.isEmpty()) {
            message.append("Context: ").append(JsonUtil.toJson(context)).append("\n\n");
        }
        
        message.append("Please create a comprehensive execution plan for this request.");
        
        return message.toString();
    }
    
    /**
     * Build system prompt for agent code generation
     */
    private String buildAgentCodeSystemPrompt(com.nebula.shared.enums.ProgrammingLanguage language) {
        return String.format("""
            You are an expert %s developer for the Nebula platform. Your task is to generate high-quality, production-ready agent code.
            
            Key requirements:
            1. Generate clean, well-documented code
            2. Include proper error handling
            3. Implement the agent's capabilities as specified
            4. Use appropriate design patterns
            5. Include logging and monitoring
            6. Make the code modular and testable
            
            Respond with only the code, properly formatted.
            """, language.getValue());
    }
    
    /**
     * Build user message for agent code generation
     */
    private String buildAgentCodeUserMessage(Agent agent, ExecutionPlan executionPlan) {
        StringBuilder message = new StringBuilder();
        message.append("Agent Details:\n");
        message.append("- ID: ").append(agent.getAgentId()).append("\n");
        message.append("- Name: ").append(agent.getName()).append("\n");
        message.append("- Type: ").append(agent.getType()).append("\n");
        message.append("- Language: ").append(agent.getLanguage()).append("\n");
        message.append("- Capabilities: ").append(agent.getCapabilities()).append("\n");
        
        if (agent.getConfiguration() != null) {
            message.append("- Configuration: ").append(JsonUtil.toJson(agent.getConfiguration())).append("\n");
        }
        
        message.append("\nExecution Plan Context:\n");
        message.append(JsonUtil.toJson(executionPlan));
        
        message.append("\n\nPlease generate the complete agent implementation.");
        
        return message.toString();
    }
    
    /**
     * Build system prompt for tool code generation
     */
    private String buildToolCodeSystemPrompt(com.nebula.shared.enums.ProgrammingLanguage language) {
        return String.format("""
            You are an expert %s developer for the Nebula platform. Your task is to generate tool implementations for agents.
            
            Key requirements:
            1. Generate reusable, modular tool code
            2. Include proper configuration handling
            3. Implement robust error handling
            4. Add comprehensive logging
            5. Make tools thread-safe if needed
            6. Include proper resource management
            
            Respond with only the code, properly formatted.
            """, language.getValue());
    }
    
    /**
     * Build user message for tool code generation
     */
    private String buildToolCodeUserMessage(Tool tool, Agent agent) {
        StringBuilder message = new StringBuilder();
        message.append("Tool Details:\n");
        message.append("- ID: ").append(tool.getToolId()).append("\n");
        message.append("- Name: ").append(tool.getName()).append("\n");
        message.append("- Type: ").append(tool.getType()).append("\n");
        message.append("- Description: ").append(tool.getDescription()).append("\n");
        
        if (tool.getConfiguration() != null) {
            message.append("- Configuration: ").append(JsonUtil.toJson(tool.getConfiguration())).append("\n");
        }
        
        message.append("\nAgent Context:\n");
        message.append("- Agent ID: ").append(agent.getAgentId()).append("\n");
        message.append("- Agent Language: ").append(agent.getLanguage()).append("\n");
        message.append("- Agent Capabilities: ").append(agent.getCapabilities()).append("\n");
        
        message.append("\n\nPlease generate the complete tool implementation.");
        
        return message.toString();
    }
    
    /**
     * Build system prompt for agent suggestion
     */
    private String buildAgentSuggestionSystemPrompt() {
        return """
            You are an expert AI agent architect for the Nebula platform. Your task is to analyze user prompts and suggest the optimal set of agents needed.
            
            Consider:
            1. What specific capabilities are needed
            2. How agents should interact with each other
            3. What programming language is best for each agent
            4. What tools each agent will need
            5. Dependencies between agents
            
            Respond with a JSON array of agent specifications.
            """;
    }
    
    /**
     * Build user message for agent suggestion
     */
    private String buildAgentSuggestionUserMessage(String userPrompt, Map<String, Object> context) {
        StringBuilder message = new StringBuilder();
        message.append("User Prompt: ").append(userPrompt).append("\n\n");
        
        if (context != null && !context.isEmpty()) {
            message.append("Context: ").append(JsonUtil.toJson(context)).append("\n\n");
        }
        
        message.append("Please suggest the optimal set of agents for this request.");
        
        return message.toString();
    }
    
    /**
     * Parse execution plan from LLM response
     */
    private ExecutionPlan parseExecutionPlanFromLLMResponse(String llmResponse) {
        try {
            // Extract JSON from LLM response (may contain additional text)
            String jsonPart = extractJsonFromResponse(llmResponse);
            return JsonUtil.fromJson(jsonPart, ExecutionPlan.class);
        } catch (Exception e) {
            logger.error("Error parsing execution plan from LLM response", e);
            throw new RuntimeException("Failed to parse execution plan", e);
        }
    }
    
    /**
     * Parse agents from LLM response
     */
    private List<Agent> parseAgentsFromLLMResponse(String llmResponse) {
        try {
            // Extract JSON from LLM response
            String jsonPart = extractJsonFromResponse(llmResponse);
            return JsonUtil.fromJson(jsonPart, List.class);
        } catch (Exception e) {
            logger.error("Error parsing agents from LLM response", e);
            throw new RuntimeException("Failed to parse agents", e);
        }
    }
    
    /**
     * Extract code from LLM response
     */
    private String extractCodeFromLLMResponse(String llmResponse, com.nebula.shared.enums.ProgrammingLanguage language) {
        // Extract code blocks from markdown-style response
        String codeBlockPattern = "```" + language.getValue() + "\\n([\\s\\S]*?)\\n```";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(codeBlockPattern);
        java.util.regex.Matcher matcher = pattern.matcher(llmResponse);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // If no code block found, return the entire response
        return llmResponse;
    }
    
    /**
     * Extract JSON from LLM response
     */
    private String extractJsonFromResponse(String response) {
        // Look for JSON content between ```json and ``` or just find JSON-like content
        String jsonPattern = "```json\\n([\\s\\S]*?)\\n```";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(jsonPattern);
        java.util.regex.Matcher matcher = pattern.matcher(response);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Try to find JSON-like content (starts with { and ends with })
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        
        return response;
    }
    
    /**
     * Generate a mock execution plan for testing
     */
    private String generateMockExecutionPlan() {
        return """
            {
              "planId": "mock-plan-001",
              "version": "1.0.0",
              "metadata": {
                "name": "Mock Execution Plan",
                "description": "A mock execution plan for testing",
                "createdBy": "Mock-LLM",
                "createdAt": "2024-12-14T06:00:00Z"
              },
              "agents": [
                {
                  "agentId": "mock-agent-001",
                  "name": "Mock Agent",
                  "type": "data",
                  "language": "java",
                  "capabilities": ["mock-capability"]
                }
              ],
              "executionFlow": {
                "type": "sequential",
                "steps": [
                  {
                    "stepId": "mock-step-001",
                    "type": "sequential",
                    "agentId": "mock-agent-001",
                    "name": "Mock Step",
                    "description": "A mock execution step"
                  }
                ]
              }
            }
            """;
    }

    /**
     * Generate a response from LLM for general analysis
     */
    public String generateResponse(String analysisPrompt, Map<String, Object> context) {
        logger.info("Generating LLM response for analysis prompt");
        
        try {
            String systemPrompt = "You are an expert AI assistant for the Nebula platform. Provide helpful, accurate, and detailed responses.";
            String userMessage = buildAnalysisUserMessage(analysisPrompt, context);
            
            return callLLM(systemPrompt, userMessage);
            
        } catch (Exception e) {
            logger.error("Error generating LLM response", e);
            throw new RuntimeException("Failed to generate LLM response: " + e.getMessage(), e);
        }
    }
    
    /**
     * Build user message for analysis
     */
    private String buildAnalysisUserMessage(String analysisPrompt, Map<String, Object> context) {
        StringBuilder message = new StringBuilder();
        message.append("Analysis Request: ").append(analysisPrompt).append("\n\n");
        
        if (context != null && !context.isEmpty()) {
            message.append("Context: ").append(JsonUtil.toJson(context)).append("\n\n");
        }
        
        message.append("Please provide a detailed analysis and response.");
        
        return message.toString();
    }
}