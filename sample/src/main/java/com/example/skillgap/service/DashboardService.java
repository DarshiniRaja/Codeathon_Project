package com.example.skillgap.service;

import com.example.skillgap.dto.DashboardResponseDTO;
import com.example.skillgap.dto.SkillGapItemDTO;
import com.example.skillgap.dto.SkillGapResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.Student;
import com.example.skillgap.repository.ApplicationRepository;
import com.example.skillgap.repository.JobRepository;
import com.example.skillgap.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final SkillGapService skillGapService;

    public DashboardService(StudentRepository studentRepository,
                            JobRepository jobRepository,
                            ApplicationRepository applicationRepository,
                            SkillGapService skillGapService) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.skillGapService = skillGapService;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboardMetrics() {
        long totalStudents = studentRepository.count();
        long totalJobs = jobRepository.count();
        long totalApplications = applicationRepository.count();

        List<Student> students = studentRepository.findAll();
        List<Job> jobs = jobRepository.findAll();

        List<Double> matchPercentages = new ArrayList<>();
        Map<String, Long> gapCountBySkill = new HashMap<>();

        for (Student student : students) {
            for (Job job : jobs) {
                try {
                    SkillGapResponseDTO gapResult = skillGapService.calculateSkillGap(student.getId(), job.getId());
                    if (gapResult != null) {
                        matchPercentages.add(gapResult.getMatchPercent());
                        for (SkillGapItemDTO item : gapResult.getSkills()) {
                            if ("GAP".equalsIgnoreCase(item.getStatus()) || item.getCurrentLevel() < item.getRequiredLevel()) {
                                gapCountBySkill.put(item.getSkill(), gapCountBySkill.getOrDefault(item.getSkill(), 0L) + 1);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }

        Double averageSkillMatch = null;
        if (!matchPercentages.isEmpty()) {
            double sum = matchPercentages.stream().mapToDouble(Double::doubleValue).sum();
            double avg = sum / matchPercentages.size();
            averageSkillMatch = BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue();
        }

        List<DashboardResponseDTO.TopSkillGapDTO> topSkillGaps = gapCountBySkill.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(e -> new DashboardResponseDTO.TopSkillGapDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new DashboardResponseDTO(
                totalStudents,
                totalJobs,
                totalApplications,
                averageSkillMatch,
                topSkillGaps
        );
    }
}
