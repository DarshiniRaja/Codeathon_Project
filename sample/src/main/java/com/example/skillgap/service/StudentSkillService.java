package com.example.skillgap.service;

import com.example.skillgap.dto.StudentSkillRequestDTO;
import com.example.skillgap.dto.StudentSkillResponseDTO;
import com.example.skillgap.entity.Skill;
import com.example.skillgap.entity.Student;
import com.example.skillgap.entity.StudentSkill;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentSkillService {

    private final StudentSkillRepository studentSkillRepository;
    private final StudentService studentService;
    private final SkillService skillService;

    public StudentSkillService(StudentSkillRepository studentSkillRepository,
                               StudentService studentService,
                               SkillService skillService) {
        this.studentSkillRepository = studentSkillRepository;
        this.studentService = studentService;
        this.skillService = skillService;
    }

    @Transactional
    public StudentSkillResponseDTO addStudentSkill(Long studentId, StudentSkillRequestDTO request) {
        if (studentId == null) {
            throw new BadRequestException("Student ID cannot be null");
        }
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        if (request.getSkillId() == null) {
            throw new BadRequestException("Skill ID cannot be null");
        }
        if (request.getProficiency() == null) {
            throw new BadRequestException("Proficiency cannot be null");
        }
        if (request.getProficiency() < 1 || request.getProficiency() > 5) {
            throw new BadRequestException("Proficiency must be between 1 and 5");
        }

        Student student = studentService.getStudentEntityById(studentId);
        Skill skill = skillService.getSkillEntityById(request.getSkillId());

        if (studentSkillRepository.existsByStudentIdAndSkillId(studentId, request.getSkillId())) {
            throw new DuplicateResourceException("Skill already assigned to this student");
        }

        StudentSkill studentSkill = new StudentSkill(student, skill, request.getProficiency());
        StudentSkill saved = studentSkillRepository.save(studentSkill);

        return new StudentSkillResponseDTO(
                saved.getId(),
                student.getId(),
                skill.getId(),
                skill.getName(),
                saved.getProficiency()
        );
    }

    @Transactional(readOnly = true)
    public List<StudentSkillResponseDTO> getSkillsByStudentId(Long studentId) {
        // Ensure student exists (throws 404 if not found)
        studentService.getStudentEntityById(studentId);

        return studentSkillRepository.findByStudentId(studentId).stream()
                .map(ss -> new StudentSkillResponseDTO(
                        ss.getId(),
                        ss.getStudent().getId(),
                        ss.getSkill().getId(),
                        ss.getSkill().getName(),
                        ss.getProficiency()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentSkill> getStudentSkillEntities(Long studentId) {
        studentService.getStudentEntityById(studentId);
        return studentSkillRepository.findByStudentId(studentId);
    }
}
