package com.eduai.schoolmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Course;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByDepartment(String department);

    List<Course> findByGrade(String grade);

    @Query("{'teacher.teacherId': ?0}")
    List<Course> findByTeacherTeacherId(String teacherId);

    List<Course> findByStatus(String status);

    @Query("{'courseName': {$regex: ?0, $options: 'i'}}")
    Page<Course> findByCourseNameContainingIgnoreCase(String courseName, Pageable pageable);

    List<Course> findBySemester(String semester);

    List<Course> findByAiGradingEnabled(boolean aiGradingEnabled);

    long countByDepartment(String department);

    long countByStatus(String status);
}
