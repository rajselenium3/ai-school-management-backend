package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Submission;
import com.eduai.schoolmanagement.repository.SubmissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {
    @Mock
    private SubmissionRepository submissionRepository;
    @InjectMocks
    private SubmissionService submissionService;

    @Test
    @DisplayName("should return all submissions")
    void getAllSubmissions_returnsAll() {
        List<Submission> submissions = List.of(mock(Submission.class));
        when(submissionRepository.findAll()).thenReturn(submissions);
        assertThat(submissionService.getAllSubmissions()).isEqualTo(submissions);
    }

    @Test
    @DisplayName("should return submission by id")
    void getSubmissionById_found() {
        Submission submission = mock(Submission.class);
        when(submissionRepository.findById("id")).thenReturn(Optional.of(submission));
        assertThat(submissionService.getSubmissionById("id")).contains(submission);
    }

    @Test
    @DisplayName("should return empty if submission by id not found")
    void getSubmissionById_notFound() {
        when(submissionRepository.findById("id")).thenReturn(Optional.empty());
        assertThat(submissionService.getSubmissionById("id")).isEmpty();
    }

    @Test
    @DisplayName("should return submissions by assignment")
    void getSubmissionsByAssignment() {
        Assignment assignment = mock(Assignment.class);
        List<Submission> submissions = List.of(mock(Submission.class));
        when(submissionRepository.findByAssignment(assignment)).thenReturn(submissions);
        assertThat(submissionService.getSubmissionsByAssignment(assignment)).isEqualTo(submissions);
    }

    @Test
    @DisplayName("should return submissions by student")
    void getSubmissionsByStudent() {
        Student student = mock(Student.class);
        List<Submission> submissions = List.of(mock(Submission.class));
        when(submissionRepository.findByStudent(student)).thenReturn(submissions);
        assertThat(submissionService.getSubmissionsByStudent(student)).isEqualTo(submissions);
    }

    @Test
    @DisplayName("should return submission by assignment and student")
    void getSubmissionByAssignmentAndStudent() {
        Assignment assignment = mock(Assignment.class);
        Student student = mock(Student.class);
        Submission submission = mock(Submission.class);
        when(submissionRepository.findLatestSubmissionByAssignmentAndStudent(assignment, student)).thenReturn(Optional.of(submission));
        assertThat(submissionService.getSubmissionByAssignmentAndStudent(assignment, student)).contains(submission);
    }

    @Test
    @DisplayName("should return submissions by status")
    void getSubmissionsByStatus() {
        List<Submission> submissions = List.of(mock(Submission.class));
        when(submissionRepository.findByStatus("PENDING")).thenReturn(submissions);
        assertThat(submissionService.getSubmissionsByStatus("PENDING")).isEqualTo(submissions);
    }

    @Test
    @DisplayName("should return pending grading submissions")
    void getPendingGradingSubmissions() {
        List<Submission> submissions = List.of(mock(Submission.class));
        when(submissionRepository.findPendingGrading()).thenReturn(submissions);
        assertThat(submissionService.getPendingGradingSubmissions()).isEqualTo(submissions);
    }

    @Test
    @DisplayName("should return AI graded submissions")
    void getAIGradedSubmissions() {
        List<Submission> submissions = List.of(mock(Submission.class));
        when(submissionRepository.findAIGradedSubmissions()).thenReturn(submissions);
        assertThat(submissionService.getAIGradedSubmissions()).isEqualTo(submissions);
    }

    @Test
    @DisplayName("should return submissions needing human review")
    void getSubmissionsNeedingHumanReview() {
        List<Submission> submissions = List.of(mock(Submission.class));
        when(submissionRepository.findSubmissionsNeedingHumanReview()).thenReturn(submissions);
        assertThat(submissionService.getSubmissionsNeedingHumanReview()).isEqualTo(submissions);
    }
}
