package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.entity.Payment;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("should return all payments")
    void getAllPayments_returnsAll() {
        List<Payment> payments = List.of(mock(Payment.class));
        when(paymentRepository.findAll()).thenReturn(payments);
        assertThat(paymentService.getAllPayments()).isEqualTo(payments);
    }

    @Test
    @DisplayName("should return payment by id")
    void getPaymentById_found() {
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById("id")).thenReturn(Optional.of(payment));
        assertThat(paymentService.getPaymentById("id")).contains(payment);
    }

    @Test
    @DisplayName("should return empty if payment by id not found")
    void getPaymentById_notFound() {
        when(paymentRepository.findById("id")).thenReturn(Optional.empty());
        assertThat(paymentService.getPaymentById("id")).isEmpty();
    }

    @Test
    @DisplayName("should return payments by student")
    void getPaymentsByStudent() {
        Student student = mock(Student.class);
        List<Payment> payments = List.of(mock(Payment.class));
        when(paymentRepository.findByStudent(student)).thenReturn(payments);
        assertThat(paymentService.getPaymentsByStudent(student)).isEqualTo(payments);
    }

    @Test
    @DisplayName("should return payments by student id")
    void getPaymentsByStudentId() {
        List<Payment> payments = List.of(mock(Payment.class));
        when(paymentRepository.findByStudentStudentId("sid")).thenReturn(payments);
        assertThat(paymentService.getPaymentsByStudentId("sid")).isEqualTo(payments);
    }

    @Test
    @DisplayName("should return payments by fee")
    void getPaymentsByFee() {
        Fee fee = mock(Fee.class);
        List<Payment> payments = List.of(mock(Payment.class));
        when(paymentRepository.findByFee(fee)).thenReturn(payments);
        assertThat(paymentService.getPaymentsByFee(fee)).isEqualTo(payments);
    }

    @Test
    @DisplayName("should return payments by status")
    void getPaymentsByStatus() {
        List<Payment> payments = List.of(mock(Payment.class));
        when(paymentRepository.findByStatus("PAID")).thenReturn(payments);
        assertThat(paymentService.getPaymentsByStatus("PAID")).isEqualTo(payments);
    }
}
