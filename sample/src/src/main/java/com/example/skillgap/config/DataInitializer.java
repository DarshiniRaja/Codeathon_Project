package com.example.skillgap.config;

import com.example.skillgap.entity.Job;
import com.example.skillgap.entity.JobSkill;
import com.example.skillgap.entity.Skill;
import com.example.skillgap.entity.Student;
import com.example.skillgap.entity.StudentSkill;
import com.example.skillgap.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;

    public DataInitializer(StudentRepository studentRepository,
                           SkillRepository skillRepository,
                           StudentSkillRepository studentSkillRepository,
                           JobRepository jobRepository,
                           JobSkillRepository jobSkillRepository) {
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.jobRepository = jobRepository;
        this.jobSkillRepository = jobSkillRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Create Skills
        Skill java = skillRepository.save(new Skill("Java"));
        Skill sql = skillRepository.save(new Skill("SQL"));
        Skill springBoot = skillRepository.save(new Skill("Spring Boot"));
        Skill html = skillRepository.save(new Skill("HTML"));
        Skill react = skillRepository.save(new Skill("React"));

        // 2. Create Students
        Student ravi = studentRepository.save(new Student("Ravi Kumar", "ravi@example.com"));
        Student priya = studentRepository.save(new Student("Priya Sharma", "priya@example.com"));

        // 3. Assign Student Skills
        studentSkillRepository.saveAll(List.of(
                new StudentSkill(ravi, java, 4),
                new StudentSkill(ravi, sql, 5),
                new StudentSkill(ravi, html, 3),
                new StudentSkill(priya, java, 5),
                new StudentSkill(priya, springBoot, 4),
                new StudentSkill(priya, react, 3)
        ));

        // 4. Create Jobs
        Job backendDev = jobRepository.save(new Job(
                "Java Backend Developer",
                "Develop high-performance Spring Boot REST APIs and database solutions"
        ));
        Job fullStackDev = jobRepository.save(new Job(
                "Full Stack Developer",
                "Build modern web applications with React and Spring Boot"
        ));

        // 5. Assign Job Required Skills
        jobSkillRepository.saveAll(List.of(
                new JobSkill(backendDev, java, 5, true),
                new JobSkill(backendDev, sql, 4, true),
                new JobSkill(backendDev, springBoot, 4, false),
                new JobSkill(fullStackDev, java, 4, true),
                new JobSkill(fullStackDev, react, 4, true),
                new JobSkill(fullStackDev, html, 4, false)
        ));
    }
}
