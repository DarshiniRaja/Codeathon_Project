package com.example.skillgap.controller;

import com.example.skillgap.dto.StudentSkillRequestDTO;
import com.example.skillgap.dto.StudentSkillResponseDTO;
import com.example.skillgap.service.StudentSkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{id}/skills")
@CrossOrigin(origins = "*")
public class StudentSkillController {

    private final StudentSkillService studentSkillService;

    public StudentSkillController(StudentSkillService studentSkillService) {
        this.studentSkillService = studentSkillService;
    }

    @PostMapping
    public ResponseEntity<StudentSkillResponseDTO> addSkill(
            @PathVariable("id") Long studentId,
            @RequestBody StudentSkillRequestDTO request) {
        StudentSkillResponseDTO response = studentSkillService.addStudentSkill(studentId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentSkillResponseDTO>> getSkills(@PathVariable("id") Long studentId) {
        List<StudentSkillResponseDTO> list = studentSkillService.getSkillsByStudentId(studentId);
        return ResponseEntity.ok(list);
    }
}
