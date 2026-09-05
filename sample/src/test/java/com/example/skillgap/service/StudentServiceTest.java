package com.example.skillgap.service;

import com.example.skillgap.dto.StudentRequestDTO;
import com.example.skillgap.dto.StudentResponseDTO;
import com.example.skillgap.entity.Student;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Successfully create student")
    void testCreateStudentSuccess() {
        StudentRequestDTO req = new StudentRequestDTO("Ravi Kumar", "ravi@example.com");
        Student saved = new Student(1L, "Ravi Kumar", "ravi@example.com");

        when(studentRepository.existsByEmail("ravi@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(saved);

        StudentResponseDTO res = studentService.createStudent(req);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Ravi Kumar", res.getName());
        assertEquals("ravi@example.com", res.getEmail());
    }

    @Test
    @DisplayName("Reject duplicate student email")
    void testCreateStudentDuplicateEmail() {
        StudentRequestDTO req = new StudentRequestDTO("Ravi Kumar", "ravi@example.com");
        when(studentRepository.existsByEmail("ravi@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.createStudent(req));
    }

    @Test
    @DisplayName("Reject invalid email format")
    void testCreateStudentInvalidEmail() {
        StudentRequestDTO req = new StudentRequestDTO("Ravi Kumar", "invalid-email-format");
        assertThrows(BadRequestException.class, () -> studentService.createStudent(req));
    }

    @Test
    @DisplayName("Reject empty student name")
    void testCreateStudentEmptyName() {
        StudentRequestDTO req = new StudentRequestDTO("", "ravi@example.com");
        assertThrows(BadRequestException.class, () -> studentService.createStudent(req));
    }

    @Test
    @DisplayName("Retrieve student by ID")
    void testGetStudentById() {
        Student student = new Student(1L, "Ravi Kumar", "ravi@example.com");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentResponseDTO res = studentService.getStudentById(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
    }

    @Test
    @DisplayName("Retrieve missing student throws 404")
    void testGetMissingStudentThrowsNotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(999L));
    }
}
