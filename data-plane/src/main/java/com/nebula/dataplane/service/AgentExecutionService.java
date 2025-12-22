package com.nebula.dataplane.service;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for executing agent operations.
 */
@Service
public class AgentExecutionService {
    
    // In-memory store for agent registrations (in production, use a proper service discovery)
    private final Map<String, AgentExecutor> agentRegistry = new ConcurrentHashMap<>();

    /**
     * Execute an agent operation with the given parameters.
     *
     * @param agentId    The ID of the agent to execute
     * @param operation  The operation to perform
     * @param parameters The parameters for the operation
     * @return The result of the agent execution
     */
    public Map<String, Object> executeAgent(String agentId, String operation, JsonObject parameters) {
        AgentExecutor executor = agentRegistry.get(agentId);
        if (executor == null) {
            throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        
        try {
            return executor.execute(operation, parameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute agent operation: " + e.getMessage(), e);
        }
    }

    /**
     * Register an agent executor.
     *
     * @param agentId  The ID of the agent
     * @param executor The executor for the agent
     */
    public void registerAgent(String agentId, AgentExecutor executor) {
        agentRegistry.put(agentId, executor);
    }

    /**
     * Interface for agent executors.
     */
    public interface AgentExecutor {
        /**
         * Execute an agent operation.
         *
         * @param operation  The operation to perform
         * @param parameters The parameters for the operation
         * @return The result of the operation
         */
        Map<String, Object> execute(String operation, JsonObject parameters);
    }
}
