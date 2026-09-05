package com.example.skillgap.dto;

public class ApplicationRequestDTO {

    private Long studentId;
    private Long jobId;

    public ApplicationRequestDTO() {
    }

    public ApplicationRequestDTO(Long studentId, Long jobId) {
        this.studentId = studentId;
        this.jobId = jobId;
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
}
