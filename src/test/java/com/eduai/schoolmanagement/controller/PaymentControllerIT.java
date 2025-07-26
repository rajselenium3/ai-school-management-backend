package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.service.PaymentService;
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

@WebMvcTest(PaymentController.class)
class PaymentControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("should return all payments")
    void getAllPayments() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of());
        mockMvc.perform(get("/api/payments").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
