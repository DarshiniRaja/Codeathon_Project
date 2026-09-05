package com.example.skillgap.controller;

import com.example.skillgap.dto.RecommendationResponseDTO;
import com.example.skillgap.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}/jobs/{jobId}/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<RecommendationResponseDTO>> getRecommendations(
            @PathVariable("studentId") Long studentId,
            @PathVariable("jobId") Long jobId) {
        List<RecommendationResponseDTO> list = recommendationService.generateRecommendations(studentId, jobId);
        return ResponseEntity.ok(list);
    }
}
