package com.example.skillgap.dto;

public class StudentSkillRequestDTO {

    private Long skillId;
    private Integer proficiency;

    public StudentSkillRequestDTO() {
    }

    public StudentSkillRequestDTO(Long skillId, Integer proficiency) {
        this.skillId = skillId;
        this.proficiency = proficiency;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Integer getProficiency() {
        return proficiency;
    }

    public void setProficiency(Integer proficiency) {
        this.proficiency = proficiency;
    }
}
