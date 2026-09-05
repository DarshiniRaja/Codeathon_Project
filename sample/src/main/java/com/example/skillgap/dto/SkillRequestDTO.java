package com.example.skillgap.dto;

public class SkillRequestDTO {

    private String name;

    public SkillRequestDTO() {
    }

    public SkillRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
