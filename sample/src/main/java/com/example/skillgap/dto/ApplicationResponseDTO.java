package com.example.skillgap.dto;

import java.time.LocalDateTime;

public class ApplicationResponseDTO {

    private Long id;
    private Long studentId;
    private Long jobId;
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

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
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
