package com.example.skillgap.controller;

import com.example.skillgap.dto.*;
import com.example.skillgap.exception.GlobalExceptionHandler;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.service.ApplicationService;
import com.example.skillgap.service.RecommendationService;
import com.example.skillgap.service.SkillGapService;
import com.example.skillgap.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SkillGapIntegrationTest {

    private MockMvc studentMvc;
    private MockMvc skillGapMvc;
    private MockMvc recommendationMvc;
    private MockMvc applicationMvc;

    @Mock
    private StudentService studentService;

    @Mock
    private SkillGapService skillGapService;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private StudentController studentController;

    @InjectMocks
    private SkillGapController skillGapController;

    @InjectMocks
    private RecommendationController recommendationController;

    @InjectMocks
    private ApplicationController applicationController;

    @BeforeEach
    void setUp() {
        studentMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        skillGapMvc = MockMvcBuilders.standaloneSetup(skillGapController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        recommendationMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        applicationMvc = MockMvcBuilders.standaloneSetup(applicationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/students returns 201 Created and response JSON")
    void testCreateStudentEndpoint() throws Exception {
        StudentResponseDTO res = new StudentResponseDTO(1L, "Ravi Kumar", "ravi@example.com");

        when(studentService.createStudent(any(StudentRequestDTO.class))).thenReturn(res);

        String json = "{\"name\":\"Ravi Kumar\",\"email\":\"ravi@example.com\"}";

        studentMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ravi Kumar"))
                .andExpect(jsonPath("$.email").value("ravi@example.com"));
    }

    @Test
    @DisplayName("GET /api/students/{id} returns 404 when student not found")
    void testGetStudentNotFound() throws Exception {
        when(studentService.getStudentById(99L))
                .thenThrow(new ResourceNotFoundException("Student with id 99 not found"));

        studentMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Student with id 99 not found"))
                .andExpect(jsonPath("$.path").value("/api/students/99"));
    }

    @Test
    @DisplayName("GET /api/students/{studentId}/jobs/{jobId}/skill-gap returns 200 and calculation results")
    void testSkillGapEndpoint() throws Exception {
        List<SkillGapItemDTO> items = List.of(
                new SkillGapItemDTO("Java", 4, 5, 1, "GAP", true),
                new SkillGapItemDTO("SQL", 5, 4, 0, "MATCHED", true)
        );
        SkillGapResponseDTO gapResponse = new SkillGapResponseDTO(1L, 10L, 76.5, items);

        when(skillGapService.calculateSkillGap(1L, 10L)).thenReturn(gapResponse);

        skillGapMvc.perform(get("/api/students/1/jobs/10/skill-gap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.jobId").value(10))
                .andExpect(jsonPath("$.matchPercent").value(76.5))
                .andExpect(jsonPath("$.skills[0].skill").value("Java"))
                .andExpect(jsonPath("$.skills[0].gap").value(1))
                .andExpect(jsonPath("$.skills[0].status").value("GAP"))
                .andExpect(jsonPath("$.skills[1].skill").value("SQL"))
                .andExpect(jsonPath("$.skills[1].gap").value(0))
                .andExpect(jsonPath("$.skills[1].status").value("MATCHED"));
    }

    @Test
    @DisplayName("GET /api/students/{studentId}/jobs/{jobId}/recommendations returns 200 and recommendations")
    void testRecommendationsEndpoint() throws Exception {
        List<RecommendationResponseDTO> recs = List.of(
                new RecommendationResponseDTO(
                        "Java",
                        3,
                        5,
                        "HIGH",
                        "Current Java level is 3 but the required level is 5. Improve Java proficiency by 2 levels."
                )
        );

        when(recommendationService.generateRecommendations(1L, 10L)).thenReturn(recs);

        recommendationMvc.perform(get("/api/students/1/jobs/10/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skill").value("Java"))
                .andExpect(jsonPath("$[0].currentLevel").value(3))
                .andExpect(jsonPath("$[0].requiredLevel").value(5))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].reason").exists());
    }

    @Test
    @DisplayName("POST /api/applications returns 201 and ApplicationResponseDTO")
    void testCreateApplicationEndpoint() throws Exception {
        ApplicationResponseDTO res = new ApplicationResponseDTO(25L, 1L, 10L, 76.5, "APPLIED");

        when(applicationService.createApplication(any(ApplicationRequestDTO.class))).thenReturn(res);

        String json = "{\"studentId\":1,\"jobId\":10}";

        applicationMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(25))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.jobId").value(10))
                .andExpect(jsonPath("$.matchPercent").value(76.5))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }
}
