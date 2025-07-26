package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.service.GradeService;
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

@WebMvcTest(GradeController.class)
class GradeControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GradeService gradeService;

    @Test
    @DisplayName("should return all grades")
    void getAllGrades() throws Exception {
        when(gradeService.getAllGrades()).thenReturn(List.of());
        mockMvc.perform(get("/api/grades").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
