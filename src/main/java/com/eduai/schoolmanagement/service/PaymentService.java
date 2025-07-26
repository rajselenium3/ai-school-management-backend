package com.eduai.schoolmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.entity.Payment;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getPaymentsByStudent(Student student) {
        return paymentRepository.findByStudent(student);
    }

    public List<Payment> getPaymentsByStudentId(String studentId) {
        return paymentRepository.findByStudentStudentId(studentId);
    }

    public List<Payment> getPaymentsByFee(Fee fee) {
        return paymentRepository.findByFee(fee);
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> getCompletedPaymentsByStudent(Student student) {
        return paymentRepository.findCompletedPaymentsByStudent(student);
    }

    public List<Payment> getPendingPaymentsByStudent(Student student) {
        return paymentRepository.findPendingPaymentsByStudent(student);
    }

    public List<Payment> getOverduePayments() {
        return paymentRepository.findOverduePayments(LocalDateTime.now());
    }

    public List<Payment> getPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    public List<Payment> getCompletedPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findCompletedPaymentsBetween(startDate, endDate);
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    public Optional<Payment> getPaymentByReceiptNumber(String receiptNumber) {
        return paymentRepository.findByReceiptNumber(receiptNumber);
    }

    public Payment initiatePayment(Student student, Fee fee, Double amount, String paymentMethod) {
        log.info("Initiating payment for student {} for fee {}", student.getStudentId(), fee.getFeeName());

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setFee(fee);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("PENDING");
        payment.setDueDate(fee.getDueDate().atStartOfDay());

        // Generate reference number
        payment.setReferenceNumber(generateReferenceNumber());

        // Calculate total amount (including discounts and late fees)
        Double discountAmount = calculateDiscountAmount(student, fee, amount);
        Double lateFeeAmount = calculateLateFeeAmount(fee);
        Double totalAmount = amount - discountAmount + lateFeeAmount;

        payment.setDiscountAmount(discountAmount);
        payment.setLateFeeAmount(lateFeeAmount);
        payment.setTotalAmount(totalAmount);

        return paymentRepository.save(payment);
    }

    public Payment processPayment(String paymentId, String transactionId, String paymentGateway) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setTransactionId(transactionId);
            payment.setPaymentGateway(paymentGateway);
            payment.setStatus("COMPLETED");
            payment.setCompletedDate(LocalDateTime.now());
            payment.setPaymentDate(LocalDateTime.now());

            // Generate receipt number
            payment.setReceiptNumber(generateReceiptNumber());

            log.info("Payment processed successfully: {}", transactionId);
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }

    public Payment failPayment(String paymentId, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("FAILED");
            payment.setRemarks(reason);

            log.info("Payment failed: {} - {}", paymentId, reason);
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }

    public Payment refundPayment(String paymentId, Double refundAmount, String refundReason) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();

            if (!"COMPLETED".equals(payment.getStatus())) {
                throw new RuntimeException("Cannot refund payment that is not completed");
            }

            Payment.RefundDetails refund = new Payment.RefundDetails();
            refund.setRefundAmount(refundAmount);
            refund.setRefundReason(refundReason);
            refund.setRefundDate(LocalDateTime.now());
            refund.setRefundStatus("INITIATED");

            payment.setRefund(refund);
            payment.setStatus("REFUNDED");

            log.info("Refund initiated for payment: {} amount: {}", paymentId, refundAmount);
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }

    public Payment savePayment(Payment payment) {
        log.info("Saving payment for student: {}", payment.getStudent().getStudentId());

        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }

    public void deletePayment(String id) {
        log.info("Deleting payment with id: {}", id);
        paymentRepository.deleteById(id);
    }

    public Object getPaymentStatistics() {
        long totalPayments = paymentRepository.count();
        long completedPayments = paymentRepository.countCompletedPayments();
        long pendingPayments = paymentRepository.countPendingPayments();

        return java.util.Map.of(
            "totalPayments", totalPayments,
            "completedPayments", completedPayments,
            "pendingPayments", pendingPayments,
            "successRate", totalPayments > 0 ? (completedPayments * 100.0 / totalPayments) : 0,
            "paymentMethodBreakdown", getPaymentMethodBreakdown(),
            "revenueData", getRevenueData()
        );
    }

    public Object getRevenueData() {
        List<Payment> completedPayments = paymentRepository.findByStatus("COMPLETED");
        Double totalRevenue = completedPayments.stream()
            .mapToDouble(Payment::getTotalAmount)
            .sum();

        Double thisMonthRevenue = completedPayments.stream()
            .filter(p -> p.getPaymentDate().isAfter(LocalDateTime.now().minusMonths(1)))
            .mapToDouble(Payment::getTotalAmount)
            .sum();

        return java.util.Map.of(
            "totalRevenue", totalRevenue,
            "thisMonthRevenue", thisMonthRevenue,
            "averagePaymentAmount", completedPayments.isEmpty() ? 0 : totalRevenue / completedPayments.size()
        );
    }

    private Object getPaymentMethodBreakdown() {
        return java.util.Map.of(
            "CASH", paymentRepository.countByPaymentMethod("CASH"),
            "CARD", paymentRepository.countByPaymentMethod("CARD"),
            "BANK_TRANSFER", paymentRepository.countByPaymentMethod("BANK_TRANSFER"),
            "ONLINE", paymentRepository.countByPaymentMethod("ONLINE"),
            "UPI", paymentRepository.countByPaymentMethod("UPI"),
            "CHEQUE", paymentRepository.countByPaymentMethod("CHEQUE")
        );
    }

    private Double calculateDiscountAmount(Student student, Fee fee, Double amount) {
        // Implement discount logic based on student profile and fee rules
        // This is a simplified implementation
        if (fee.getDiscountRules() != null && !fee.getDiscountRules().isEmpty()) {
            // Apply first applicable discount rule
            return fee.getDiscountRules().stream()
                .filter(rule -> rule.isActive())
                .findFirst()
                .map(rule -> {
                    if ("PERCENTAGE".equals(rule.getDiscountType())) {
                        return amount * rule.getDiscountValue() / 100;
                    } else if ("FIXED_AMOUNT".equals(rule.getDiscountType())) {
                        return Math.min(amount, rule.getDiscountValue());
                    }
                    return 0.0;
                })
                .orElse(0.0);
        }
        return 0.0;
    }

    private Double calculateLateFeeAmount(Fee fee) {
        if (fee.getDueDate() != null && fee.getDueDate().isBefore(LocalDateTime.now().toLocalDate())) {
            if (fee.getLateFeeAmount() != null) {
                return fee.getLateFeeAmount();
            } else if (fee.getLateFeePercentage() != null) {
                return fee.getAmount() * fee.getLateFeePercentage() / 100;
            }
        }
        return 0.0;
    }

    private String generateReferenceNumber() {
        return "PAY" + System.currentTimeMillis();
    }

    private String generateReceiptNumber() {
        return "RCP" + System.currentTimeMillis();
    }

    public List<Payment> getStudentPaymentHistory(String studentId) {
        return paymentRepository.findStudentCompletedPayments(studentId);
    }

    public Double getStudentOutstandingAmount(String studentId) {
        List<Payment> pendingPayments = paymentRepository.findByStudentStudentId(studentId)
            .stream()
            .filter(p -> "PENDING".equals(p.getStatus()) || "FAILED".equals(p.getStatus()))
            .toList();

        return pendingPayments.stream()
            .mapToDouble(Payment::getTotalAmount)
            .sum();
    }

    public Object getDashboardStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);

        List<Payment> thisMonthPayments = getCompletedPaymentsBetween(startOfMonth, now);
        List<Payment> thisWeekPayments = getCompletedPaymentsBetween(startOfWeek, now);

        Double monthlyRevenue = thisMonthPayments.stream().mapToDouble(Payment::getTotalAmount).sum();
        Double weeklyRevenue = thisWeekPayments.stream().mapToDouble(Payment::getTotalAmount).sum();

        return java.util.Map.of(
            "monthlyRevenue", monthlyRevenue,
            "weeklyRevenue", weeklyRevenue,
            "monthlyPaymentCount", thisMonthPayments.size(),
            "weeklyPaymentCount", thisWeekPayments.size(),
            "pendingPayments", paymentRepository.countPendingPayments(),
            "overduePayments", getOverduePayments().size()
        );
    }
}
