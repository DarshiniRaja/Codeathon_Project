package com.example.skillgap.dto;

public class JobSkillResponseDTO {

    private Long id;
    private Long jobId;
    private Long skillId;
    private String skillName;
    private Integer requiredLevel;
    private Boolean mandatory;

    public JobSkillResponseDTO() {
    }

    public JobSkillResponseDTO(Long id, Long jobId, Long skillId, String skillName, Integer requiredLevel, Boolean mandatory) {
        this.id = id;
        this.jobId = jobId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.requiredLevel = requiredLevel;
        this.mandatory = mandatory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
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

    public String getSkill() {
        return skillName;
    }

    public void setSkill(String skill) {
        this.skillName = skill;
    }
}
