package com.example.skillgap.service;

import com.example.skillgap.dto.SkillRequestDTO;
import com.example.skillgap.dto.SkillResponseDTO;
import com.example.skillgap.entity.Skill;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.SkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @Test
    @DisplayName("Successfully create skill")
    void testCreateSkillSuccess() {
        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenReturn(new Skill(1L, "Java"));

        SkillResponseDTO res = skillService.createSkill(new SkillRequestDTO("Java"));

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Java", res.getName());
    }

    @Test
    @DisplayName("Reject duplicate skill name")
    void testDuplicateSkillRejected() {
        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                skillService.createSkill(new SkillRequestDTO("Java")));
    }

    @Test
    @DisplayName("Reject empty skill name")
    void testEmptySkillNameRejected() {
        assertThrows(BadRequestException.class, () ->
                skillService.createSkill(new SkillRequestDTO("")));
    }

    @Test
    @DisplayName("Get missing skill throws 404")
    void testGetMissingSkillThrowsNotFound() {
        when(skillRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> skillService.getSkillById(999L));
    }
}
