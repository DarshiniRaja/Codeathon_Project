package com.example.skillgap.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillGapResponseDTO {

    private Long studentId;
    private Long jobId;
    private Double matchPercent;
    private List<SkillGapItemDTO> skills = new ArrayList<>();

    public SkillGapResponseDTO() {
    }

    public SkillGapResponseDTO(Long studentId, Long jobId, Double matchPercent, List<SkillGapItemDTO> skills) {
        this.studentId = studentId;
        this.jobId = jobId;
        this.matchPercent = matchPercent;
        this.skills = skills != null ? skills : new ArrayList<>();
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

    public List<SkillGapItemDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillGapItemDTO> skills) {
        this.skills = skills;
    }
}
