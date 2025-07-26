package com.eduai.schoolmanagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Course;

@Repository
public interface AssignmentRepository extends MongoRepository<Assignment, String> {

    // Find assignments by course
    List<Assignment> findByCourse(Course course);

    // Find assignments by teacher (course teacher)
    @Query("{'course.teacher.teacherId': ?0}")
    List<Assignment> findByTeacherId(String teacherId);

    // Find assignments by course code
    @Query("{'course.courseCode': ?0}")
    List<Assignment> findByCourseCode(String courseCode);

    // Find assignments by type
    List<Assignment> findByType(String type);

    // Find assignments by title containing (search)
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Assignment> findByTitleContainingIgnoreCase(String title);

    // Find assignments by due date range
    @Query("{'dueDate': {$gte: ?0, $lte: ?1}}")
    List<Assignment> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find assignments due today
    @Query("{'dueDate': {$gte: ?0, $lte: ?1}}")
    List<Assignment> findDueToday(LocalDateTime startOfDay, LocalDateTime endOfDay);

    // Find assignments due soon (within next X days)
    @Query("{'dueDate': {$gte: ?0, $lte: ?1}}")
    List<Assignment> findDueSoon(LocalDateTime now, LocalDateTime futureDate);

    // Find overdue assignments
    @Query("{'dueDate': {$lt: ?0}}")
    List<Assignment> findOverdueAssignments(LocalDateTime currentTime);

    // Find assignments with AI grading enabled
    @Query("{'aiGradingEnabled': true}")
    List<Assignment> findAIGradingEnabledAssignments();

    // Find assignments by course and type
    @Query("{'course': ?0, 'type': ?1}")
    List<Assignment> findByCourseAndType(Course course, String type);

    // Find assignments by teacher and type
    @Query("{'course.teacher.teacherId': ?0, 'type': ?1}")
    List<Assignment> findByTeacherIdAndType(String teacherId, String type);

    // Find assignments by course and date range
    @Query("{'course': ?0, 'dueDate': {$gte: ?1, $lte: ?2}}")
    List<Assignment> findByCourseAndDueDateBetween(Course course, LocalDateTime startDate, LocalDateTime endDate);

    // Find recent assignments (created recently)
    @Query("{'createdAt': {$gte: ?0}}")
    List<Assignment> findRecentAssignments(LocalDateTime since);

    // Find assignments with submissions
    @Query("{'submissions': {$gt: 0}}")
    List<Assignment> findAssignmentsWithSubmissions();

    // Find assignments pending grading
    @Query("{'submissions': {$gt: 0}, 'graded': {$lt: '$submissions'}}")
    List<Assignment> findAssignmentsPendingGrading();

    // Find assignments by weight range
    @Query("{'weight': {$gte: ?0, $lte: ?1}}")
    List<Assignment> findByWeightBetween(Double minWeight, Double maxWeight);

    // Find assignments by max score range
    @Query("{'maxScore': {$gte: ?0, $lte: ?1}}")
    List<Assignment> findByMaxScoreBetween(Double minScore, Double maxScore);

    // Count assignments by course
    long countByCourse(Course course);

    // Count assignments by teacher
    @Query(value = "{'course.teacher.teacherId': ?0}", count = true)
    long countByTeacherId(String teacherId);

    // Count assignments by type
    long countByType(String type);

    // Count overdue assignments
    @Query(value = "{'dueDate': {$lt: ?0}}", count = true)
    long countOverdueAssignments(LocalDateTime currentTime);

    // Count AI grading enabled assignments
    @Query(value = "{'aiGradingEnabled': true}", count = true)
    long countAIGradingEnabledAssignments();

    // Find assignments by submission start date range
    @Query("{'submissionStartDate': {$gte: ?0, $lte: ?1}}")
    List<Assignment> findBySubmissionStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find assignments where submission period is active
    @Query("{'submissionStartDate': {$lte: ?0}, 'dueDate': {$gte: ?0}}")
    List<Assignment> findActiveSubmissionPeriod(LocalDateTime currentTime);

    // Find assignments by course department
    @Query("{'course.department': ?0}")
    List<Assignment> findByCourseDepartment(String department);

    // Find assignments by grade level
    @Query("{'course.grade': ?0}")
    List<Assignment> findByCourseGrade(String grade);

    // Complex query: Find assignments requiring attention (overdue, low submission rate, etc.)
    @Query("{'$or': [" +
           "{'dueDate': {$lt: ?0}}, " +
           "{'submissions': {$lt: {'$multiply': ['$expectedSubmissions', 0.5]}}}" +
           "]}")
    List<Assignment> findAssignmentsRequiringAttention(LocalDateTime currentTime);

    // Find assignments by average score range
    @Query("{'averageScore': {$gte: ?0, $lte: ?1}}")
    List<Assignment> findByAverageScoreBetween(Double minAverage, Double maxAverage);

    // Find assignments with rubric
    @Query("{'rubric': {$exists: true, $ne: []}}")
    List<Assignment> findAssignmentsWithRubric();

    // Find assignments by course status
    @Query("{'course.status': ?0}")
    List<Assignment> findByCourseStatus(String courseStatus);

    // Search assignments by multiple criteria
    @Query("{'$and': [" +
           "{'title': {$regex: ?0, $options: 'i'}}, " +
           "{'type': {$in: ?1}}, " +
           "{'course.courseCode': {$in: ?2}}" +
           "]}")
    List<Assignment> searchAssignments(String titleSearch, List<String> types, List<String> courseCodes);

    // Find assignments with attachments
    @Query("{'attachments': {$exists: true, $ne: []}}")
    List<Assignment> findAssignmentsWithAttachments();

    // Advanced analytics queries
    @Query(value = "{'course': ?0}", fields = "{'maxScore': 1, 'averageScore': 1, 'submissions': 1}")
    List<Assignment> findCourseAssignmentScores(Course course);

    // Find assignments by teacher and date range for workload analysis
    @Query("{'course.teacher.teacherId': ?0, 'dueDate': {$gte: ?1, $lte: ?2}}")
    List<Assignment> findTeacherWorkload(String teacherId, LocalDateTime startDate, LocalDateTime endDate);

    // Find assignments similar to given assignment (same course, type, weight range)
    @Query("{'course': ?0, 'type': ?1, 'weight': {$gte: ?2, $lte: ?3}, 'id': {$ne: ?4}}")
    List<Assignment> findSimilarAssignments(Course course, String type, Double minWeight, Double maxWeight, String excludeId);
}
