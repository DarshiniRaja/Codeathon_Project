package com.example.skillgap.dto;

import java.time.LocalDateTime;

public class ApplicationResponseDTO {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long jobId;
    private String jobTitle;
    private Double matchPercent;
    private String status;
    private LocalDateTime createdAt;

    public ApplicationResponseDTO() {
    }

    public ApplicationResponseDTO(Long id, Long studentId, Long jobId, Double matchPercent, String status) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.matchPercent = matchPercent;
        this.status = status;
    }

    public ApplicationResponseDTO(Long id, Long studentId, Long jobId, Double matchPercent, String status, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.matchPercent = matchPercent;
        this.status = status;
        this.createdAt = createdAt;
    }

    public ApplicationResponseDTO(Long id, Long studentId, String studentName, Long jobId, String jobTitle, Double matchPercent, String status, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.matchPercent = matchPercent;
        this.status = status;
        this.createdAt = createdAt;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Double getMatchPercent() {
        return matchPercent;
    }

    public void setMatchPercent(Double matchPercent) {
        this.matchPercent = matchPercent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
