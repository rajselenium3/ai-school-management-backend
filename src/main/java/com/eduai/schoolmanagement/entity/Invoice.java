package com.eduai.schoolmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "invoices")
public class Invoice extends BaseEntity {

    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;

    @DBRef
    @NotNull(message = "Student reference is required")
    private Student student;

    // Invoice details
    private String academicYear;
    private String term; // MONTHLY, QUARTERLY, YEARLY
    private LocalDate invoiceDate;
    private LocalDate dueDate;

    // Amounts
    private Double subtotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private Double paidAmount;
    private Double balanceAmount;

    // Invoice items (fees)
    private List<InvoiceItem> items;

    // Status and payment tracking
    private String status; // DRAFT, SENT, PAID, OVERDUE, CANCELLED
    private LocalDateTime sentDate;
    private LocalDateTime paidDate;

    // Payment tracking
    private List<String> paymentIds; // References to Payment entities
    private String paymentStatus; // UNPAID, PARTIAL, PAID, REFUNDED

    // Communication
    private boolean emailSent;
    private boolean smsSent;
    private LocalDateTime lastReminderSent;
    private Integer reminderCount;

    // Additional details
    private String notes;
    private String terms; // Payment terms and conditions

    @Data
    public static class InvoiceItem {
        @DBRef
        private Fee fee;

        private String description;
        private Integer quantity;
        private Double unitPrice;
        private Double discountAmount;
        private Double amount;
        private String feeType;

        // Additional item details
        private String period; // Which period this fee covers
        private boolean mandatory;
    }
}
