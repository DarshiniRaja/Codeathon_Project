package com.example.skillgap.controller;

import com.example.skillgap.dto.SkillGapResponseDTO;
import com.example.skillgap.service.SkillGapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students/{studentId}/jobs/{jobId}/skill-gap")
@CrossOrigin(origins = "*")
public class SkillGapController {

    private final SkillGapService skillGapService;

    public SkillGapController(SkillGapService skillGapService) {
        this.skillGapService = skillGapService;
    }

    @GetMapping
    public ResponseEntity<SkillGapResponseDTO> getSkillGap(
            @PathVariable("studentId") Long studentId,
            @PathVariable("jobId") Long jobId) {
        SkillGapResponseDTO response = skillGapService.calculateSkillGap(studentId, jobId);
        return ResponseEntity.ok(response);
    }
}
