package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Microsoft Teams integration configuration for human-in-the-loop workflows
 */
public class TeamsIntegration {
    @JsonProperty("meetingId")
    private String meetingId;
    
    @JsonProperty("speechToText")
    private Boolean speechToText;
    
    @JsonProperty("textToSpeech")
    private Boolean textToSpeech;
    
    // Constructors
    public TeamsIntegration() {}
    
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
