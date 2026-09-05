package com.example.skillgap.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillGapResponseDTO {

    private Long studentId;
    private String studentName;
    private Long jobId;
    private String jobTitle;
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

    public SkillGapResponseDTO(Long studentId, String studentName, Long jobId, String jobTitle, Double matchPercent, List<SkillGapItemDTO> skills) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getOverallMatchPercent() {
        return matchPercent != null ? (int) Math.round(matchPercent) : 0;
    }

    public List<SkillGapItemDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillGapItemDTO> skills) {
        this.skills = skills;
    }
}
