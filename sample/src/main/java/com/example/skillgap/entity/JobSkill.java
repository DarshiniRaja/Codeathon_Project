package com.example.skillgap.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"job_id", "skill_id"})
})
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Integer requiredLevel;

    @Column(nullable = false)
    private Boolean mandatory = false;

    public JobSkill() {
    }

    public JobSkill(Job job, Skill skill, Integer requiredLevel, Boolean mandatory) {
        this.job = job;
        this.skill = skill;
        this.requiredLevel = requiredLevel;
        this.mandatory = mandatory != null ? mandatory : false;
    }

    public JobSkill(Long id, Job job, Skill skill, Integer requiredLevel, Boolean mandatory) {
        this.id = id;
        this.job = job;
        this.skill = skill;
        this.requiredLevel = requiredLevel;
        this.mandatory = mandatory != null ? mandatory : false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Integer getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(Integer requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
}
