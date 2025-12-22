package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents an execution plan for agent orchestration in Nebula platform.
 * This model corresponds to the execution-plan.json schema.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionPlan {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$")
    @JsonProperty("planId")
    private String planId;

    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$")
    @JsonProperty("version")
    private String version;

    @NotNull
    @Valid
    @JsonProperty("metadata")
    private Metadata metadata;

    @NotEmpty
    @Valid
    @JsonProperty("agents")
    private List<Agent> agents;

    @NotNull
    @Valid
    @JsonProperty("executionFlow")
    private ExecutionFlow executionFlow;

    @JsonProperty("sharedContext")
    private SharedContext sharedContext;

    @JsonProperty("humanInTheLoop")
    private HumanInTheLoop humanInTheLoop;

    // Constructors
    public ExecutionPlan() {
    }

    public ExecutionPlan(String planId, String version, Metadata metadata,
                         List<Agent> agents, ExecutionFlow executionFlow,
                         SharedContext sharedContext, HumanInTheLoop humanInTheLoop) {
        this.planId = planId;
        this.version = version;
        this.metadata = metadata;
        this.agents = agents;
        this.executionFlow = executionFlow;
        this.sharedContext = sharedContext;
        this.humanInTheLoop = humanInTheLoop;
    }

    // Getters and Setters
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    public ExecutionFlow getExecutionFlow() {
        return executionFlow;
    }

    public void setExecutionFlow(ExecutionFlow executionFlow) {
        this.executionFlow = executionFlow;
    }

    public SharedContext getSharedContext() {
        return sharedContext;
    }

    public void setSharedContext(SharedContext sharedContext) {
        this.sharedContext = sharedContext;
    }

    public HumanInTheLoop getHumanInTheLoop() {
        return humanInTheLoop;
    }

    public void setHumanInTheLoop(HumanInTheLoop humanInTheLoop) {
        this.humanInTheLoop = humanInTheLoop;
    }

    /**
     * Metadata for the execution plan
     */
    public static class Metadata {
        @NotBlank
        @JsonProperty("name")
        private String name;

        @NotBlank
        @JsonProperty("description")
        private String description;

        @NotBlank
        @JsonProperty("createdBy")
        private String createdBy;

        @NotNull
        @JsonProperty("createdAt")
        private String createdAt;

        @JsonProperty("estimatedDuration")
        private String estimatedDuration;

        @JsonProperty("tags")
        private List<String> tags;

        // Constructors
        public Metadata() {
        }

        public Metadata(String name, String description, String createdBy, String createdAt) {
            this.name = name;
            this.description = description;
            this.createdBy = createdBy;
            this.createdAt = createdAt;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getEstimatedDuration() {
            return estimatedDuration;
        }

        public void setEstimatedDuration(String estimatedDuration) {
            this.estimatedDuration = estimatedDuration;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    /**
     * Shared context for all agents in the execution plan
     */
    public static class SharedContext {
        @JsonProperty("variables")
        private Variables variables;

        @NotEmpty
        @JsonProperty("secrets")
        private List<@NotBlank String> secrets;

        // Constructors
        public SharedContext() {
        }

        public SharedContext(Variables variables, List<String> secrets) {
            this.variables = variables;
            this.secrets = secrets;
        }

        // Getters and Setters
        public Variables getVariables() {
            return variables;
        }

        public void setVariables(Variables variables) {
            this.variables = variables;
        }

        public List<String> getSecrets() {
            return secrets;
        }

        public void setSecrets(List<String> secrets) {
            this.secrets = secrets;
        }

        /**
         * Variables for shared context
         */
        public static class Variables {
            @NotBlank
            @JsonProperty("environment")
            private String environment;

            @JsonProperty("version")
            private String version;

            @JsonProperty("config")
            private Map<String, Object> config;

            // Getters and Setters
            public String getEnvironment() {
                return environment;
            }

            public void setEnvironment(String environment) {
                this.environment = environment;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public Map<String, Object> getConfig() {
                return config;
            }

            public void setConfig(Map<String, Object> config) {
                this.config = config;
            }
        }
    }

    /**
     * Human-in-the-loop configuration
     */
    public static class HumanInTheLoop {
        @JsonProperty("enabled")
        private Boolean enabled = false;

        @JsonProperty("approvalRequired")
        private List<String> approvalRequired;

        @JsonProperty("teamsIntegration")
        private TeamsIntegration teamsIntegration;

        // Constructors
        public HumanInTheLoop() {
        }

        public HumanInTheLoop(Boolean enabled, List<String> approvalRequired, TeamsIntegration teamsIntegration) {
            this.enabled = enabled;
            this.approvalRequired = approvalRequired;
            this.teamsIntegration = teamsIntegration;
        }

        // Getters and Setters
        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getApprovalRequired() {
            return approvalRequired;
        }

        public void setApprovalRequired(List<String> approvalRequired) {
            this.approvalRequired = approvalRequired;
        }

        public TeamsIntegration getTeamsIntegration() {
            return teamsIntegration;
        }

        public void setTeamsIntegration(TeamsIntegration teamsIntegration) {
            this.teamsIntegration = teamsIntegration;
        }

        /**
         * Microsoft Teams integration configuration
         */
        public static class TeamsIntegration {
            @NotBlank
            @JsonProperty("meetingId")
            private String meetingId;

            @JsonProperty("speechToText")
            private Boolean speechToText;

            @JsonProperty("textToSpeech")
            private Boolean textToSpeech;

            // Constructors
            public TeamsIntegration() {
            }

            public TeamsIntegration(String meetingId, Boolean speechToText, Boolean textToSpeech) {
                this.meetingId = meetingId;
                this.speechToText = speechToText;
                this.textToSpeech = textToSpeech;
            }

            // Getters and Setters
            public String getMeetingId() {
                return meetingId;
            }

            public void setMeetingId(String meetingId) {
                this.meetingId = meetingId;
            }

            public Boolean getSpeechToText() {
                return speechToText;
            }

            public void setSpeechToText(Boolean speechToText) {
                this.speechToText = speechToText;
            }

            public Boolean getTextToSpeech() {
                return textToSpeech;
            }

            public void setTextToSpeech(Boolean textToSpeech) {
                this.textToSpeech = textToSpeech;
            }
        }
    }
}