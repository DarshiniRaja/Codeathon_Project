package com.example.skillgap.service;

import com.example.skillgap.dto.JobRequestDTO;
import com.example.skillgap.dto.JobResponseDTO;
import com.example.skillgap.dto.JobSkillResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.JobRepository;
import com.example.skillgap.repository.JobSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;

    public JobService(JobRepository jobRepository, JobSkillRepository jobSkillRepository) {
        this.jobRepository = jobRepository;
        this.jobSkillRepository = jobSkillRepository;
    }

    @Transactional
    public JobResponseDTO createJob(JobRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Job title cannot be empty");
        }

        Job job = new Job(request.getTitle().trim(), request.getDescription());
        Job saved = jobRepository.save(job);
        return new JobResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription());
    }

    @Transactional(readOnly = true)
    public List<JobResponseDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(job -> {
                    List<JobSkillResponseDTO> skillDTOs = jobSkillRepository.findByJobId(job.getId()).stream()
                            .map(js -> new JobSkillResponseDTO(
                                    js.getId(),
                                    job.getId(),
                                    js.getSkill().getId(),
                                    js.getSkill().getName(),
                                    js.getRequiredLevel(),
                                    js.getMandatory()
                            ))
                            .collect(Collectors.toList());
                    return new JobResponseDTO(job.getId(), job.getTitle(), job.getDescription(), skillDTOs);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JobResponseDTO getJobById(Long id) {
        Job job = getJobEntityById(id);
        List<JobSkillResponseDTO> skillDTOs = jobSkillRepository.findByJobId(job.getId()).stream()
                .map(js -> new JobSkillResponseDTO(
                        js.getId(),
                        job.getId(),
                        js.getSkill().getId(),
                        js.getSkill().getName(),
                        js.getRequiredLevel(),
                        js.getMandatory()
                ))
                .collect(Collectors.toList());

        return new JobResponseDTO(job.getId(), job.getTitle(), job.getDescription(), skillDTOs);
    }

    @Transactional(readOnly = true)
    public Job getJobEntityById(Long id) {
        if (id == null) {
            throw new BadRequestException("Job ID cannot be null");
        }
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job with id " + id + " not found"));
    }
}
