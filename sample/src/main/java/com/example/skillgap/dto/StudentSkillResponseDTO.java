package com.example.skillgap.dto;

public class StudentSkillResponseDTO {

    private Long id;
    private Long studentId;
    private Long skillId;
    private String skillName;
    private Integer proficiency;

    public StudentSkillResponseDTO() {
    }

    public StudentSkillResponseDTO(Long id, Long studentId, Long skillId, String skillName, Integer proficiency) {
        this.id = id;
        this.studentId = studentId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.proficiency = proficiency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public Integer getProficiency() {
        return proficiency;
    }

    public void setProficiency(Integer proficiency) {
        this.proficiency = proficiency;
    }

    public String getSkill() {
        return skillName;
    }

    public void setSkill(String skill) {
        this.skillName = skill;
    }
}
