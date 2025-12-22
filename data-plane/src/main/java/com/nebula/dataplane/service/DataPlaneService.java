package com.nebula.dataplane.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.agents.LlmAgent;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.FunctionTool;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import com.nebula.dataplane.tool.AgentExecutorTool;
import com.nebula.dataplane.utils.SchemaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPlaneService {
    private static final String PLANS_COLLECTION = "executionPlans";
    
    private final Firestore firestore;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    /**
     * Fetches and processes the execution plan for the given planId
     * @param planId The ID of the execution plan to fetch
     * @return The execution flow as a JsonNode
     * @throws ExecutionException If there's an error fetching the document
     * @throws InterruptedException If the operation is interrupted
     * @throws IllegalStateException If the document doesn't exist or is invalid
     */
    public String generateExecutionFlowJobSources(String planId) throws ExecutionException, InterruptedException, IOException {
        log.info("Fetching execution plan for planId: {}", planId);
        Iterable<DocumentReference> listDocs = firestore.collection(PLANS_COLLECTION).listDocuments();
        DocumentReference docRef = firestore.collection(PLANS_COLLECTION).document(planId);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new IllegalStateException("Execution plan not found for id: " + planId);
        }
        
        try {
            // Load the prompt file from the classpath
            Resource resource = resourceLoader.getResource("classpath:execution-plan-prompt.txt");
            String prompt;
            try (InputStream inputStream = resource.getInputStream()) {
                prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
            log.info("Prompt loaded successfully.");

            // Convert Firestore document to Map
            Map<String, Object> planData = document.getData();
            if (planData == null || planData.isEmpty()) {
                throw new IllegalStateException("Empty execution plan data for id: " + planId);
            }
            
            Map<String, Object> executionFlow = planData.keySet().stream()
                    .filter(key -> key.equalsIgnoreCase("planData"))
                    .map(key -> planData.get(key))
                    .map(value -> getExecutionFlow(value))
                    .findFirst().orElse(null);

            LlmAgent agent = LlmAgent.builder()
                    .name("Data Plane Agent")
                    .description("Data Plane Agent that generates ExecutionFlow")
                    .model("gemini-2.5-pro")
                    .instruction(prompt)
                    .tools(List.of(FunctionTool
                            .create(AgentExecutorTool.class, "generateExecutorJobSources")))
                    .inputSchema(Schema.fromJson(SchemaUtils.loadSchemaAsString("input-schema.json")))
                    .build();
            String response = sendAndGetResponse(agent, planId);
            log.info("Successfully retrieved execution flow for planId: {}", planId);
            return response;
        } catch (Exception e) {
            log.error("Error processing execution plan for planId: " + planId, e);
            throw new IllegalStateException("Failed to process execution plan: " + e.getMessage(), e);
        }
    }

    private Map getExecutionFlow(Object value) {
        return (Map)((Map) value).get("executionFlow");
    }


    /**
     * Helper method to send a message and get the response as a string
     */
    public String sendAndGetResponse(LlmAgent agent, String runId) {
        InMemoryRunner runner = new InMemoryRunner(agent);
        Session session = runner.sessionService()
                .createSession(agent.name(), runId)
                .blockingGet();
        session.state().put("generated_code", "");
        session.state().put("planId", runId);
        session.state().put("generatedCode", "");

        // Use the agent's instruction as the user message
        String userMessage = agent.instruction().toString();

        Content userMsg = Content.fromParts(Part.fromText(userMessage));

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
}
