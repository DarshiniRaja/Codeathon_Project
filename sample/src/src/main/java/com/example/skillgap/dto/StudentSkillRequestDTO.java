package com.example.skillgap.dto;

public class StudentSkillRequestDTO {

    private Long skillId;
    private String skill;
    private Integer proficiency;

    public StudentSkillRequestDTO() {
    }

    public StudentSkillRequestDTO(Long skillId, Integer proficiency) {
        this.skillId = skillId;
        this.proficiency = proficiency;
    }

    public StudentSkillRequestDTO(String skill, Integer proficiency) {
        this.skill = skill;
        this.proficiency = proficiency;
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

    public Integer getProficiency() {
        return proficiency;
    }

    public void setProficiency(Integer proficiency) {
        this.proficiency = proficiency;
    }
}
