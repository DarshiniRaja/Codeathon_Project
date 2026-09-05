package com.example.skillgap.service;

import com.example.skillgap.dto.SkillGapItemDTO;
import com.example.skillgap.dto.SkillGapResponseDTO;
import com.example.skillgap.entity.*;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.JobSkillRepository;
import com.example.skillgap.repository.StudentSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillGapCalculationTest {

    @Mock
    private StudentService studentService;

    @Mock
    private JobService jobService;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @Mock
    private JobSkillRepository jobSkillRepository;

    @InjectMocks
    private SkillGapService skillGapService;

    private Student testStudent;
    private Job testJob;
    private Skill javaSkill;
    private Skill sqlSkill;
    private Skill htmlSkill;

    @BeforeEach
    void setUp() {
        testStudent = new Student(1L, "Ravi Kumar", "ravi@example.com");
        testJob = new Job(10L, "Java Backend Developer", "Spring Boot development");

        javaSkill = new Skill(100L, "Java");
        sqlSkill = new Skill(101L, "SQL");
        htmlSkill = new Skill(102L, "HTML");
    }

    @Test
    @DisplayName("Case 1: All skills matched -> matchPercent = 100.0%")
    void testAllSkillsMatched() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        when(studentSkillRepository.findByStudentId(1L)).thenReturn(List.of(
                new StudentSkill(testStudent, javaSkill, 5),
                new StudentSkill(testStudent, sqlSkill, 5)
        ));

        when(jobSkillRepository.findByJobId(10L)).thenReturn(List.of(
                new JobSkill(testJob, javaSkill, 5, true),
                new JobSkill(testJob, sqlSkill, 4, false)
        ));

        SkillGapResponseDTO response = skillGapService.calculateSkillGap(1L, 10L);

        assertNotNull(response);
        assertEquals(100.0, response.getMatchPercent());
        assertEquals(2, response.getSkills().size());

        assertTrue(response.getSkills().stream().allMatch(s -> "MATCHED".equals(s.getStatus()) && s.getGap() == 0));
    }

    @Test
    @DisplayName("Case 2: No skills matched -> student has none of the required skills")
    void testNoSkillsMatched() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        when(studentSkillRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());

        when(jobSkillRepository.findByJobId(10L)).thenReturn(List.of(
                new JobSkill(testJob, javaSkill, 5, true),
                new JobSkill(testJob, sqlSkill, 4, false)
        ));

        SkillGapResponseDTO response = skillGapService.calculateSkillGap(1L, 10L);

        assertNotNull(response);
        assertEquals(0.0, response.getMatchPercent());
        assertEquals(2, response.getSkills().size());

        SkillGapItemDTO javaItem = response.getSkills().stream()
                .filter(s -> "Java".equals(s.getSkill())).findFirst().orElseThrow();
        assertEquals(0, javaItem.getCurrentLevel());
        assertEquals(5, javaItem.getRequiredLevel());
        assertEquals(5, javaItem.getGap());
        assertEquals("GAP", javaItem.getStatus());
        assertTrue(javaItem.getMandatory());
    }

    @Test
    @DisplayName("Case 3: Formula verification from Section 27 specification")
    void testSection27FormulaExample() {
        // Java: current 4, req 5, mandatory = true (ach: 0.8, weight: 2, score: 1.6)
        // SQL: current 5, req 5, mandatory = true (ach: 1.0, weight: 2, score: 2.0)
        // HTML: current 3, req 4, mandatory = false (ach: 0.75, weight: 1, score: 0.75)
        // Total score = 1.6 + 2.0 + 0.75 = 4.35
        // Total weight = 2 + 2 + 1 = 5
        // matchPercent = (4.35 / 5) * 100 = 87.0%
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        when(studentSkillRepository.findByStudentId(1L)).thenReturn(List.of(
                new StudentSkill(testStudent, javaSkill, 4),
                new StudentSkill(testStudent, sqlSkill, 5),
                new StudentSkill(testStudent, htmlSkill, 3)
        ));

        when(jobSkillRepository.findByJobId(10L)).thenReturn(List.of(
                new JobSkill(testJob, javaSkill, 5, true),
                new JobSkill(testJob, sqlSkill, 5, true),
                new JobSkill(testJob, htmlSkill, 4, false)
        ));

        SkillGapResponseDTO response = skillGapService.calculateSkillGap(1L, 10L);

        assertNotNull(response);
        assertEquals(87.0, response.getMatchPercent());
        assertEquals(3, response.getSkills().size());
    }

    @Test
    @DisplayName("Case 4 & 5: Exact match and higher level -> gap = 0, status = MATCHED")
    void testExactAndHigherLevel() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        when(studentSkillRepository.findByStudentId(1L)).thenReturn(List.of(
                new StudentSkill(testStudent, javaSkill, 4), // exact match
                new StudentSkill(testStudent, sqlSkill, 5)   // higher than required (req=3)
        ));

        when(jobSkillRepository.findByJobId(10L)).thenReturn(List.of(
                new JobSkill(testJob, javaSkill, 4, true),
                new JobSkill(testJob, sqlSkill, 3, false)
        ));

        SkillGapResponseDTO response = skillGapService.calculateSkillGap(1L, 10L);

        assertEquals(100.0, response.getMatchPercent());
        for (SkillGapItemDTO item : response.getSkills()) {
            assertEquals(0, item.getGap());
            assertEquals("MATCHED", item.getStatus());
        }
    }

    @Test
    @DisplayName("Case 6 & 7: Lower level and missing skill -> gap = required - current")
    void testLowerLevelAndMissingSkill() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        when(studentSkillRepository.findByStudentId(1L)).thenReturn(List.of(
                new StudentSkill(testStudent, javaSkill, 2) // lower: req 5, gap 3
                // SQL is completely missing: req 4, gap 4
        ));

        when(jobSkillRepository.findByJobId(10L)).thenReturn(List.of(
                new JobSkill(testJob, javaSkill, 5, true),
                new JobSkill(testJob, sqlSkill, 4, false)
        ));

        SkillGapResponseDTO response = skillGapService.calculateSkillGap(1L, 10L);

        SkillGapItemDTO javaItem = response.getSkills().stream()
                .filter(s -> "Java".equals(s.getSkill())).findFirst().orElseThrow();
        assertEquals(3, javaItem.getGap());
        assertEquals("GAP", javaItem.getStatus());

        SkillGapItemDTO sqlItem = response.getSkills().stream()
                .filter(s -> "SQL".equals(s.getSkill())).findFirst().orElseThrow();
        assertEquals(0, sqlItem.getCurrentLevel());
        assertEquals(4, sqlItem.getGap());
        assertEquals("GAP", sqlItem.getStatus());
    }

    @Test
    @DisplayName("Case 8: Invalid student ID -> throws ResourceNotFoundException (404)")
    void testInvalidStudentThrowsNotFound() {
        when(studentService.getStudentEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Student with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () -> skillGapService.calculateSkillGap(999L, 10L));
    }

    @Test
    @DisplayName("Case 9: Invalid job ID -> throws ResourceNotFoundException (404)")
    void testInvalidJobThrowsNotFound() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Job with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () -> skillGapService.calculateSkillGap(1L, 999L));
    }

    @Test
    @DisplayName("Case 10: Job has no required skills -> returns matchPercent = 0.0 and empty skills list")
    void testJobWithNoRequiredSkills() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);
        when(studentSkillRepository.findByStudentId(1L)).thenReturn(Collections.emptyList());
        when(jobSkillRepository.findByJobId(10L)).thenReturn(Collections.emptyList());

        SkillGapResponseDTO response = skillGapService.calculateSkillGap(1L, 10L);

        assertNotNull(response);
        assertEquals(0.0, response.getMatchPercent());
        assertTrue(response.getSkills().isEmpty());
    }

    @Test
    @DisplayName("Case 11: Student has extra skills -> extra skills are ignored")
    void testExtraStudentSkillsIgnored() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        Skill python = new Skill(103L, "Python");
        when(studentSkillRepository.findByStudentId(1L)).thenReturn(List.of(
                new StudentSkill(testStudent, javaSkill, 5),
                new StudentSkill(testStudent, python, 5) // Not required by job
        ));

        when(jobSkillRepository.findByJobId(10L)).thenReturn(List.of(
                new JobSkill(testJob, javaSkill, 5, true)
        ));

        SkillGapResponseDTO response = skillGapService.calculateSkillGap(1L, 10L);

        assertEquals(1, response.getSkills().size());
        assertEquals("Java", response.getSkills().get(0).getSkill());
        assertEquals(100.0, response.getMatchPercent());
    }

    @Test
    @DisplayName("Null arguments validation")
    void testNullArguments() {
        assertThrows(BadRequestException.class, () -> skillGapService.calculateSkillGap(null, 10L));
        assertThrows(BadRequestException.class, () -> skillGapService.calculateSkillGap(1L, null));
    }
}
