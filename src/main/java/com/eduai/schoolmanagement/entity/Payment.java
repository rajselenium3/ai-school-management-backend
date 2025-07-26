package com.eduai.schoolmanagement.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "payments")
public class Payment extends BaseEntity {

    @DBRef
    @NotNull(message = "Student reference is required")
    private Student student;

    @DBRef
    @NotNull(message = "Fee reference is required")
    private Fee fee;

    // Payment details
    @NotNull(message = "Payment amount is required")
    @Min(value = 0, message = "Payment amount must be positive")
    private Double amount;

    private Double discountAmount;
    private Double lateFeeAmount;
    private Double totalAmount;

    // Payment method and transaction
    private String paymentMethod; // CASH, CARD, BANK_TRANSFER, ONLINE, CHEQUE, UPI
    private String transactionId;
    private String referenceNumber;
    private String paymentGateway; // RAZORPAY, PAYTM, STRIPE, etc.

    // Payment status and dates
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED
    private LocalDateTime paymentDate;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;

    // Payment breakdown
    private List<FeeBreakdown> feeBreakdown;
    private String remarks;
    private String receiptNumber;

    // Refund details
    private RefundDetails refund;

    // Additional metadata
    private Map<String, Object> paymentMetadata;
    private PaymentAnalytics analytics;

    @Data
    public static class FeeBreakdown {
        private String feeComponent; // What part of the fee this covers
        private Double amount;
        private String description;
    }

    @Data
    public static class RefundDetails {
        private Double refundAmount;
        private String refundReason;
        private LocalDateTime refundDate;
        private String refundMethod;
        private String refundTransactionId;
        private String refundStatus; // INITIATED, COMPLETED, FAILED
    }

    @Data
    public static class PaymentAnalytics {
        private String paymentChannel; // WEBSITE, MOBILE_APP, OFFLINE
        private String deviceInfo;
        private String ipAddress;
        private Integer attemptCount;
        private LocalDateTime firstAttempt;
    }
}
