package com.example.skillgap.dto;

import java.util.ArrayList;
import java.util.List;

public class StudentResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Integer skillCount;
    private List<StudentSkillResponseDTO> skills = new ArrayList<>();

    public StudentResponseDTO() {
    }

    public StudentResponseDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public StudentResponseDTO(Long id, String name, String email, Integer skillCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.skillCount = skillCount;
    }

    public StudentResponseDTO(Long id, String name, String email, List<StudentSkillResponseDTO> skills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.skills = skills != null ? skills : new ArrayList<>();
        this.skillCount = this.skills.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSkillCount() {
        return skillCount != null ? skillCount : (skills != null ? skills.size() : 0);
    }

    public void setSkillCount(Integer skillCount) {
        this.skillCount = skillCount;
    }

    public List<StudentSkillResponseDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<StudentSkillResponseDTO> skills) {
        this.skills = skills;
        this.skillCount = skills != null ? skills.size() : 0;
    }
}
