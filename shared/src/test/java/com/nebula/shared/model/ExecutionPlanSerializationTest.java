package com.nebula.shared.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nebula.shared.enums.AgentType;
import com.nebula.shared.enums.ProgrammingLanguage;
import com.nebula.shared.enums.ToolType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExecutionPlanSerializationTest {

    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void testExecutionPlanSerializationDeserialization() throws JsonProcessingException {
        // 1. Create test data
        ExecutionPlan executionPlan = createTestExecutionPlan();
        
        // 2. Serialize to JSON
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(executionPlan);
        assertNotNull(json);
        
        // 3. Deserialize back to object
        ExecutionPlan deserialized = objectMapper.readValue(json, ExecutionPlan.class);
        
        // 4. Verify the deserialized object
        assertNotNull(deserialized);
        assertEquals(15, deserialized.getAgents().size());
        
        // Verify first agent
        Agent firstAgent = deserialized.getAgents().get(0);
        assertEquals("agent-1", firstAgent.getAgentId());
        assertEquals("Agent 1", firstAgent.getName());
        assertEquals(2, firstAgent.getTools().size());
        assertEquals("tool-1-1", firstAgent.getTools().get(0).getToolId());
        
        // Verify last agent
        Agent lastAgent = deserialized.getAgents().get(14);
        assertEquals("agent-15", lastAgent.getAgentId());
        assertEquals("Agent 15", lastAgent.getName());
        assertEquals(1, lastAgent.getTools().size());
    }
    
    private ExecutionPlan createTestExecutionPlan() {
        // Create metadata
        ExecutionPlan.Metadata metadata = new ExecutionPlan.Metadata(
            "Test Execution Plan",
            "Test plan with 15 agents",
            "test-user",
            Instant.now().toString()
        );
        metadata.setEstimatedDuration("PT1H");
        metadata.setTags(Arrays.asList("test", "integration"));
        
        // Create 15 agents with tools
        List<Agent> agents = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            agents.add(createAgent(i));
        }
        
        // Create a simple sequential execution flow
        ExecutionFlow executionFlow = new ExecutionFlow();
        
        // Create shared context
        ExecutionPlan.SharedContext.Variables variables = new ExecutionPlan.SharedContext.Variables();
        variables.setEnvironment("test");
        variables.setVersion("1.0.0");

        List<String> secrets = Arrays.asList("api-key", "db-password");
        ExecutionPlan.SharedContext sharedContext = new ExecutionPlan.SharedContext(variables, secrets);
        
        // Create human in the loop configuration
        ExecutionPlan.HumanInTheLoop.TeamsIntegration teamsIntegration = new ExecutionPlan.HumanInTheLoop.TeamsIntegration(
            "meeting-123", true, true
        );
        ExecutionPlan.HumanInTheLoop humanInTheLoop = new ExecutionPlan.HumanInTheLoop(
            true, 
            Arrays.asList("step-1", "step-5"),
            teamsIntegration
        );
        
        // Create and return the execution plan
        return new ExecutionPlan(
            "test-plan-1",
            "1.0.0",
            metadata,
            agents,
            executionFlow,
            sharedContext,
            humanInTheLoop
        );
    }
    
    private Agent createAgent(int id) {
        String agentId = "agent-" + id;
        String agentName = "Agent " + id;
        AgentType agentType = (id % 2 == 0) ? AgentType.CONTROL_PLANE : AgentType.DATA_PLANE;
        ProgrammingLanguage language = ProgrammingLanguage.JAVA;
        
        // Create capabilities based on agent type
        List<String> capabilities = new ArrayList<>();
        capabilities.add("capability-1");
        if (agentType == AgentType.CONTROL_PLANE) {
            capabilities.add("orchestration");
        } else {
            capabilities.add("data-processing");
        }
        
        // Create tools (1-3 tools per agent)
        List<Tool> tools = new ArrayList<>();
        int numTools = (id % 3) + 1; // 1-3 tools per agent
        
        for (int i = 1; i <= numTools; i++) {
            String toolId = "tool-" + id + "-" + i;
            String toolName = "Tool " + i + " for " + agentName;
            String description = "Description for " + toolName;
            
            // Create tool parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("timeout", 5000);
            parameters.put("retries", 3);
            
            // Create and add tool
            Tool tool = new Tool(toolId, toolName, ToolType.API);
            tool.setDescription(description);
            tools.add(tool);
        }
        
        // Create and configure the agent
        Agent agent = new Agent(agentId, agentName, agentType, language, capabilities);
        agent.setTools(tools);
        
        // Add dependencies for agents after the first one
        if (id > 1) {
            agent.setDependencies(Collections.singletonList("agent-" + (id - 1)));
        }
        
        return agent;
    }
}
