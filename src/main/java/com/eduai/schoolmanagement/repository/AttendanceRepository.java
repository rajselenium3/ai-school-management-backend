package com.eduai.schoolmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Attendance;

@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, String> {

    @Query("{'student.studentId': ?0}")
    List<Attendance> findByStudentStudentId(String studentId);

    @Query("{'course.courseCode': ?0}")
    List<Attendance> findByCourseCourseCode(String courseCode);

    List<Attendance> findByDate(LocalDate date);

    @Query("{'student.studentId': ?0, 'course.courseCode': ?1, 'date': ?2}")
    List<Attendance> findByStudentStudentIdAndCourseCourseCodeAndDate(String studentId, String courseCode, LocalDate date);

    @Query("{'student.studentId': ?0, 'date': {$gte: ?1, $lte: ?2}}")
    List<Attendance> findByStudentStudentIdAndDateBetween(String studentId, LocalDate startDate, LocalDate endDate);

    @Query("{'course.courseCode': ?0, 'date': {$gte: ?1, $lte: ?2}}")
    List<Attendance> findByCourseCourseCodeAndDateBetween(String courseCode, LocalDate startDate, LocalDate endDate);

    @Query("{'student.studentId': ?0, 'course.courseCode': ?1, 'date': {$gte: ?2, $lte: ?3}}")
    List<Attendance> findByStudentStudentIdAndCourseCourseCodeAndDateBetween(String studentId, String courseCode, LocalDate startDate, LocalDate endDate);

    @Query("{'course.courseCode': ?0, 'date': ?1}")
    List<Attendance> findByCourseCourseCodeAndDate(String courseCode, LocalDate date);

    List<Attendance> findByStatus(String status);

    @Query("{'date': {$gte: ?0, $lte: ?1}}")
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("{'analytics.riskLevel': ?0}")
    List<Attendance> findByRiskLevel(String riskLevel);

    @Query("{'student.studentId': ?0, 'status': ?1}")
    long countByStudentStudentIdAndStatus(String studentId, String status);

    long countByDateAndStatus(LocalDate date, String status);

    @Query("{'course.courseCode': ?0, 'status': ?1}")
    long countByCourseCourseCodeAndStatus(String courseCode, String status);
}
