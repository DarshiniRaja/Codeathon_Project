package com.example.skillgap.service;

import com.example.skillgap.dto.JobSkillRequestDTO;
import com.example.skillgap.dto.JobSkillResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.JobSkill;
import com.example.skillgap.entity.Skill;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.JobSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobSkillServiceTest {

    @Mock
    private JobSkillRepository jobSkillRepository;

    @Mock
    private JobService jobService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private JobSkillService jobSkillService;

    private Job testJob;
    private Skill testSkill;

    @BeforeEach
    void setUp() {
        testJob = new Job(10L, "Java Developer", "Description");
        testSkill = new Skill(100L, "Java");
    }

    @Test
    @DisplayName("Successfully add job skill with mandatory flag")
    void testAddJobSkillSuccess() {
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);
        when(skillService.getSkillEntityById(100L)).thenReturn(testSkill);
        when(jobSkillRepository.existsByJobIdAndSkillId(10L, 100L)).thenReturn(false);

        JobSkill saved = new JobSkill(50L, testJob, testSkill, 5, true);
        when(jobSkillRepository.save(any(JobSkill.class))).thenReturn(saved);

        JobSkillResponseDTO res = jobSkillService.addJobSkill(10L, new JobSkillRequestDTO(100L, 5, true));

        assertNotNull(res);
        assertEquals(50L, res.getId());
        assertEquals(10L, res.getJobId());
        assertEquals(100L, res.getSkillId());
        assertEquals(5, res.getRequiredLevel());
        assertTrue(res.getMandatory());
    }

    @Test
    @DisplayName("Reject invalid required level: 0 and 6")
    void testInvalidRequiredLevelRejected() {
        assertThrows(BadRequestException.class, () ->
                jobSkillService.addJobSkill(10L, new JobSkillRequestDTO(100L, 0, true)));

        assertThrows(BadRequestException.class, () ->
                jobSkillService.addJobSkill(10L, new JobSkillRequestDTO(100L, 6, true)));
    }

    @Test
    @DisplayName("Reject duplicate job skill")
    void testDuplicateJobSkillRejected() {
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);
        when(skillService.getSkillEntityById(100L)).thenReturn(testSkill);
        when(jobSkillRepository.existsByJobIdAndSkillId(10L, 100L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                jobSkillService.addJobSkill(10L, new JobSkillRequestDTO(100L, 4, false)));
    }

    @Test
    @DisplayName("Reject missing job (404)")
    void testMissingJobThrowsNotFound() {
        when(jobService.getJobEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Job with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                jobSkillService.addJobSkill(999L, new JobSkillRequestDTO(100L, 4, false)));
    }

    @Test
    @DisplayName("Reject missing skill (404)")
    void testMissingSkillThrowsNotFound() {
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);
        when(skillService.getSkillEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Skill with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                jobSkillService.addJobSkill(10L, new JobSkillRequestDTO(999L, 4, false)));
    }
}
