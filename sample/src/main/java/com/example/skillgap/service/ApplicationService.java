package com.example.skillgap.service;

import com.example.skillgap.dto.ApplicationRequestDTO;
import com.example.skillgap.dto.ApplicationResponseDTO;
import com.example.skillgap.dto.SkillGapResponseDTO;
import com.example.skillgap.entity.Application;
import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.Student;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentService studentService;
    private final JobService jobService;
    private final SkillGapService skillGapService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              StudentService studentService,
                              JobService jobService,
                              SkillGapService skillGapService) {
        this.applicationRepository = applicationRepository;
        this.studentService = studentService;
        this.jobService = jobService;
        this.skillGapService = skillGapService;
    }

    @Transactional
    public ApplicationResponseDTO createApplication(ApplicationRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        if (request.getStudentId() == null) {
            throw new BadRequestException("Student ID cannot be null");
        }
        if (request.getJobId() == null) {
            throw new BadRequestException("Job ID cannot be null");
        }

        // 1. Verify student exists (throws 404 if not found)
        Student student = studentService.getStudentEntityById(request.getStudentId());

        // 2. Verify job exists (throws 404 if not found)
        Job job = jobService.getJobEntityById(request.getJobId());

        // 3. Prevent duplicate application
        if (applicationRepository.existsByStudentIdAndJobId(student.getId(), job.getId())) {
            throw new DuplicateResourceException(
                    "Student with id " + student.getId() + " has already applied to job " + job.getId()
            );
        }

        // 4. Calculate current match percentage via SkillGapService
        SkillGapResponseDTO gapResult = skillGapService.calculateSkillGap(student.getId(), job.getId());
        Double matchPercent = gapResult.getMatchPercent();

        // 5. Store application
        Application application = new Application(student, job, matchPercent, "APPLIED");
        Application saved = applicationRepository.save(application);

        return new ApplicationResponseDTO(
                saved.getId(),
                student.getId(),
                student.getName(),
                job.getId(),
                job.getTitle(),
                saved.getMatchPercent(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(app -> new ApplicationResponseDTO(
                        app.getId(),
                        app.getStudent().getId(),
                        app.getStudent().getName(),
                        app.getJob().getId(),
                        app.getJob().getTitle(),
                        app.getMatchPercent(),
                        app.getStatus(),
                        app.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO getApplicationById(Long id) {
        if (id == null) {
            throw new BadRequestException("Application ID cannot be null");
        }
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application with id " + id + " not found"));
        return new ApplicationResponseDTO(
                app.getId(),
                app.getStudent().getId(),
                app.getStudent().getName(),
                app.getJob().getId(),
                app.getJob().getTitle(),
                app.getMatchPercent(),
                app.getStatus(),
                app.getCreatedAt()
        );
    }
}
