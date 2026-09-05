package com.example.skillgap.service;

import com.example.skillgap.dto.SkillGapItemDTO;
import com.example.skillgap.dto.SkillGapResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.JobSkill;
import com.example.skillgap.entity.Student;
import com.example.skillgap.entity.StudentSkill;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.repository.JobSkillRepository;
import com.example.skillgap.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillGapService {

    private final StudentService studentService;
    private final JobService jobService;
    private final StudentSkillRepository studentSkillRepository;
    private final JobSkillRepository jobSkillRepository;

    public SkillGapService(StudentService studentService,
                           JobService jobService,
                           StudentSkillRepository studentSkillRepository,
                           JobSkillRepository jobSkillRepository) {
        this.studentService = studentService;
        this.jobService = jobService;
        this.studentSkillRepository = studentSkillRepository;
        this.jobSkillRepository = jobSkillRepository;
    }

    /**
     * Calculates the skill gap and overall match percentage for a given student and job.
     *
     * Gap formula:
     *   gap = max(requiredLevel - currentLevel, 0)
     *   status = MATCHED if currentLevel >= requiredLevel else GAP
     *
     * Match percentage formula:
     *   achievement = min(currentLevel / requiredLevel, 1.0)
     *   mandatory weight = 2, optional weight = 1
     *   weightedScore = sum(achievement * weight)
     *   matchPercent = (weightedScore / totalWeight) * 100, rounded to 2 decimal places
     */
    @Transactional(readOnly = true)
    public SkillGapResponseDTO calculateSkillGap(Long studentId, Long jobId) {
        if (studentId == null) {
            throw new BadRequestException("Student ID cannot be null");
        }
        if (jobId == null) {
            throw new BadRequestException("Job ID cannot be null");
        }

        // 1. Verify student exists (throws 404 ResourceNotFoundException if missing)
        Student student = studentService.getStudentEntityById(studentId);

        // 2. Verify job exists (throws 404 ResourceNotFoundException if missing)
        Job job = jobService.getJobEntityById(jobId);

        // 3. Retrieve student skills
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentId(student.getId());
        Map<Long, Integer> studentSkillLevelBySkillId = studentSkills.stream()
                .collect(Collectors.toMap(
                        ss -> ss.getSkill().getId(),
                        StudentSkill::getProficiency,
                        (existing, replacement) -> existing
                ));

        // 4. Retrieve job required skills
        List<JobSkill> jobSkills = jobSkillRepository.findByJobId(job.getId());

        // 5. Handle case where job has no required skills
        if (jobSkills.isEmpty()) {
            return new SkillGapResponseDTO(studentId, student.getName(), jobId, job.getTitle(), 0.0, Collections.emptyList());
        }

        List<SkillGapItemDTO> items = new ArrayList<>();
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;

        for (JobSkill js : jobSkills) {
            Long skillId = js.getSkill().getId();
            String skillName = js.getSkill().getName();
            int requiredLevel = js.getRequiredLevel();
            boolean mandatory = js.getMandatory() != null && js.getMandatory();

            // 6. Treat missing student skill as level 0
            int currentLevel = studentSkillLevelBySkillId.getOrDefault(skillId, 0);

            // 7. Calculate gap and status
            int gap = Math.max(requiredLevel - currentLevel, 0);
            String status = currentLevel >= requiredLevel ? "MATCHED" : "GAP";

            // 8. Match percentage components
            double achievement = Math.min((double) currentLevel / (double) requiredLevel, 1.0);
            double weight = mandatory ? 2.0 : 1.0;

            totalWeightedScore += (achievement * weight);
            totalWeight += weight;

            items.add(new SkillGapItemDTO(
                    skillName,
                    currentLevel,
                    requiredLevel,
                    gap,
                    status,
                    mandatory
            ));
        }

        // 9. Overall match percentage calculation
        double matchPercent = 0.0;
        if (totalWeight > 0) {
            double rawPercent = (totalWeightedScore / totalWeight) * 100.0;
            matchPercent = BigDecimal.valueOf(rawPercent)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new SkillGapResponseDTO(studentId, student.getName(), jobId, job.getTitle(), matchPercent, items);
    }
}
