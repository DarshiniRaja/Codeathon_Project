package com.example.skillgap.dto;

import java.util.ArrayList;
import java.util.List;

public class JobResponseDTO {

    private Long id;
    private String title;
    private String description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JobSkillResponseDTO> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<JobSkillResponseDTO> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
