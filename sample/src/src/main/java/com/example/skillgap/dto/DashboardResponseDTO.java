package com.example.skillgap.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardResponseDTO {

    private Long totalStudents;
    private Long totalJobs;
    private Long totalApplications;
    private Double averageSkillMatch;
    private List<TopSkillGapDTO> topSkillGaps = new ArrayList<>();

    public DashboardResponseDTO() {
    }

    public DashboardResponseDTO(Long totalStudents, Long totalJobs, Long totalApplications, Double averageSkillMatch, List<TopSkillGapDTO> topSkillGaps) {
        this.totalStudents = totalStudents;
        this.totalJobs = totalJobs;
        this.totalApplications = totalApplications;
        this.averageSkillMatch = averageSkillMatch;
        this.topSkillGaps = topSkillGaps != null ? topSkillGaps : new ArrayList<>();
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(Long totalJobs) {
        this.totalJobs = totalJobs;
    }

    public Long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(Long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public Double getAverageSkillMatch() {
        return averageSkillMatch;
    }

    public void setAverageSkillMatch(Double averageSkillMatch) {
        this.averageSkillMatch = averageSkillMatch;
    }

    public List<TopSkillGapDTO> getTopSkillGaps() {
        return topSkillGaps;
    }

    public void setTopSkillGaps(List<TopSkillGapDTO> topSkillGaps) {
        this.topSkillGaps = topSkillGaps;
    }

    public static class TopSkillGapDTO {
        private String skill;
        private Long gapCount;

        public TopSkillGapDTO() {
        }

        public TopSkillGapDTO(String skill, Long gapCount) {
            this.skill = skill;
            this.gapCount = gapCount;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public Long getGapCount() {
            return gapCount;
        }

        public void setGapCount(Long gapCount) {
            this.gapCount = gapCount;
        }
    }
}
