package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Parent;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.User;
import com.eduai.schoolmanagement.repository.ParentRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
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
class ParentServiceTest {
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private ParentService parentService;

    @Test
    @DisplayName("should return all parents")
    void getAllParents_returnsAll() {
        List<Parent> parents = List.of(mock(Parent.class));
        when(parentRepository.findAll()).thenReturn(parents);
        assertThat(parentService.getAllParents()).isEqualTo(parents);
    }

    @Test
    @DisplayName("should return all active parents")
    void getAllActiveParents() {
        List<Parent> parents = List.of(mock(Parent.class));
        when(parentRepository.findByIsActiveTrue()).thenReturn(parents);
        assertThat(parentService.getAllActiveParents()).isEqualTo(parents);
    }

    @Test
    @DisplayName("should return parent by id")
    void getParentById_found() {
        Parent parent = mock(Parent.class);
        when(parentRepository.findById("id")).thenReturn(Optional.of(parent));
        assertThat(parentService.getParentById("id")).contains(parent);
    }

    @Test
    @DisplayName("should return empty if parent by id not found")
    void getParentById_notFound() {
        when(parentRepository.findById("id")).thenReturn(Optional.empty());
        assertThat(parentService.getParentById("id")).isEmpty();
    }

    @Test
    @DisplayName("should return parent by parentId")
    void getParentByParentId_found() {
        Parent parent = mock(Parent.class);
        when(parentRepository.findByParentId("pid")).thenReturn(Optional.of(parent));
        assertThat(parentService.getParentByParentId("pid")).contains(parent);
    }

    @Test
    @DisplayName("should return parent by user")
    void getParentByUser_found() {
        User user = mock(User.class);
        Parent parent = mock(Parent.class);
        when(parentRepository.findByUser(user)).thenReturn(Optional.of(parent));
        assertThat(parentService.getParentByUser(user)).contains(parent);
    }

    @Test
    @DisplayName("should return parent by email")
    void getParentByEmail_found() {
        Parent parent = mock(Parent.class);
        when(parentRepository.findByUserEmail("mail@x.com")).thenReturn(Optional.of(parent));
        assertThat(parentService.getParentByEmail("mail@x.com")).contains(parent);
    }

    @Test
    @DisplayName("should return active parent by email")
    void getActiveParentByEmail_found() {
        Parent parent = mock(Parent.class);
        when(parentRepository.findActiveParentByEmail("mail@x.com")).thenReturn(Optional.of(parent));
        assertThat(parentService.getActiveParentByEmail("mail@x.com")).contains(parent);
    }

    @Test
    @DisplayName("should return parents by student")
    void getParentsByStudent() {
        Student student = mock(Student.class);
        List<Parent> parents = List.of(mock(Parent.class));
        when(parentRepository.findByChildrenContaining(student)).thenReturn(parents);
        assertThat(parentService.getParentsByStudent(student)).isEqualTo(parents);
    }

    @Test
    @DisplayName("should return parents by student id")
    void getParentsByStudentId() {
        List<Parent> parents = List.of(mock(Parent.class));
        when(parentRepository.findParentsByChildStudentId("sid")).thenReturn(parents);
        assertThat(parentService.getParentsByStudentId("sid")).isEqualTo(parents);
    }
}
