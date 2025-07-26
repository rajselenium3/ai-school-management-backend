package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.service.FeeService;
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

@WebMvcTest(FeeController.class)
class FeeControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FeeService feeService;

    @Test
    @DisplayName("should return all fees")
    void getAllFees() throws Exception {
        when(feeService.getAllFees()).thenReturn(List.of());
        mockMvc.perform(get("/api/fees").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
