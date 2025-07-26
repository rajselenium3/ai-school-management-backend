package com.eduai.schoolmanagement.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.entity.Payment;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.service.FeeService;
import com.eduai.schoolmanagement.service.PaymentService;
import com.eduai.schoolmanagement.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Payment processing and tracking operations")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final StudentService studentService;
    private final FeeService feeService;

    @GetMapping
    @Operation(summary = "Get all payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<Payment> getPaymentById(@PathVariable String id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get payments by student ID")
    public ResponseEntity<List<Payment>> getPaymentsByStudentId(@PathVariable String studentId) {
        List<Payment> payments = paymentService.getPaymentsByStudentId(studentId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue payments")
    public ResponseEntity<List<Payment>> getOverduePayments() {
        List<Payment> payments = paymentService.getOverduePayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/completed")
    @Operation(summary = "Get completed payments between dates")
    public ResponseEntity<List<Payment>> getCompletedPaymentsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Payment> payments = paymentService.getCompletedPaymentsBetween(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        Optional<Payment> payment = paymentService.getPaymentByTransactionId(transactionId);
        return payment.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/receipt/{receiptNumber}")
    @Operation(summary = "Get payment by receipt number")
    public ResponseEntity<Payment> getPaymentByReceiptNumber(@PathVariable String receiptNumber) {
        Optional<Payment> payment = paymentService.getPaymentByReceiptNumber(receiptNumber);
        return payment.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}/completed")
    @Operation(summary = "Get completed payments by student")
    public ResponseEntity<List<Payment>> getCompletedPaymentsByStudent(@PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                List<Payment> payments = paymentService.getCompletedPaymentsByStudent(studentOpt.get());
                return ResponseEntity.ok(payments);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}/pending")
    @Operation(summary = "Get pending payments by student")
    public ResponseEntity<List<Payment>> getPendingPaymentsByStudent(@PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                List<Payment> payments = paymentService.getPendingPaymentsByStudent(studentOpt.get());
                return ResponseEntity.ok(payments);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}/history")
    @Operation(summary = "Get student payment history")
    public ResponseEntity<List<Payment>> getStudentPaymentHistory(@PathVariable String studentId) {
        List<Payment> payments = paymentService.getStudentPaymentHistory(studentId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/student/{studentId}/outstanding")
    @Operation(summary = "Get student outstanding amount")
    public ResponseEntity<Object> getStudentOutstandingAmount(@PathVariable String studentId) {
        Double outstandingAmount = paymentService.getStudentOutstandingAmount(studentId);
        return ResponseEntity.ok(java.util.Map.of(
            "studentId", studentId,
            "outstandingAmount", outstandingAmount
        ));
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment")
    public ResponseEntity<Payment> initiatePayment(
            @RequestParam String studentId,
            @RequestParam String feeId,
            @RequestParam Double amount,
            @RequestParam String paymentMethod) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            Optional<Fee> feeOpt = feeService.getFeeById(feeId);

            if (studentOpt.isPresent() && feeOpt.isPresent()) {
                Payment payment = paymentService.initiatePayment(
                    studentOpt.get(), feeOpt.get(), amount, paymentMethod);
                return ResponseEntity.status(HttpStatus.CREATED).body(payment);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process payment")
    public ResponseEntity<Payment> processPayment(
            @PathVariable String id,
            @RequestParam String transactionId,
            @RequestParam String paymentGateway) {
        try {
            Payment payment = paymentService.processPayment(id, transactionId, paymentGateway);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/fail")
    @Operation(summary = "Mark payment as failed")
    public ResponseEntity<Payment> failPayment(
            @PathVariable String id,
            @RequestParam String reason) {
        try {
            Payment payment = paymentService.failPayment(id, reason);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund payment")
    public ResponseEntity<Payment> refundPayment(
            @PathVariable String id,
            @RequestParam Double refundAmount,
            @RequestParam String refundReason) {
        try {
            Payment payment = paymentService.refundPayment(id, refundAmount, refundReason);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping
    @Operation(summary = "Create payment")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        Payment savedPayment = paymentService.savePayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get payment statistics")
    public ResponseEntity<Object> getPaymentStatistics() {
        Object statistics = paymentService.getPaymentStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue data")
    public ResponseEntity<Object> getRevenueData() {
        Object revenueData = paymentService.getRevenueData();
        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<Object> getDashboardStatistics() {
        Object stats = paymentService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "Get available payment methods")
    public ResponseEntity<List<String>> getPaymentMethods() {
        List<String> paymentMethods = List.of(
            "CASH", "CARD", "BANK_TRANSFER", "ONLINE", "UPI", "CHEQUE"
        );
        return ResponseEntity.ok(paymentMethods);
    }

    @GetMapping("/payment-gateways")
    @Operation(summary = "Get available payment gateways")
    public ResponseEntity<List<String>> getPaymentGateways() {
        List<String> gateways = List.of("RAZORPAY", "PAYTM", "STRIPE", "PAYPAL");
        return ResponseEntity.ok(gateways);
    }

    @PostMapping("/quick-payment")
    @Operation(summary = "Quick payment processing")
    public ResponseEntity<Payment> quickPayment(
            @RequestParam String studentId,
            @RequestParam String feeId,
            @RequestParam Double amount,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            Optional<Fee> feeOpt = feeService.getFeeById(feeId);

            if (studentOpt.isPresent() && feeOpt.isPresent()) {
                // Initiate and immediately process payment
                Payment payment = paymentService.initiatePayment(
                    studentOpt.get(), feeOpt.get(), amount, paymentMethod);
                payment = paymentService.processPayment(payment.getId(), transactionId, "MANUAL");
                return ResponseEntity.status(HttpStatus.CREATED).body(payment);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search payments")
    public ResponseEntity<List<Payment>> searchPayments(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Payment> payments = paymentService.getAllPayments().stream()
            .filter(payment -> studentId == null || payment.getStudent().getStudentId().equals(studentId))
            .filter(payment -> status == null || payment.getStatus().equals(status))
            .filter(payment -> paymentMethod == null || payment.getPaymentMethod().equals(paymentMethod))
            .filter(payment -> startDate == null || payment.getPaymentDate() == null ||
                              payment.getPaymentDate().isAfter(startDate))
            .filter(payment -> endDate == null || payment.getPaymentDate() == null ||
                              payment.getPaymentDate().isBefore(endDate))
            .toList();

        return ResponseEntity.ok(payments);
    }
}
