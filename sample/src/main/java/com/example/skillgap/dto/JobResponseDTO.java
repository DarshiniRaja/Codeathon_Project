package com.example.skillgap.dto;

import java.util.ArrayList;
import java.util.List;

public class JobResponseDTO {

    private Long id;
    private String title;
    private String department;
    private String description;
    private Integer requiredSkillCount;
    private List<JobSkillResponseDTO> requiredSkills = new ArrayList<>();

    public JobResponseDTO() {
    }

    public JobResponseDTO(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public JobResponseDTO(Long id, String title, String description, List<JobSkillResponseDTO> requiredSkills) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.requiredSkillCount = this.requiredSkills.size();
    }

    public JobResponseDTO(Long id, String title, String department, String description, List<JobSkillResponseDTO> requiredSkills) {
        this.id = id;
        this.title = title;
        this.department = department;
        this.description = description;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.requiredSkillCount = this.requiredSkills.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRequiredSkillCount() {
        return requiredSkillCount != null ? requiredSkillCount : (requiredSkills != null ? requiredSkills.size() : 0);
    }

    public void setRequiredSkillCount(Integer requiredSkillCount) {
        this.requiredSkillCount = requiredSkillCount;
    }

    public List<JobSkillResponseDTO> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<JobSkillResponseDTO> requiredSkills) {
        this.requiredSkills = requiredSkills;
        this.requiredSkillCount = requiredSkills != null ? requiredSkills.size() : 0;
    }
}
