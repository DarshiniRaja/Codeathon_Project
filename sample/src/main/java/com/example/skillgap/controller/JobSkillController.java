package com.example.skillgap.controller;

import com.example.skillgap.dto.JobSkillRequestDTO;
import com.example.skillgap.dto.JobSkillResponseDTO;
import com.example.skillgap.service.JobSkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/{id}/skills")
@CrossOrigin(origins = "*")
public class JobSkillController {

    private final JobSkillService jobSkillService;

    public JobSkillController(JobSkillService jobSkillService) {
        this.jobSkillService = jobSkillService;
    }

    @PostMapping
    public ResponseEntity<JobSkillResponseDTO> addSkill(
            @PathVariable("id") Long jobId,
            @RequestBody JobSkillRequestDTO request) {
        JobSkillResponseDTO response = jobSkillService.addJobSkill(jobId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobSkillResponseDTO>> getSkills(@PathVariable("id") Long jobId) {
        List<JobSkillResponseDTO> list = jobSkillService.getSkillsByJobId(jobId);
        return ResponseEntity.ok(list);
    }
}
