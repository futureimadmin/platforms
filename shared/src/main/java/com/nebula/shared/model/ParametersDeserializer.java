package com.nebula.shared.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Custom deserializer for handling both array and map formats for parameters.
 */
public class ParametersDeserializer extends JsonDeserializer<Map<String, Object>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        // Check if the current token is an array
        if (p.currentToken() == JsonToken.START_ARRAY) {
            // Convert array to a map with numeric keys
            ArrayNode arrayNode = objectMapper.readTree(p);
            Iterator<JsonNode> elements = arrayNode.elements();
            int index = 0;
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                result.put(String.valueOf(index++), objectMapper.treeToValue(element, Object.class));
            }
        } else if (p.currentToken() == JsonToken.START_OBJECT) {
            // Handle regular object
            result = objectMapper.readValue(p, Map.class);
        }
        
        return result;
    }
}
