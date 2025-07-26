package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("should return all students")
    void getAllStudents() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());
        mockMvc.perform(get("/api/students").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
