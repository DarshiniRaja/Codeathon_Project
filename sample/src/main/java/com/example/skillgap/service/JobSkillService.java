package com.example.skillgap.service;

import com.example.skillgap.dto.JobSkillRequestDTO;
import com.example.skillgap.dto.JobSkillResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.JobSkill;
import com.example.skillgap.entity.Skill;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.repository.JobSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSkillService {

    private final JobSkillRepository jobSkillRepository;
    private final JobService jobService;
    private final SkillService skillService;

    public JobSkillService(JobSkillRepository jobSkillRepository,
                           JobService jobService,
                           SkillService skillService) {
        this.jobSkillRepository = jobSkillRepository;
        this.jobService = jobService;
        this.skillService = skillService;
    }

    @Transactional
    public JobSkillResponseDTO addJobSkill(Long jobId, JobSkillRequestDTO request) {
        if (jobId == null) {
            throw new BadRequestException("Job ID cannot be null");
        }
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        if (request.getSkillId() == null) {
            throw new BadRequestException("Skill ID cannot be null");
        }
        if (request.getRequiredLevel() == null) {
            throw new BadRequestException("Required level cannot be null");
        }
        if (request.getRequiredLevel() < 1 || request.getRequiredLevel() > 5) {
            throw new BadRequestException("Required level must be between 1 and 5");
        }

        Job job = jobService.getJobEntityById(jobId);
        Skill skill = skillService.getSkillEntityById(request.getSkillId());

        if (jobSkillRepository.existsByJobIdAndSkillId(jobId, request.getSkillId())) {
            throw new DuplicateResourceException("Skill already assigned to this job");
        }

        JobSkill jobSkill = new JobSkill(
                job,
                skill,
                request.getRequiredLevel(),
                request.getMandatory() != null ? request.getMandatory() : false
        );
        JobSkill saved = jobSkillRepository.save(jobSkill);

        return new JobSkillResponseDTO(
                saved.getId(),
                job.getId(),
                skill.getId(),
                skill.getName(),
                saved.getRequiredLevel(),
                saved.getMandatory()
        );
    }

    @Transactional(readOnly = true)
    public List<JobSkillResponseDTO> getSkillsByJobId(Long jobId) {
        // Ensure job exists (throws 404 if not found)
        jobService.getJobEntityById(jobId);

        return jobSkillRepository.findByJobId(jobId).stream()
                .map(js -> new JobSkillResponseDTO(
                        js.getId(),
                        js.getJob().getId(),
                        js.getSkill().getId(),
                        js.getSkill().getName(),
                        js.getRequiredLevel(),
                        js.getMandatory()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobSkill> getJobSkillEntities(Long jobId) {
        jobService.getJobEntityById(jobId);
        return jobSkillRepository.findByJobId(jobId);
    }
}
