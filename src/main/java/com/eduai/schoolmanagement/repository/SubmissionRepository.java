package com.eduai.schoolmanagement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Submission;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {

    // Find by assignment
    List<Submission> findByAssignment(Assignment assignment);

    // Find by student
    List<Submission> findByStudent(Student student);

    // Find by assignment and student
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, Student student);

    // Find latest submission by assignment and student
    @Query("{'assignment': ?0, 'student': ?1, 'latestAttempt': true}")
    Optional<Submission> findLatestSubmissionByAssignmentAndStudent(Assignment assignment, Student student);

    // Find by status
    List<Submission> findByStatus(String status);

    // Find submissions by student ID
    @Query("{'student.studentId': ?0}")
    List<Submission> findByStudentStudentId(String studentId);

    // Find submissions by assignment ID
    @Query("{'assignment.id': ?0}")
    List<Submission> findByAssignmentId(String assignmentId);

    // Find submitted between dates
    @Query("{'submittedAt': {$gte: ?0, $lte: ?1}}")
    List<Submission> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find late submissions
    @Query("{'status': 'LATE'}")
    List<Submission> findLateSubmissions();

    // Find submissions pending grading
    @Query("{'status': 'SUBMITTED', 'score': null, 'aiGrading.aiGraded': {$ne: true}}")
    List<Submission> findPendingGrading();

    // Find AI graded submissions
    @Query("{'aiGrading.aiGraded': true}")
    List<Submission> findAIGradedSubmissions();

    // Find submissions needing human review
    @Query("{'aiGrading.aiGraded': true, 'aiGrading.humanReviewed': false}")
    List<Submission> findSubmissionsNeedingHumanReview();

    // Find high confidence AI grades
    @Query("{'aiGrading.aiGraded': true, 'aiGrading.confidence': {$gte: ?0}}")
    List<Submission> findHighConfidenceAIGrades(Double confidenceThreshold);

    // Find low confidence AI grades
    @Query("{'aiGrading.aiGraded': true, 'aiGrading.confidence': {$lt: ?0}}")
    List<Submission> findLowConfidenceAIGrades(Double confidenceThreshold);

    // Find plagiarism flagged submissions
    @Query("{'plagiarismCheck.status': 'FLAGGED'}")
    List<Submission> findPlagiarismFlaggedSubmissions();

    // Find submissions by assignment and status
    @Query("{'assignment': ?0, 'status': ?1}")
    List<Submission> findByAssignmentAndStatus(Assignment assignment, String status);

    // Find graded submissions by assignment
    @Query("{'assignment': ?0, 'status': 'GRADED'}")
    List<Submission> findGradedSubmissionsByAssignment(Assignment assignment);

    // Find student submissions for course
    @Query("{'student': ?0, 'assignment.course.courseCode': ?1}")
    List<Submission> findByStudentAndCourseCode(Student student, String courseCode);

    // Count submissions by assignment
    long countByAssignment(Assignment assignment);

    // Count submissions by student
    long countByStudent(Student student);

    // Count submissions by status
    long countByStatus(String status);

    // Count AI graded submissions
    @Query(value = "{'aiGrading.aiGraded': true}", count = true)
    long countAIGradedSubmissions();

    // Count late submissions
    @Query(value = "{'status': 'LATE'}", count = true)
    long countLateSubmissions();

    // Find submissions by grade range
    @Query("{'score': {$gte: ?0, $lte: ?1}}")
    List<Submission> findByScoreBetween(Double minScore, Double maxScore);

    // Find recent submissions
    @Query("{'submittedAt': {$gte: ?0}}")
    List<Submission> findRecentSubmissions(LocalDateTime since);

    // Find overdue assignments (not submitted and past due date)
    @Query("{'assignment.dueDate': {$lt: ?0}, 'status': {$in: ['DRAFT', null]}}")
    List<Submission> findOverdueAssignments(LocalDateTime currentTime);

    // Find submissions by attempt number
    @Query("{'assignment': ?0, 'student': ?1, 'attemptNumber': ?2}")
    Optional<Submission> findByAssignmentAndStudentAndAttemptNumber(Assignment assignment, Student student, int attemptNumber);

    // Find all attempts by student and assignment
    @Query("{'assignment': ?0, 'student': ?1}")
    List<Submission> findAllAttemptsByAssignmentAndStudent(Assignment assignment, Student student);

    // Advanced analytics queries
    @Query(value = "{'assignment': ?0, 'status': 'GRADED'}", fields = "{'score': 1}")
    List<Submission> findScoresByAssignment(Assignment assignment);

    // Find submissions with plagiarism check
    @Query("{'plagiarismCheck.checked': true}")
    List<Submission> findSubmissionsWithPlagiarismCheck();

    // Find submissions by device type
    @Query("{'analytics.submissionDevice': ?0}")
    List<Submission> findBySubmissionDevice(String deviceType);

    // Find submissions requiring attention (late, flagged, low AI confidence)
    @Query("{'$or': [" +
           "{'status': 'LATE'}, " +
           "{'plagiarismCheck.status': 'FLAGGED'}, " +
           "{'aiGrading.aiGraded': true, 'aiGrading.confidence': {$lt: 0.7}}" +
           "]}")
    List<Submission> findSubmissionsRequiringAttention();

    // Find submissions by time spent range
    @Query("{'analytics.timeSpent': {$gte: ?0, $lte: ?1}}")
    List<Submission> findByTimeSpentBetween(Long minMinutes, Long maxMinutes);
}
