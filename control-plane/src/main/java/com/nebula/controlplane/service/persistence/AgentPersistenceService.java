package com.nebula.controlplane.service.persistence;

import com.nebula.controlplane.repository.AgentRepository;
import com.nebula.shared.domain.AgentDocument;
import com.nebula.shared.enums.AgentType;
import com.nebula.shared.model.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for Agent persistence operations
 */
@Service
public class AgentPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(AgentPersistenceService.class);

    private final AgentRepository agentRepository;

    @Autowired
    public AgentPersistenceService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    /**
     * Create a new agent
     */
    public AgentDocument createAgent(Agent agent) throws ExecutionException, InterruptedException {
        logger.info("Creating new agent: {}", agent.getName());
        
        AgentDocument agentDoc = convertToDocument(agent);
        agentDoc.setCreatedAt(Instant.now());
        agentDoc.setUpdatedAt(Instant.now());
        agentDoc.setStatus("CREATED");
        
        return agentRepository.save(agentDoc);
    }

    /**
     * Update an existing agent
     */
    public AgentDocument updateAgent(AgentDocument agentDoc) throws ExecutionException, InterruptedException {
        logger.info("Updating agent: {}", agentDoc.getId());
        
        agentDoc.setUpdatedAt(Instant.now());
        return agentRepository.save(agentDoc);
    }

    /**
     * Get agent by ID
     */
    public Optional<AgentDocument> getAgentById(String agentId) throws ExecutionException, InterruptedException {
        logger.debug("Retrieving agent by ID: {}", agentId);
        return agentRepository.findById(agentId);
    }

    /**
     * Get all agents
     */
    public List<AgentDocument> getAllAgents() throws ExecutionException, InterruptedException {
        logger.debug("Retrieving all agents");
        return agentRepository.findAll();
    }

    /**
     * Get agents by plan ID
     */
    public List<AgentDocument> getAgentsByPlanId(String planId) throws ExecutionException, InterruptedException {
        logger.debug("Retrieving agents for plan: {}", planId);
        return agentRepository.findByPlanId(planId);
    }

    /**
     * Get agents by type
     */
    public List<AgentDocument> getAgentsByType(AgentType type) throws ExecutionException, InterruptedException {
        logger.debug("Retrieving agents by type: {}", type);
        return agentRepository.findByType(type);
    }

    /**
     * Get agents by status
     */
    public List<AgentDocument> getAgentsByStatus(String status) throws ExecutionException, InterruptedException {
        logger.debug("Retrieving agents by status: {}", status);
        return agentRepository.findByStatus(status);
    }

    /**
     * Get active agents for a plan
     */
    public List<AgentDocument> getActiveAgentsByPlanId(String planId) throws ExecutionException, InterruptedException {
        logger.debug("Retrieving active agents for plan: {}", planId);
        return agentRepository.findActiveAgentsByPlanId(planId);
    }

    /**
     * Update agent status
     */
    public void updateAgentStatus(String agentId, String status) throws ExecutionException, InterruptedException {
        logger.info("Updating agent status: {} to {}", agentId, status);
        agentRepository.updateStatus(agentId, status);
    }

    /**
     * Activate agent
     */
    public void activateAgent(String agentId) throws ExecutionException, InterruptedException {
        logger.info("Activating agent: {}", agentId);
        agentRepository.updateStatus(agentId, "ACTIVE");
    }

    /**
     * Deactivate agent
     */
    public void deactivateAgent(String agentId) throws ExecutionException, InterruptedException {
        logger.info("Deactivating agent: {}", agentId);
        agentRepository.updateStatus(agentId, "INACTIVE");
    }

    /**
     * Mark agent as busy
     */
    public void markAgentBusy(String agentId) throws ExecutionException, InterruptedException {
        logger.info("Marking agent as busy: {}", agentId);
        agentRepository.updateStatus(agentId, "BUSY");
        agentRepository.incrementExecutionCount(agentId);
    }

    /**
     * Mark agent as available
     */
    public void markAgentAvailable(String agentId) throws ExecutionException, InterruptedException {
        logger.info("Marking agent as available: {}", agentId);
        agentRepository.updateStatus(agentId, "AVAILABLE");
    }

    /**
     * Record agent execution
     */
    public void recordAgentExecution(String agentId, boolean success) throws ExecutionException, InterruptedException {
        logger.info("Recording execution for agent: {} - success: {}", agentId, success);
        
        Optional<AgentDocument> agentOpt = agentRepository.findById(agentId);
        if (agentOpt.isPresent()) {
            AgentDocument agent = agentOpt.get();
            agent.incrementExecutionCount();
            agent.updateSuccessRate(success);
            agentRepository.save(agent);
        }
    }

    /**
     * Delete agent
     */
    public void deleteAgent(String agentId) throws ExecutionException, InterruptedException {
        logger.info("Deleting agent: {}", agentId);
        agentRepository.deleteById(agentId);
    }

    /**
     * Check if agent exists
     */
    public boolean agentExists(String agentId) throws ExecutionException, InterruptedException {
        return agentRepository.existsById(agentId);
    }

    /**
     * Get agent count
     */
    public long getAgentCount() throws ExecutionException, InterruptedException {
        return agentRepository.count();
    }

    /**
     * Get agent count by plan
     */
    public long getAgentCountByPlan(String planId) throws ExecutionException, InterruptedException {
        return agentRepository.countByPlanId(planId);
    }

    /**
     * Convert Agent model to AgentDocument
     */
    private AgentDocument convertToDocument(Agent agent) {
        AgentDocument doc = new AgentDocument();
        doc.setName(agent.getName());
        doc.setType(agent.getType());
        // Note: Agent model doesn't have description field, using name as description
        doc.setDescription(agent.getName());
        doc.setPlanId(agent.getPlanId());
        doc.setCapabilities(agent.getCapabilities());
        // Convert Tool objects to ToolType enums
        if (agent.getTools() != null) {
            List<com.nebula.shared.enums.ToolType> toolTypes = agent.getTools().stream()
                    .map(tool -> tool.getType())
                    .collect(java.util.stream.Collectors.toList());
            doc.setTools(toolTypes);
        }
        return doc;
    }

    /**
     * Convert AgentDocument to Agent model
     */
    public Agent convertToModel(AgentDocument doc) {
        Agent agent = new Agent();
        agent.setAgentId(doc.getId());
        agent.setName(doc.getName());
        agent.setType(doc.getType());
        agent.setPlanId(doc.getPlanId());
        agent.setCapabilities(doc.getCapabilities());
        // Convert ToolType enums to Tool objects
        if (doc.getTools() != null) {
            List<com.nebula.shared.model.Tool> tools = doc.getTools().stream()
                    .map(toolType -> {
                        com.nebula.shared.model.Tool tool = new com.nebula.shared.model.Tool();
                        tool.setType(toolType);
                        tool.setName(toolType.name());
                        return tool;
                    })
                    .collect(java.util.stream.Collectors.toList());
            agent.setTools(tools);
        }
        return agent;
    }

    /**
     * Convert list of AgentDocuments to Agent models
     */
    public List<Agent> convertToModels(List<AgentDocument> documents) {
        return documents.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * Get agent statistics
     */
    public AgentStatistics getAgentStatistics() throws ExecutionException, InterruptedException {
        logger.debug("Calculating agent statistics");
        
        List<AgentDocument> allAgents = agentRepository.findAll();
        
        long totalAgents = allAgents.size();
        long activeAgents = allAgents.stream()
                .filter(agent -> "ACTIVE".equals(agent.getStatus()) || "AVAILABLE".equals(agent.getStatus()))
                .count();
        long busyAgents = allAgents.stream()
                .filter(agent -> "BUSY".equals(agent.getStatus()) || "RUNNING".equals(agent.getStatus()))
                .count();
        long inactiveAgents = allAgents.stream()
                .filter(agent -> "INACTIVE".equals(agent.getStatus()))
                .count();
        
        double averageSuccessRate = allAgents.stream()
                .filter(agent -> agent.getSuccessRate() != null)
                .mapToDouble(AgentDocument::getSuccessRate)
                .average()
                .orElse(0.0);
        
        long totalExecutions = allAgents.stream()
                .filter(agent -> agent.getExecutionCount() != null)
                .mapToLong(AgentDocument::getExecutionCount)
                .sum();
        
        return new AgentStatistics(totalAgents, activeAgents, busyAgents, inactiveAgents, 
                averageSuccessRate, totalExecutions);
    }

    /**
     * Agent statistics data class
     */
    public static class AgentStatistics {
        private final long totalAgents;
        private final long activeAgents;
        private final long busyAgents;
        private final long inactiveAgents;
        private final double averageSuccessRate;
        private final long totalExecutions;

        public AgentStatistics(long totalAgents, long activeAgents, long busyAgents, 
                             long inactiveAgents, double averageSuccessRate, long totalExecutions) {
            this.totalAgents = totalAgents;
            this.activeAgents = activeAgents;
            this.busyAgents = busyAgents;
            this.inactiveAgents = inactiveAgents;
            this.averageSuccessRate = averageSuccessRate;
            this.totalExecutions = totalExecutions;
        }

        // Getters
        public long getTotalAgents() { return totalAgents; }
        public long getActiveAgents() { return activeAgents; }
        public long getBusyAgents() { return busyAgents; }
        public long getInactiveAgents() { return inactiveAgents; }
        public double getAverageSuccessRate() { return averageSuccessRate; }
        public long getTotalExecutions() { return totalExecutions; }
    }
}