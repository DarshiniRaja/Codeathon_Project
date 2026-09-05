package com.example.skillgap.controller;

import com.example.skillgap.dto.LoginRequestDTO;
import com.example.skillgap.dto.LoginResponseDTO;
import com.example.skillgap.entity.Student;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final StudentRepository studentRepository;

    public AuthController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required for login");
        }

        String email = request.getEmail().trim().toLowerCase();
        String role = "student".equalsIgnoreCase(request.getRole()) ? "student" : "admin";
        String token = "sga-token-" + UUID.randomUUID();

        Long studentId = null;
        String name = "Admin User";

        if ("student".equalsIgnoreCase(role)) {
            Optional<Student> studentOpt = studentRepository.findByEmail(email);
            if (studentOpt.isPresent()) {
                Student s = studentOpt.get();
                studentId = s.getId();
                name = s.getName();
            } else {
                name = email.split("@")[0];
            }
        }

        LoginResponseDTO.UserDTO userDTO = new LoginResponseDTO.UserDTO(
                studentId != null ? studentId : 1L,
                name,
                email,
                role,
                studentId
        );

        return ResponseEntity.ok(new LoginResponseDTO(token, userDTO));
    }
}
