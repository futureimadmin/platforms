package com.nebula.dataplane.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SchemaUtils {
    private static final String SCHEMA_JSON = "{\n" +
            "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
            "  \"title\": \"CodingOutput\",\n" +
            "  \"type\": \"object\",\n" +
            "  \"properties\": {\n" +
            "    \"codeFiles\": {\n" +
            "      \"type\": \"array\",\n" +
            "      \"items\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"properties\": {\n" +
            "          \"fileName\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"description\": \"Name of the file including extension (e.g., 'Test.java')\"\n" +
            "          },\n" +
            "          \"content\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"description\": \"The actual content of the file\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"required\": [\"fileName\", \"content\"],\n" +
            "        \"additionalProperties\": false\n" +
            "      },\n" +
            "      \"description\": \"List of code files to be processed\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"required\": [\"codeFiles\"],\n" +
            "  \"additionalProperties\": false\n" +
            "}";
    public static String loadSchemaAsString(String resourcePath) {
        try (InputStream is = SchemaUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                return SCHEMA_JSON;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return SCHEMA_JSON;
        }
    }
}