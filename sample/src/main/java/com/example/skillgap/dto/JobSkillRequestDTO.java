package com.example.skillgap.dto;

public class JobSkillRequestDTO {

    private Long skillId;
    private String skill;
    private Integer requiredLevel;
    private Boolean mandatory = false;

    public JobSkillRequestDTO() {
    }

    public JobSkillRequestDTO(Long skillId, Integer requiredLevel, Boolean mandatory) {
        this.skillId = skillId;
        this.requiredLevel = requiredLevel;
        this.mandatory = mandatory != null ? mandatory : false;
    }

    public JobSkillRequestDTO(String skill, Integer requiredLevel, Boolean mandatory) {
        this.skill = skill;
        this.requiredLevel = requiredLevel;
        this.mandatory = mandatory != null ? mandatory : false;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(Integer requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
}
