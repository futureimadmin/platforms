package com.nebula.shared.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for agent communication service.
 * Provides methods for agents to communicate with each other and share context.
 */
public interface AgentCommunicationService {
    
    /**
     * Send a message from one agent to another
     */
    CompletableFuture<Void> sendMessage(String fromAgentId, String toAgentId, String message, Map<String, Object> context);
    
    /**
     * Broadcast a message to all agents in the execution plan
     */
    CompletableFuture<Void> broadcastMessage(String fromAgentId, String message, Map<String, Object> context);
    
    /**
     * Subscribe to messages for a specific agent
     */
    void subscribeToMessages(String agentId, MessageHandler messageHandler);
    
    /**
     * Unsubscribe from messages for a specific agent
     */
    void unsubscribeFromMessages(String agentId);
    
    /**
     * Update shared context variable
     */
    void updateSharedContext(String key, Object value);
    
    /**
     * Get shared context variable
     */
    Object getSharedContext(String key);
    
    /**
     * Get all shared context variables
     */
    Map<String, Object> getAllSharedContext();
    
    /**
     * Publish an event to the event bus
     */
    CompletableFuture<Void> publishEvent(String eventType, String agentId, Map<String, Object> eventData);
    
    /**
     * Subscribe to events of a specific type
     */
    void subscribeToEvents(String eventType, EventHandler eventHandler);
    
    /**
     * Unsubscribe from events of a specific type
     */
    void unsubscribeFromEvents(String eventType, EventHandler eventHandler);
    
    /**
     * Interface for handling incoming messages
     */
    @FunctionalInterface
    interface MessageHandler {
        void handleMessage(String fromAgentId, String message, Map<String, Object> context);
    }
    
    /**
     * Interface for handling events
     */
    @FunctionalInterface
    interface EventHandler {
        void handleEvent(String eventType, String agentId, Map<String, Object> eventData);
    }
}