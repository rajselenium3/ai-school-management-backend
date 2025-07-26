package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Parent;
import com.eduai.schoolmanagement.entity.AccessCode;
import com.eduai.schoolmanagement.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private IDGenerationService idGenerationService;
    @Mock
    private AccessCodeService accessCodeService;
    @Mock
    private ParentService parentService;
    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("should return all students")
    void getAllStudents_returnsAll() {
        List<Student> students = List.of(mock(Student.class));
        when(studentRepository.findAll()).thenReturn(students);
        assertThat(studentService.getAllStudents()).isEqualTo(students);
    }

    @Test
    @DisplayName("should return student by id")
    void getStudentById_found() {
        Student student = mock(Student.class);
        when(studentRepository.findById("id")).thenReturn(Optional.of(student));
        assertThat(studentService.getStudentById("id")).contains(student);
    }

    @Test
    @DisplayName("should return empty if student by id not found")
    void getStudentById_notFound() {
        when(studentRepository.findById("id")).thenReturn(Optional.empty());
        assertThat(studentService.getStudentById("id")).isEmpty();
    }

    @Test
    @DisplayName("should return student by studentId")
    void getStudentByStudentId_found() {
        Student student = mock(Student.class);
        when(studentRepository.findByStudentId("sid")).thenReturn(Optional.of(student));
        assertThat(studentService.getStudentByStudentId("sid")).contains(student);
    }

    @Test
    @DisplayName("should return student by email")
    void getStudentByEmail_found() {
        Student student = mock(Student.class);
        when(studentRepository.findByUserEmail("mail@x.com")).thenReturn(Optional.of(student));
        assertThat(studentService.getStudentByEmail("mail@x.com")).contains(student);
    }

    @Test
    @DisplayName("should return students by grade")
    void getStudentsByGrade() {
        List<Student> students = List.of(mock(Student.class));
        when(studentRepository.findByGrade("10")).thenReturn(students);
        assertThat(studentService.getStudentsByGrade("10")).isEqualTo(students);
    }

    @Test
    @DisplayName("should return students by grade and section")
    void getStudentsByGradeAndSection() {
        List<Student> students = List.of(mock(Student.class));
        when(studentRepository.findByGradeAndSection("10", "A")).thenReturn(students);
        assertThat(studentService.getStudentsByGradeAndSection("10", "A")).isEqualTo(students);
    }

    @Test
    @DisplayName("should search students by name")
    void searchStudentsByName() {
        Page<Student> page = mock(Page.class);
        Pageable pageable = mock(Pageable.class);
        when(studentRepository.findByFirstNameContainingIgnoreCase("John", pageable)).thenReturn(page);
        assertThat(studentService.searchStudentsByName("John", pageable)).isEqualTo(page);
    }
}
