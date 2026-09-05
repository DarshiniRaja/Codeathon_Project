package com.example.skillgap.dto;

public class SkillGapItemDTO {

    private String skill;
    private Integer currentLevel;
    private Integer requiredLevel;
    private Integer gap;
    private String status;
    private Boolean mandatory;

    public SkillGapItemDTO() {
    }

    public SkillGapItemDTO(String skill, Integer currentLevel, Integer requiredLevel, Integer gap, String status, Boolean mandatory) {
        this.skill = skill;
        this.currentLevel = currentLevel;
        this.requiredLevel = requiredLevel;
        this.gap = gap;
        this.status = status;
        this.mandatory = mandatory;
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

    public Integer getGap() {
        return gap;
    }

    public void setGap(Integer gap) {
        this.gap = gap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
}
