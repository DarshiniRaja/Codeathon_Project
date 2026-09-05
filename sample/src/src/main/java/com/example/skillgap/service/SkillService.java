package com.example.skillgap.service;

import com.example.skillgap.dto.SkillRequestDTO;
import com.example.skillgap.dto.SkillResponseDTO;
import com.example.skillgap.entity.Skill;
import com.example.skillgap.exception.BadRequestException;
import com.example.skillgap.exception.DuplicateResourceException;
import com.example.skillgap.exception.ResourceNotFoundException;
import com.example.skillgap.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional
    public SkillResponseDTO createSkill(SkillRequestDTO request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Skill name cannot be empty");
        }
        String skillName = request.getName().trim();
        if (skillRepository.existsByNameIgnoreCase(skillName)) {
            throw new DuplicateResourceException("Skill with name '" + skillName + "' already exists");
        }

        Skill skill = new Skill(skillName);
        Skill saved = skillRepository.save(skill);
        return new SkillResponseDTO(saved.getId(), saved.getName());
    }

    @Transactional(readOnly = true)
    public List<SkillResponseDTO> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(s -> new SkillResponseDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SkillResponseDTO getSkillById(Long id) {
        Skill skill = getSkillEntityById(id);
        return new SkillResponseDTO(skill.getId(), skill.getName());
    }

    @Transactional(readOnly = true)
    public Skill getSkillEntityById(Long id) {
        if (id == null) {
            throw new BadRequestException("Skill ID cannot be null");
        }
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill with id " + id + " not found"));
    }

    @Transactional
    public Skill getOrCreateSkill(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Skill name cannot be empty");
        }
        String cleanName = name.trim();
        return skillRepository.findByNameIgnoreCase(cleanName)
                .orElseGet(() -> skillRepository.save(new Skill(cleanName)));
    }
}
