package com.nebula.controlplane.service;

import com.nebula.controlplane.config.ExecutionPlanConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class LLMServiceIntegrationTest {

    @Autowired
    private LLMService llmService;

    @BeforeEach
    void setUp() {
        // Any setup that needs to be done before each test
    }

    @Test
    void callGemini_WithValidPrompts_ShouldReturnResponse() {
        // Given
        String systemPrompt = llmService.buildExecutionPlanSystemPrompt();
        String userPrompt = "Create an execution plan for a data processing pipeline with Data Validation, Execute Rules, Create Summary, Create Statement as steps in the pipeline";
        String userMessage = llmService.buildExecutionPlanUserMessage(userPrompt, Collections.emptyMap());

        // When
        String result = llmService.callGemini(systemPrompt, userMessage);

        // Then
        assertNotNull(result, "Response from callGemini should not be null");
        assertFalse(result.isBlank(), "Response should not be empty or blank");

        // Additional assertions based on expected response structure
        assertTrue(result.contains("executionPlan") ||
                        result.contains("agents") ||
                        result.contains("steps"),
                "Response should contain expected structure");
    }

    @Test
    void callGemini_WithEmptySystemPrompt_ShouldHandleGracefully() {
        // Given
        String systemPrompt = "";
        String userPrompt = "Create a simple execution plan";
        String userMessage = llmService.buildExecutionPlanUserMessage(userPrompt, Collections.emptyMap());

        // When & Then
        assertDoesNotThrow(() -> {
            String result = llmService.callGemini(systemPrompt, userMessage);
            assertNotNull(result, "Response should not be null even with empty system prompt");
        });
    }

    @Test
    void callGemini_WithEmptyUserMessage_ShouldHandleGracefully() {
        // Given
        String systemPrompt = llmService.buildExecutionPlanSystemPrompt();
        String userMessage = "";

        // When & Then
        assertDoesNotThrow(() -> {
            String result = llmService.callGemini(systemPrompt, userMessage);
            assertNotNull(result, "Response should not be null even with empty user message");
        });
    }

    @Test
    void callGemini_WithRealisticPrompt_ShouldReturnStructuredResponse() {
        // Given
        String systemPrompt = """
            You are an AI assistant that helps create execution plans.
            Always respond with a JSON structure containing an 'agents' array and 'steps' array.
            """;

        String userPrompt = """
            Create a data processing pipeline that:
            1. Fetches data from an API
            2. Processes the data
            3. Stores results in a database
            """;

        String userMessage = llmService.buildExecutionPlanUserMessage(userPrompt, Collections.emptyMap());

        // When
        String result = llmService.callGemini(systemPrompt, userMessage);

        // Then
        assertNotNull(result, "Response should not be null");
        assertTrue(result.trim().startsWith("{") || result.trim().startsWith("["),
                "Response should be a JSON object or array");
    }
}
