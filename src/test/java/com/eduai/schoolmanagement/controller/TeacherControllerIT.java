package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.service.TeacherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeacherController.class)
class TeacherControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TeacherService teacherService;

    @Test
    @DisplayName("should return all teachers")
    void getAllTeachers() throws Exception {
        when(teacherService.getAllTeachers()).thenReturn(List.of());
        mockMvc.perform(get("/api/teachers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
