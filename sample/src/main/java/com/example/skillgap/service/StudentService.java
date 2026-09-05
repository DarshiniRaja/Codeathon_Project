package com.example.skillgap.service;

import com.example.skillgap.dto.StudentRequestDTO;
import com.example.skillgap.dto.StudentResponseDTO;
import com.example.skillgap.entity.Student;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Student name cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Student email cannot be empty");
        }
        String email = request.getEmail().trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Student email has an invalid format");
        }
        if (studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Student with email " + email + " already exists");
        }

        Student student = new Student(request.getName().trim(), email);
        Student saved = studentRepository.save(student);
        return new StudentResponseDTO(saved.getId(), saved.getName(), saved.getEmail());
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentResponseDTO(s.getId(), s.getName(), s.getEmail()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = getStudentEntityById(id);
        return new StudentResponseDTO(student.getId(), student.getName(), student.getEmail());
    }

    @Transactional(readOnly = true)
    public Student getStudentEntityById(Long id) {
        if (id == null) {
            throw new BadRequestException("Student ID cannot be null");
        }
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id " + id + " not found"));
    }
}
