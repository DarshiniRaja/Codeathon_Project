package com.example.skillgap.repository;

import com.example.skillgap.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByStudentIdAndJobId(Long studentId, Long jobId);
    void deleteByStudentIdAndJobId(Long studentId, Long jobId);
}
