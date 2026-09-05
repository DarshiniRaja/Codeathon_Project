package com.example.skillgap.dto;

public class RecommendationResponseDTO {

    private String skill;
    private Integer currentLevel;
    private Integer requiredLevel;
    private String priority;
    private String reason;

    public RecommendationResponseDTO() {
    }

    public RecommendationResponseDTO(String skill, Integer currentLevel, Integer requiredLevel, String priority, String reason) {
        this.skill = skill;
        this.currentLevel = currentLevel;
        this.requiredLevel = requiredLevel;
        this.priority = priority;
        this.reason = reason;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(Integer requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
