package com.example.skillgap.service;

import com.example.skillgap.dto.StudentSkillRequestDTO;
import com.example.skillgap.dto.StudentSkillResponseDTO;
import com.example.skillgap.entity.Skill;
import com.example.skillgap.entity.Student;
import com.example.skillgap.entity.StudentSkill;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.StudentSkillRepository;
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
public class StudentSkillServiceTest {

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private StudentSkillService studentSkillService;

    private Student testStudent;
    private Skill testSkill;

    @BeforeEach
    void setUp() {
        testStudent = new Student(1L, "Ravi Kumar", "ravi@example.com");
        testSkill = new Skill(10L, "Java");
    }

    @Test
    @DisplayName("Successfully add student skill with proficiency 1 and 5 (boundaries)")
    void testAddStudentSkillSuccess() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(skillService.getSkillEntityById(10L)).thenReturn(testSkill);
        when(studentSkillRepository.existsByStudentIdAndSkillId(1L, 10L)).thenReturn(false);

        StudentSkill saved = new StudentSkill(100L, testStudent, testSkill, 5);
        when(studentSkillRepository.save(any(StudentSkill.class))).thenReturn(saved);

        StudentSkillResponseDTO res = studentSkillService.addStudentSkill(1L, new StudentSkillRequestDTO(10L, 5));

        assertNotNull(res);
        assertEquals(100L, res.getId());
        assertEquals(1L, res.getStudentId());
        assertEquals(10L, res.getSkillId());
        assertEquals("Java", res.getSkillName());
        assertEquals(5, res.getProficiency());
    }

    @Test
    @DisplayName("Reject invalid proficiency: 0 and 6")
    void testInvalidProficiencyRejected() {
        assertThrows(BadRequestException.class, () ->
                studentSkillService.addStudentSkill(1L, new StudentSkillRequestDTO(10L, 0)));

        assertThrows(BadRequestException.class, () ->
                studentSkillService.addStudentSkill(1L, new StudentSkillRequestDTO(10L, 6)));
    }

    @Test
    @DisplayName("Reject duplicate skill assignment to student")
    void testDuplicateStudentSkillRejected() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(skillService.getSkillEntityById(10L)).thenReturn(testSkill);
        when(studentSkillRepository.existsByStudentIdAndSkillId(1L, 10L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                studentSkillService.addStudentSkill(1L, new StudentSkillRequestDTO(10L, 4)));
    }

    @Test
    @DisplayName("Reject missing student (404)")
    void testMissingStudentThrowsNotFound() {
        when(studentService.getStudentEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Student with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                studentSkillService.addStudentSkill(999L, new StudentSkillRequestDTO(10L, 4)));
    }

    @Test
    @DisplayName("Reject missing skill (404)")
    void testMissingSkillThrowsNotFound() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(skillService.getSkillEntityById(999L))
                .thenThrow(new ResourceNotFoundException("Skill with id 999 not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                studentSkillService.addStudentSkill(1L, new StudentSkillRequestDTO(999L, 4)));
    }
}
