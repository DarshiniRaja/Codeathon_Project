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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private JobService jobService;

    @Mock
    private SkillGapService skillGapService;

    @InjectMocks
    private ApplicationService applicationService;

    private Student testStudent;
    private Job testJob;

    @BeforeEach
    void setUp() {
        testStudent = new Student(1L, "Ravi Kumar", "ravi@example.com");
        testJob = new Job(10L, "Java Backend Developer", "Spring Boot development");
    }

    @Test
    @DisplayName("Create valid application and store correct match percentage")
    void testCreateValidApplication() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);
        when(applicationRepository.existsByStudentIdAndJobId(1L, 10L)).thenReturn(false);

        when(skillGapService.calculateSkillGap(1L, 10L))
                .thenReturn(new SkillGapResponseDTO(1L, 10L, 87.0, Collections.emptyList()));

        Application savedApp = new Application(25L, testStudent, testJob, 87.0, "APPLIED");
        when(applicationRepository.save(any(Application.class))).thenReturn(savedApp);

        ApplicationResponseDTO response = applicationService.createApplication(new ApplicationRequestDTO(1L, 10L));

        assertNotNull(response);
        assertEquals(25L, response.getId());
        assertEquals(1L, response.getStudentId());
        assertEquals(10L, response.getJobId());
        assertEquals(87.0, response.getMatchPercent());
        assertEquals("APPLIED", response.getStatus());

        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    @DisplayName("Reject duplicate application")
    void testDuplicateApplicationRejected() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);
        when(applicationRepository.existsByStudentIdAndJobId(1L, 10L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                applicationService.createApplication(new ApplicationRequestDTO(1L, 10L))
        );
    }

    @Test
    @DisplayName("Reject application with invalid student (404)")
    void testApplicationInvalidStudent() {
        when(studentService.getStudentEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Student with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                applicationService.createApplication(new ApplicationRequestDTO(999L, 10L))
        );
    }

    @Test
    @DisplayName("Reject application with invalid job (404)")
    void testApplicationInvalidJob() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Job with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                applicationService.createApplication(new ApplicationRequestDTO(1L, 999L))
        );
    }

    @Test
    @DisplayName("Reject null application request")
    void testNullApplicationRequest() {
        assertThrows(BadRequestException.class, () -> applicationService.createApplication(null));
        assertThrows(BadRequestException.class, () -> applicationService.createApplication(new ApplicationRequestDTO(null, 10L)));
        assertThrows(BadRequestException.class, () -> applicationService.createApplication(new ApplicationRequestDTO(1L, null)));
    }
}
