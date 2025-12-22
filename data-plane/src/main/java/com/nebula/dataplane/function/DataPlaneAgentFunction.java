package com.nebula.dataplane.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nebula.dataplane.service.AgentExecutionService;
import com.nebula.dataplane.service.DataPlaneService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP Cloud Function for processing agent execution requests.
 *
 * This function can be deployed to Google Cloud Functions or Cloud Run.
 * It expects a 'planId' query parameter to fetch the execution plan from Firestore.
 */
public class DataPlaneAgentFunction {
//        implements HttpFunction {
    private static final Logger logger = Logger.getLogger(DataPlaneAgentFunction.class.getName());
    private static final Gson gson = new Gson();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String PLAN_ID_PARAM = "planId";

    private final AgentExecutionService agentService;
    private final DataPlaneService dataPlaneService;

    @Autowired
    public DataPlaneAgentFunction(AgentExecutionService agentService, DataPlaneService dataPlaneService) {
        this.agentService = agentService;
        this.dataPlaneService = dataPlaneService;
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        try (BufferedWriter writer = response.getWriter()) {
            // Set default response headers
            response.setContentType(APPLICATION_JSON);

            // Only allow POST method
            if (!"POST".equals(request.getMethod())) {
                response.setStatusCode(405); // Method Not Allowed
                writer.write(createErrorResponse("Method not allowed. Only POST is supported.", null));
                return;
            }

            try {
                // Get planId from query parameters
                String planId = getRequiredParameter(request, PLAN_ID_PARAM);

                // Get execution plan from Firestore
                String executionPlan = dataPlaneService.generateExecutionFlowJobSources(planId);

                // Process the execution plan
                Map<String, Object> result = new HashMap<>();
//                        processExecutionPlan(executionPlan);

                // Send success response
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("status", "SUCCESS");
                responseJson.add("executionPlan", gson.toJsonTree(executionPlan));
                responseJson.add("result", gson.toJsonTree(result));

                response.setStatusCode(200);
                writer.write(responseJson.toString());

            } catch (IllegalArgumentException e) {
                response.setStatusCode(400); // Bad Request
                writer.write(createErrorResponse(e.getMessage(), e));
            } catch (ExecutionException | InterruptedException e) {
                response.setStatusCode(404); // Not Found
                writer.write(createErrorResponse("Execution plan not found", e));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing request", e);
                response.setStatusCode(500); // Internal Server Error
                writer.write(createErrorResponse("Internal server error", e));
            }
        }
    }

    private Map<String, Object> processExecutionPlan(JsonNode executionPlan) {
        // TODO: Implement actual execution plan processing
        // This is a placeholder for the actual implementation
        return Map.of(
                "status", "PENDING",
                "message", "Execution plan processing not yet implemented"
        );
    }

    private String getRequiredParameter(HttpRequest request, String paramName) {
        return Optional.ofNullable(request.getFirstQueryParameter(paramName))
                .get()
                .orElseThrow(() -> new IllegalArgumentException("Missing required parameter: " + paramName));
    }

    private String createErrorResponse(String message, Throwable throwable) {
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("status", "ERROR");
        errorResponse.addProperty("message", message);
        if (throwable != null) {
            errorResponse.addProperty("error", throwable.getMessage());
        }
        return errorResponse.toString();
    }
}
