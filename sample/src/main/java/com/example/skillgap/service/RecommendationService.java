package com.example.skillgap.service;

import com.example.skillgap.dto.RecommendationResponseDTO;
import com.example.skillgap.dto.SkillGapItemDTO;
import com.example.skillgap.dto.SkillGapResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.Recommendation;
import com.example.skillgap.entity.Student;
import com.example.skillgap.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final SkillGapService skillGapService;
    private final StudentService studentService;
    private final JobService jobService;
    private final RecommendationRepository recommendationRepository;

    public RecommendationService(SkillGapService skillGapService,
                                  StudentService studentService,
                                  JobService jobService,
                                  RecommendationRepository recommendationRepository) {
        this.skillGapService = skillGapService;
        this.studentService = studentService;
        this.jobService = jobService;
        this.recommendationRepository = recommendationRepository;
    }

    /**
     * Generates recommendations for skills where currentLevel < requiredLevel.
     * Priority rules:
     *   Mandatory skill gap -> HIGH
     *   Optional skill gap -> MEDIUM
     */
    @Transactional
    public List<RecommendationResponseDTO> generateRecommendations(Long studentId, Long jobId) {
        // SkillGapService validates that student and job exist and computes gaps
        SkillGapResponseDTO gapResponse = skillGapService.calculateSkillGap(studentId, jobId);

        Student student = studentService.getStudentEntityById(studentId);
        Job job = jobService.getJobEntityById(jobId);

        // Clear existing persisted recommendations for this pair before re-generating
        recommendationRepository.deleteByStudentIdAndJobId(studentId, jobId);

        List<RecommendationResponseDTO> result = new ArrayList<>();
        List<Recommendation> toSave = new ArrayList<>();

        for (SkillGapItemDTO item : gapResponse.getSkills()) {
            if ("GAP".equalsIgnoreCase(item.getStatus()) || item.getCurrentLevel() < item.getRequiredLevel()) {
                String priority = Boolean.TRUE.equals(item.getMandatory()) ? "HIGH" : "MEDIUM";
                int gap = item.getGap();
                String levelSuffix = gap == 1 ? " level." : " levels.";
                String reason = "Current " + item.getSkill() + " level is " + item.getCurrentLevel()
                        + " but the required level is " + item.getRequiredLevel()
                        + ". Improve " + item.getSkill() + " proficiency by " + gap + levelSuffix;

                result.add(new RecommendationResponseDTO(
                        item.getSkill(),
                        item.getCurrentLevel(),
                        item.getRequiredLevel(),
                        priority,
                        reason
                ));

                toSave.add(new Recommendation(
                        student,
                        job,
                        item.getSkill(),
                        item.getCurrentLevel(),
                        item.getRequiredLevel(),
                        priority,
                        reason
                ));
            }
        }

        if (!toSave.isEmpty()) {
            recommendationRepository.saveAll(toSave);
        }

        return result;
    }
}
