package com.example.skillgap.service;

import com.example.skillgap.dto.JobRequestDTO;
import com.example.skillgap.dto.JobResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.JobRepository;
import com.example.skillgap.repository.JobSkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobSkillRepository jobSkillRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    @DisplayName("Successfully create job")
    void testCreateJobSuccess() {
        Job job = new Job(1L, "Java Developer", "Description");
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        JobResponseDTO res = jobService.createJob(new JobRequestDTO("Java Developer", "Description"));

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Java Developer", res.getTitle());
    }

    @Test
    @DisplayName("Reject empty job title")
    void testEmptyJobTitleRejected() {
        assertThrows(BadRequestException.class, () ->
                jobService.createJob(new JobRequestDTO("", "Description")));
    }

    @Test
    @DisplayName("Retrieve job by ID")
    void testGetJobById() {
        Job job = new Job(1L, "Java Developer", "Description");
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobSkillRepository.findByJobId(1L)).thenReturn(Collections.emptyList());

        JobResponseDTO res = jobService.getJobById(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Java Developer", res.getTitle());
    }

    @Test
    @DisplayName("Retrieve missing job throws 404")
    void testGetMissingJobThrowsNotFound() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.getJobById(999L));
    }
}
