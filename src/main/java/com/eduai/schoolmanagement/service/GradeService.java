package com.eduai.schoolmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Course;
import com.eduai.schoolmanagement.entity.Grade;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.GradeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {

    private final GradeRepository gradeRepository;

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Optional<Grade> getGradeById(String id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> getGradesByStudent(Student student) {
        return gradeRepository.findByStudent(student);
    }

    public List<Grade> getGradesByCourse(Course course) {
        return gradeRepository.findByCourse(course);
    }

    public List<Grade> getGradesByAssignment(Assignment assignment) {
        return gradeRepository.findByAssignment(assignment);
    }

    public Optional<Grade> getGradeByStudentAndAssignment(Student student, Assignment assignment) {
        return gradeRepository.findByStudentAndAssignment(student, assignment);
    }

    public List<Grade> getGradesByStudentAndCourse(Student student, Course course) {
        return gradeRepository.findByStudentAndCourse(student, course);
    }

    public List<Grade> getPendingGrades() {
        return gradeRepository.findByStatus("PENDING");
    }

    public List<Grade> getAIGeneratedGrades() {
        return gradeRepository.findByAiGenerated();
    }

    public List<Grade> getHighConfidenceAIGrades(double confidenceThreshold) {
        return gradeRepository.findByAiConfidenceGreaterThanEqual(confidenceThreshold);
    }

    public Grade saveGrade(Grade grade) {
        if (grade.getGradedDate() == null) {
            grade.setGradedDate(LocalDateTime.now());
        }
        calculateLetterGrade(grade);
        log.info("Saving grade for student: {} assignment: {}",
                grade.getStudent().getStudentId(), grade.getAssignment().getTitle());
        return gradeRepository.save(grade);
    }

    public Grade updateGrade(String id, Grade grade) {
        grade.setId(id);
        calculateLetterGrade(grade);
        return gradeRepository.save(grade);
    }

    public void deleteGrade(String id) {
        log.info("Deleting grade with id: {}", id);
        gradeRepository.deleteById(id);
    }

    public Grade generateAIGrade(Grade grade, double aiScore, double confidence, String feedback) {
        Grade.AIGrading aiGrading = new Grade.AIGrading();
        aiGrading.setAiGenerated(true);
        aiGrading.setAiSuggestedScore(aiScore);
        aiGrading.setConfidence(confidence);
        aiGrading.setAiGeneratedFeedback(feedback);
        aiGrading.setHumanReviewed(false);

        grade.setAiGrading(aiGrading);
        grade.setScore(aiScore);
        grade.setPercentage((aiScore / grade.getMaxScore()) * 100);
        grade.setFeedback(feedback);
        grade.setStatus("GRADED");

        calculateLetterGrade(grade);

        log.info("Generated AI grade: {} with confidence: {}", aiScore, confidence);
        return gradeRepository.save(grade);
    }

    public Grade reviewAIGrade(String gradeId, boolean approved, String reviewComments) {
        Optional<Grade> gradeOpt = gradeRepository.findById(gradeId);
        if (gradeOpt.isPresent()) {
            Grade grade = gradeOpt.get();
            Grade.AIGrading aiGrading = grade.getAiGrading();
            if (aiGrading != null) {
                aiGrading.setHumanReviewed(true);
                aiGrading.setReviewComments(reviewComments);

                if (!approved) {
                    grade.setStatus("PENDING");
                }

                grade.setAiGrading(aiGrading);
                return gradeRepository.save(grade);
            }
        }
        throw new RuntimeException("Grade not found or not AI generated");
    }

    private void calculateLetterGrade(Grade grade) {
        double percentage = grade.getPercentage();
        String letterGrade;

        if (percentage >= 97) letterGrade = "A+";
        else if (percentage >= 93) letterGrade = "A";
        else if (percentage >= 90) letterGrade = "A-";
        else if (percentage >= 87) letterGrade = "B+";
        else if (percentage >= 83) letterGrade = "B";
        else if (percentage >= 80) letterGrade = "B-";
        else if (percentage >= 77) letterGrade = "C+";
        else if (percentage >= 73) letterGrade = "C";
        else if (percentage >= 70) letterGrade = "C-";
        else if (percentage >= 67) letterGrade = "D+";
        else if (percentage >= 63) letterGrade = "D";
        else if (percentage >= 60) letterGrade = "D-";
        else letterGrade = "F";

        grade.setLetterGrade(letterGrade);
    }
}
