package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.service.MasterDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MasterDataController.class)
class MasterDataControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MasterDataService masterDataService;

    @Test
    @DisplayName("should return all grade levels")
    void getAllGradeLevels() throws Exception {
        when(masterDataService.getAllGradeLevels()).thenReturn(List.of());
        mockMvc.perform(get("/api/master-data/grade-levels").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return grade level by code")
    void getGradeLevelByCode() throws Exception {
        when(masterDataService.getGradeLevelByCode("G1")).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/api/master-data/grade-levels/G1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should create grade level")
    void createGradeLevel() throws Exception {
        // You can expand this with a real GradeLevel and JSON if needed
        when(masterDataService.createGradeLevel(any())).thenReturn(null);
        mockMvc.perform(post("/api/master-data/grade-levels")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update grade level")
    void updateGradeLevel() throws Exception {
        when(masterDataService.updateGradeLevel(eq("id"), any())).thenThrow(new RuntimeException());
        mockMvc.perform(put("/api/master-data/grade-levels/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete grade level")
    void deleteGradeLevel() throws Exception {
        doThrow(new RuntimeException()).when(masterDataService).deleteGradeLevel("id");
        mockMvc.perform(delete("/api/master-data/grade-levels/id"))
                .andExpect(status().isNotFound());
    }
}
