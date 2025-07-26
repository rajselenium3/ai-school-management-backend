package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Teacher;
import com.eduai.schoolmanagement.repository.TeacherRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {
    @Mock
    private TeacherRepository teacherRepository;
    @InjectMocks
    private TeacherService teacherService;

    @Test
    @DisplayName("should return all teachers")
    void getAllTeachers_returnsAll() {
        List<Teacher> teachers = List.of(mock(Teacher.class));
        when(teacherRepository.findAll()).thenReturn(teachers);
        assertThat(teacherService.getAllTeachers()).isEqualTo(teachers);
    }

    @Test
    @DisplayName("should return teacher by id")
    void getTeacherById_found() {
        Teacher teacher = mock(Teacher.class);
        when(teacherRepository.findById("id")).thenReturn(Optional.of(teacher));
        assertThat(teacherService.getTeacherById("id")).contains(teacher);
    }

    @Test
    @DisplayName("should return empty if teacher by id not found")
    void getTeacherById_notFound() {
        when(teacherRepository.findById("id")).thenReturn(Optional.empty());
        assertThat(teacherService.getTeacherById("id")).isEmpty();
    }

    @Test
    @DisplayName("should return teacher by employee id")
    void getTeacherByEmployeeId_found() {
        Teacher teacher = mock(Teacher.class);
        when(teacherRepository.findByEmployeeId("emp1")).thenReturn(Optional.of(teacher));
        assertThat(teacherService.getTeacherByEmployeeId("emp1")).contains(teacher);
    }

    @Test
    @DisplayName("should return teacher by email")
    void getTeacherByEmail_found() {
        Teacher teacher = mock(Teacher.class);
        when(teacherRepository.findByUserEmail("mail@x.com")).thenReturn(Optional.of(teacher));
        assertThat(teacherService.getTeacherByEmail("mail@x.com")).contains(teacher);
    }

    @Test
    @DisplayName("should return teachers by department")
    void getTeachersByDepartment() {
        List<Teacher> teachers = List.of(mock(Teacher.class));
        when(teacherRepository.findByDepartment("Math")).thenReturn(teachers);
        assertThat(teacherService.getTeachersByDepartment("Math")).isEqualTo(teachers);
    }

    @Test
    @DisplayName("should return teachers by employment type")
    void getTeachersByEmploymentType() {
        List<Teacher> teachers = List.of(mock(Teacher.class));
        when(teacherRepository.findByEmploymentType("FULL_TIME")).thenReturn(teachers);
        assertThat(teacherService.getTeachersByEmploymentType("FULL_TIME")).isEqualTo(teachers);
    }

	/*
	 * @Test
	 * 
	 * @DisplayName("should return teachers by subject") void getTeachersBySubject()
	 * { List<Teacher> teachers = List.of(mock(Teacher.class));
	 * when(teacherRepository.findBySubjectsContaining("Math")).thenReturn(teachers)
	 * ;
	 * assertThat(teacherService.getTeachersBySubject("Math")).isEqualTo(teachers);
	 * }
	 * 
	 * @Test
	 * 
	 * @DisplayName("should search teachers by name") void searchTeachersByName() {
	 * Page<Teacher> page = mock(Page.class); Pageable pageable =
	 * mock(Pageable.class);
	 * when(teacherRepository.findByFirstNameContainingIgnoreCase("John",
	 * pageable)).thenReturn(page);
	 * assertThat(teacherService.searchTeachersByName("John",
	 * pageable)).isEqualTo(page); }
	 * 
	 * @Test
	 * 
	 * @DisplayName("should return high performing teachers") void
	 * getHighPerformingTeachers() { List<Teacher> teachers =
	 * List.of(mock(Teacher.class));
	 * when(teacherRepository.findByPerformanceScoreGreaterThanEqual(90.0)).
	 * thenReturn(teachers);
	 * assertThat(teacherService.getHighPerformingTeachers(90.0)).isEqualTo(teachers
	 * ); }
	 */

    @Test
    @DisplayName("should save teacher")
    void saveTeacher() {
        Teacher teacher = mock(Teacher.class);
        when(teacher.getEmployeeId()).thenReturn("emp1");
        when(teacherRepository.save(teacher)).thenReturn(teacher);
        assertThat(teacherService.saveTeacher(teacher)).isEqualTo(teacher);
    }
}
