package com.example.skillgap.service;

import com.example.skillgap.dto.RecommendationResponseDTO;
import com.example.skillgap.dto.SkillGapItemDTO;
import com.example.skillgap.dto.SkillGapResponseDTO;
import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.Student;
import com.example.skillgap.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private SkillGapService skillGapService;

    @Mock
    private StudentService studentService;

    @Mock
    private JobService jobService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private Student testStudent;
    private Job testJob;

    @BeforeEach
    void setUp() {
        testStudent = new Student(1L, "Ravi Kumar", "ravi@example.com");
        testJob = new Job(10L, "Java Backend Developer", "Description");
    }

    @Test
    @DisplayName("Case 12 & 13: Mandatory gap -> HIGH, Optional gap -> MEDIUM, Matched -> omitted")
    void testRecommendationPrioritiesAndReasons() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        List<SkillGapItemDTO> gapItems = List.of(
                // Mandatory gap
                new SkillGapItemDTO("Java", 3, 5, 2, "GAP", true),
                // Optional gap
                new SkillGapItemDTO("HTML", 2, 4, 2, "GAP", false),
                // Matched skill - should produce NO recommendation
                new SkillGapItemDTO("SQL", 5, 4, 0, "MATCHED", true)
        );

        when(skillGapService.calculateSkillGap(1L, 10L))
                .thenReturn(new SkillGapResponseDTO(1L, 10L, 70.0, gapItems));

        List<RecommendationResponseDTO> recommendations = recommendationService.generateRecommendations(1L, 10L);

        assertEquals(2, recommendations.size());

        RecommendationResponseDTO javaRec = recommendations.stream()
                .filter(r -> "Java".equals(r.getSkill())).findFirst().orElseThrow();
        assertEquals("HIGH", javaRec.getPriority());
        assertEquals(3, javaRec.getCurrentLevel());
        assertEquals(5, javaRec.getRequiredLevel());
        assertEquals("Current Java level is 3 but the required level is 5. Improve Java proficiency by 2 levels.",
                javaRec.getReason());

        RecommendationResponseDTO htmlRec = recommendations.stream()
                .filter(r -> "HTML".equals(r.getSkill())).findFirst().orElseThrow();
        assertEquals("MEDIUM", htmlRec.getPriority());
        assertEquals(2, htmlRec.getCurrentLevel());
        assertEquals(4, htmlRec.getRequiredLevel());
        assertEquals("Current HTML level is 2 but the required level is 4. Improve HTML proficiency by 2 levels.",
                htmlRec.getReason());

        verify(recommendationRepository).deleteByStudentIdAndJobId(1L, 10L);
        verify(recommendationRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Singular level reason format check (1 level)")
    void testSingularLevelReason() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        List<SkillGapItemDTO> gapItems = List.of(
                new SkillGapItemDTO("Java", 4, 5, 1, "GAP", true)
        );

        when(skillGapService.calculateSkillGap(1L, 10L))
                .thenReturn(new SkillGapResponseDTO(1L, 10L, 80.0, gapItems));

        List<RecommendationResponseDTO> recommendations = recommendationService.generateRecommendations(1L, 10L);

        assertEquals(1, recommendations.size());
        assertEquals("Current Java level is 4 but the required level is 5. Improve Java proficiency by 1 level.",
                recommendations.get(0).getReason());
    }

    @Test
    @DisplayName("No recommendations when all skills are matched")
    void testNoRecommendationsWhenAllMatched() {
        when(studentService.getStudentEntityById(1L)).thenReturn(testStudent);
        when(jobService.getJobEntityById(10L)).thenReturn(testJob);

        List<SkillGapItemDTO> gapItems = List.of(
                new SkillGapItemDTO("Java", 5, 5, 0, "MATCHED", true)
        );

        when(skillGapService.calculateSkillGap(1L, 10L))
                .thenReturn(new SkillGapResponseDTO(1L, 10L, 100.0, gapItems));

        List<RecommendationResponseDTO> recommendations = recommendationService.generateRecommendations(1L, 10L);

        assertTrue(recommendations.isEmpty());
    }
}
