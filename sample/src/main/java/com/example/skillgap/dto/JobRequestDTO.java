package com.example.skillgap.dto;

public class JobRequestDTO {

    private String title;
    private String department;
    private String description;

    public JobRequestDTO() {
    }

    public JobRequestDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public JobRequestDTO(String title, String department, String description) {
        this.title = title;
        this.department = department;
        this.description = description;
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
}
