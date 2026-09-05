package com.example.skillgap.controller;

import com.example.skillgap.dto.ApplicationRequestDTO;
import com.example.skillgap.dto.ApplicationResponseDTO;
import com.example.skillgap.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponseDTO> createApplication(@RequestBody ApplicationRequestDTO request) {
        ApplicationResponseDTO response = applicationService.createApplication(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponseDTO>> getAllApplications() {
        List<ApplicationResponseDTO> list = applicationService.getAllApplications();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> getApplicationById(@PathVariable("id") Long id) {
        ApplicationResponseDTO response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }
}
