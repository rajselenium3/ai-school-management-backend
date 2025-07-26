package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.Invoice;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.service.InvoiceService;
import com.eduai.schoolmanagement.service.StudentService;
import com.eduai.schoolmanagement.service.FeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "Invoice generation and management operations")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final StudentService studentService;
    private final FeeService feeService;

    @GetMapping
    @Operation(summary = "Get all invoices")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String id) {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Get invoice by invoice number")
    public ResponseEntity<Invoice> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        Optional<Invoice> invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return invoice.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get invoices by student ID")
    public ResponseEntity<List<Invoice>> getInvoicesByStudentId(@PathVariable String studentId) {
        List<Invoice> invoices = invoiceService.getInvoicesByStudentId(studentId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get invoices by status")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable String status) {
        List<Invoice> invoices = invoiceService.getInvoicesByStatus(status);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    @Operation(summary = "Get invoices by payment status")
    public ResponseEntity<List<Invoice>> getInvoicesByPaymentStatus(@PathVariable String paymentStatus) {
        List<Invoice> invoices = invoiceService.getInvoicesByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices")
    public ResponseEntity<List<Invoice>> getOverdueInvoices() {
        List<Invoice> invoices = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/student/{studentId}/unpaid")
    @Operation(summary = "Get unpaid invoices by student")
    public ResponseEntity<List<Invoice>> getUnpaidInvoicesByStudent(@PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                List<Invoice> invoices = invoiceService.getUnpaidInvoicesByStudent(studentOpt.get());
                return ResponseEntity.ok(invoices);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get invoices between dates")
    public ResponseEntity<List<Invoice>> getInvoicesBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Invoice> invoices = invoiceService.getInvoicesBetween(startDate, endDate);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/student/{studentId}/outstanding")
    @Operation(summary = "Get student outstanding balance")
    public ResponseEntity<Object> getStudentOutstandingBalance(@PathVariable String studentId) {
        Double outstandingBalance = invoiceService.getStudentOutstandingBalance(studentId);
        return ResponseEntity.ok(java.util.Map.of(
            "studentId", studentId,
            "outstandingBalance", outstandingBalance
        ));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate invoice for student")
    public ResponseEntity<Invoice> generateInvoice(
            @RequestParam String studentId,
            @RequestParam String academicYear,
            @RequestParam String term,
            @RequestBody List<String> feeIds) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<Fee> fees = feeIds.stream()
                .map(feeService::getFeeById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

            if (fees.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Invoice invoice = invoiceService.generateInvoice(studentOpt.get(), academicYear, term, fees);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bulk-generate")
    @Operation(summary = "Generate bulk invoices")
    public ResponseEntity<List<Invoice>> generateBulkInvoices(
            @RequestParam String academicYear,
            @RequestParam String term,
            @RequestBody List<String> studentIds) {
        try {
            List<Student> students = studentIds.stream()
                .map(studentService::getStudentByStudentId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

            List<Invoice> invoices = invoiceService.generateBulkInvoices(students, academicYear, term);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Send invoice")
    public ResponseEntity<Invoice> sendInvoice(@PathVariable String id) {
        try {
            Invoice invoice = invoiceService.sendInvoice(id);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/mark-paid")
    @Operation(summary = "Mark invoice as paid")
    public ResponseEntity<Invoice> markInvoiceAsPaid(
            @PathVariable String id,
            @RequestParam Double paidAmount) {
        try {
            Invoice invoice = invoiceService.markInvoiceAsPaid(id, paidAmount);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/add-payment")
    @Operation(summary = "Add payment to invoice")
    public ResponseEntity<Invoice> addPaymentToInvoice(
            @PathVariable String id,
            @RequestParam String paymentId) {
        try {
            Invoice invoice = invoiceService.addPaymentToInvoice(id, paymentId);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel invoice")
    public ResponseEntity<Invoice> cancelInvoice(@PathVariable String id) {
        try {
            Invoice invoice = invoiceService.cancelInvoice(id);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create invoice")
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody Invoice invoice) {
        Invoice savedInvoice = invoiceService.saveInvoice(invoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedInvoice);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update invoice")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable String id, @Valid @RequestBody Invoice invoice) {
        Invoice updatedInvoice = invoiceService.updateInvoice(id, invoice);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete invoice")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/statistics")
//    @Operation(summary = "Get invoice statistics")
//    public ResponseEntity<Object> getInvoiceStatistics() {
//        Object statistics = invoiceService.getInvoiceStatistics();
//        return ResponseEntity.ok(statistics);
//    }

    @GetMapping("/revenue")
    @Operation(summary = "Get invoice revenue data")
    public ResponseEntity<Object> getInvoiceRevenueData() {
        Object revenueData = invoiceService.getInvoiceRevenueData();
        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<Object> getDashboardStatistics() {
        Object stats = invoiceService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/pending-reminders")
    @Operation(summary = "Get invoices pending reminders")
    public ResponseEntity<List<Invoice>> getInvoicesPendingReminder() {
        List<Invoice> invoices = invoiceService.getInvoicesPendingReminder();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/statuses")
    @Operation(summary = "Get available invoice statuses")
    public ResponseEntity<List<String>> getInvoiceStatuses() {
        List<String> statuses = List.of("DRAFT", "SENT", "PAID", "OVERDUE", "CANCELLED");
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/payment-statuses")
    @Operation(summary = "Get available payment statuses")
    public ResponseEntity<List<String>> getPaymentStatuses() {
        List<String> paymentStatuses = List.of("UNPAID", "PARTIAL", "PAID", "REFUNDED");
        return ResponseEntity.ok(paymentStatuses);
    }

    @GetMapping("/terms")
    @Operation(summary = "Get available invoice terms")
    public ResponseEntity<List<String>> getInvoiceTerms() {
        List<String> terms = List.of("MONTHLY", "QUARTERLY", "YEARLY", "SEMESTER");
        return ResponseEntity.ok(terms);
    }

    @GetMapping("/search")
    @Operation(summary = "Search invoices")
    public ResponseEntity<List<Invoice>> searchInvoices(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String term,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Invoice> invoices = invoiceService.getAllInvoices().stream()
            .filter(invoice -> studentId == null || invoice.getStudent().getStudentId().equals(studentId))
            .filter(invoice -> status == null || invoice.getStatus().equals(status))
            .filter(invoice -> paymentStatus == null || invoice.getPaymentStatus().equals(paymentStatus))
            .filter(invoice -> academicYear == null || invoice.getAcademicYear().equals(academicYear))
            .filter(invoice -> term == null || invoice.getTerm().equals(term))
            .filter(invoice -> startDate == null || invoice.getInvoiceDate() == null ||
                              !invoice.getInvoiceDate().isBefore(startDate))
            .filter(invoice -> endDate == null || invoice.getInvoiceDate() == null ||
                              !invoice.getInvoiceDate().isAfter(endDate))
            .toList();

        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/auto-generate")
    @Operation(summary = "Auto-generate invoices for all students")
    public ResponseEntity<Object> autoGenerateInvoices(
            @RequestParam String academicYear,
            @RequestParam String term,
            @RequestParam String grade) {
        try {
            List<Student> students = studentService.getStudentsByGrade(grade);
            List<Invoice> invoices = invoiceService.generateBulkInvoices(students, academicYear, term);

            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of(
                "message", "Invoices generated successfully",
                "count", invoices.size(),
                "academicYear", academicYear,
                "term", term,
                "grade", grade
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Failed to generate invoices: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get invoice statistics")
    public ResponseEntity<Object> getInvoiceStatistics() {
        List<Invoice> allInvoices = invoiceService.getAllInvoices();

        long totalInvoices = allInvoices.size();
        long paidInvoices = allInvoices.stream()
            .filter(i -> "PAID".equalsIgnoreCase(i.getStatus()))
            .count();
        long sentInvoices = allInvoices.stream()
            .filter(i -> "SENT".equalsIgnoreCase(i.getStatus()))
            .count();
        long overdueInvoices = allInvoices.stream()
            .filter(i -> "OVERDUE".equalsIgnoreCase(i.getStatus()))
            .count();
        long draftInvoices = allInvoices.stream()
            .filter(i -> "DRAFT".equalsIgnoreCase(i.getStatus()))
            .count();

        double totalAmount = allInvoices.stream()
            .mapToDouble(Invoice::getTotalAmount)
            .sum();

        double paidAmount = allInvoices.stream()
            .filter(i -> "PAID".equalsIgnoreCase(i.getStatus()))
            .mapToDouble(Invoice::getTotalAmount)
            .sum();

        double pendingAmount = totalAmount - paidAmount;
        double collectionRate = totalAmount > 0 ? (paidAmount * 100.0 / totalAmount) : 0;

        return ResponseEntity.ok(java.util.Map.of(
            "totalInvoices", totalInvoices,
            "paidInvoices", paidInvoices,
            "sentInvoices", sentInvoices,
            "overdueInvoices", overdueInvoices,
            "draftInvoices", draftInvoices,
            "totalAmount", Math.round(totalAmount * 100.0) / 100.0,
            "paidAmount", Math.round(paidAmount * 100.0) / 100.0,
            "pendingAmount", Math.round(pendingAmount * 100.0) / 100.0,
            "collectionRate", Math.round(collectionRate * 100.0) / 100.0
        ));
    }

//    @GetMapping("/statuses")
//    @Operation(summary = "Get available invoice statuses")
//    public ResponseEntity<List<String>> getInvoiceStatuses() {
//        List<String> statuses = List.of(
//            "DRAFT", "SENT", "PAID", "OVERDUE", "CANCELLED"
//        );
//        return ResponseEntity.ok(statuses);
//    }
}
