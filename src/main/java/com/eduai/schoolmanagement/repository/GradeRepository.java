package com.eduai.schoolmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Course;
import com.eduai.schoolmanagement.entity.Grade;
import com.eduai.schoolmanagement.entity.Student;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {

    List<Grade> findByStudent(Student student);

    List<Grade> findByCourse(Course course);

    List<Grade> findByAssignment(Assignment assignment);

    Optional<Grade> findByStudentAndAssignment(Student student, Assignment assignment);

    List<Grade> findByStudentAndCourse(Student student, Course course);

    List<Grade> findByStatus(String status);

    @Query("{'aiGrading.aiGenerated': true}")
    List<Grade> findByAiGenerated();

    @Query("{'aiGrading.confidence': {$gte: ?0}}")
    List<Grade> findByAiConfidenceGreaterThanEqual(double confidence);

    @Query("{'percentage': {$gte: ?0, $lte: ?1}}")
    List<Grade> findByPercentageBetween(double minPercentage, double maxPercentage);

    @Query("{'student': ?0}")
    List<Grade> findGradesByStudentId(String studentId);

    double countByStatus(String status);
}
