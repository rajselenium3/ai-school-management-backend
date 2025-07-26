package com.eduai.schoolmanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.entity.Invoice;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final FeeService feeService;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getInvoiceById(String id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    public List<Invoice> getInvoicesByStudent(Student student) {
        return invoiceRepository.findByStudent(student);
    }

    public List<Invoice> getInvoicesByStudentId(String studentId) {
        return invoiceRepository.findByStudentStudentId(studentId);
    }

    public List<Invoice> getInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status);
    }

    public List<Invoice> getInvoicesByPaymentStatus(String paymentStatus) {
        return invoiceRepository.findByPaymentStatus(paymentStatus);
    }

    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDate.now());
    }

    public List<Invoice> getUnpaidInvoicesByStudent(Student student) {
        return invoiceRepository.findUnpaidInvoicesByStudent(student);
    }

    public List<Invoice> getInvoicesBetween(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
    }

    public Invoice generateInvoice(Student student, String academicYear, String term, List<Fee> fees) {
        log.info("Generating invoice for student {} for term {}", student.getStudentId(), term);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setStudent(student);
        invoice.setAcademicYear(academicYear);
        invoice.setTerm(term);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setStatus("DRAFT");
        invoice.setPaymentStatus("UNPAID");

        // Create invoice items from fees
        List<Invoice.InvoiceItem> items = new ArrayList<>();
        Double subtotal = 0.0;

        for (Fee fee : fees) {
            Invoice.InvoiceItem item = new Invoice.InvoiceItem();
            item.setFee(fee);
            item.setDescription(fee.getFeeName());
            item.setQuantity(1);
            item.setUnitPrice(fee.getAmount());
            item.setAmount(fee.getAmount());
            item.setFeeType(fee.getFeeType());
            item.setMandatory(fee.isMandatory());

            // Calculate discounts if applicable
            Double discountAmount = calculateItemDiscount(fee, student);
            item.setDiscountAmount(discountAmount);
            item.setAmount(fee.getAmount() - discountAmount);

            items.add(item);
            subtotal += item.getAmount();
        }

        invoice.setItems(items);
        invoice.setSubtotal(subtotal);

        // Calculate total discount
        Double totalDiscount = items.stream()
            .mapToDouble(item -> item.getDiscountAmount() != null ? item.getDiscountAmount() : 0.0)
            .sum();
        invoice.setDiscountAmount(totalDiscount);

        // For now, no tax calculation
        invoice.setTaxAmount(0.0);

        Double totalAmount = subtotal - totalDiscount;
        invoice.setTotalAmount(totalAmount);
        invoice.setPaidAmount(0.0);
        invoice.setBalanceAmount(totalAmount);

        // Set due date (30 days from invoice date)
        invoice.setDueDate(LocalDate.now().plusDays(30));

        // Set payment terms
        invoice.setTerms("Payment is due within 30 days of invoice date. Late fees may apply after due date.");

        return invoiceRepository.save(invoice);
    }

    public Invoice sendInvoice(String invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            invoice.setStatus("SENT");
            invoice.setSentDate(LocalDateTime.now());

            log.info("Invoice sent: {}", invoice.getInvoiceNumber());
            return invoiceRepository.save(invoice);
        }
        throw new RuntimeException("Invoice not found with id: " + invoiceId);
    }

    public Invoice markInvoiceAsPaid(String invoiceId, Double paidAmount) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();

            Double currentPaidAmount = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : 0.0;
            Double newPaidAmount = currentPaidAmount + paidAmount;

            invoice.setPaidAmount(newPaidAmount);
            invoice.setBalanceAmount(invoice.getTotalAmount() - newPaidAmount);

            if (invoice.getBalanceAmount() <= 0) {
                invoice.setPaymentStatus("PAID");
                invoice.setPaidDate(LocalDateTime.now());
            } else {
                invoice.setPaymentStatus("PARTIAL");
            }

            log.info("Invoice payment updated: {} paid: {}", invoice.getInvoiceNumber(), paidAmount);
            return invoiceRepository.save(invoice);
        }
        throw new RuntimeException("Invoice not found with id: " + invoiceId);
    }

    public Invoice addPaymentToInvoice(String invoiceId, String paymentId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();

            if (invoice.getPaymentIds() == null) {
                invoice.setPaymentIds(new ArrayList<>());
            }

            invoice.getPaymentIds().add(paymentId);

            return invoiceRepository.save(invoice);
        }
        throw new RuntimeException("Invoice not found with id: " + invoiceId);
    }

    public Invoice saveInvoice(Invoice invoice) {
        log.info("Saving invoice: {}", invoice.getInvoiceNumber());

        if (invoice.getCreatedAt() == null) {
            invoice.setCreatedAt(LocalDateTime.now());
        }

        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(String id, Invoice invoice) {
        invoice.setId(id);
        log.info("Updating invoice with id: {}", id);
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(String id) {
        log.info("Deleting invoice with id: {}", id);
        invoiceRepository.deleteById(id);
    }

    public Invoice cancelInvoice(String invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();

            if ("PAID".equals(invoice.getPaymentStatus())) {
                throw new RuntimeException("Cannot cancel a paid invoice");
            }

            invoice.setStatus("CANCELLED");
            invoice.setPaymentStatus("CANCELLED");

            log.info("Invoice cancelled: {}", invoice.getInvoiceNumber());
            return invoiceRepository.save(invoice);
        }
        throw new RuntimeException("Invoice not found with id: " + invoiceId);
    }

    public List<Invoice> generateBulkInvoices(List<Student> students, String academicYear, String term) {
        log.info("Generating bulk invoices for {} students", students.size());

        List<Invoice> invoices = new ArrayList<>();

        for (Student student : students) {
            try {
                // Get applicable fees for this student
                List<Fee> applicableFees = feeService.getApplicableFeesForStudent(
                    student.getGrade(),
                    "GENERAL", // Default department
                    academicYear
                );

                if (!applicableFees.isEmpty()) {
                    Invoice invoice = generateInvoice(student, academicYear, term, applicableFees);
                    invoices.add(invoice);
                }
            } catch (Exception e) {
                log.error("Failed to generate invoice for student {}: {}", student.getStudentId(), e.getMessage());
            }
        }

        return invoices;
    }

    public Object getInvoiceStatistics() {
        long totalInvoices = invoiceRepository.count();
        long paidInvoices = invoiceRepository.countByPaymentStatus("PAID");
        long unpaidInvoices = invoiceRepository.countByPaymentStatus("UNPAID");
        long overdueInvoices = invoiceRepository.countOverdueInvoices(LocalDate.now());

        return java.util.Map.of(
            "totalInvoices", totalInvoices,
            "paidInvoices", paidInvoices,
            "unpaidInvoices", unpaidInvoices,
            "overdueInvoices", overdueInvoices,
            "collectionRate", totalInvoices > 0 ? (paidInvoices * 100.0 / totalInvoices) : 0,
            "revenueData", getInvoiceRevenueData()
        );
    }

    public Object getInvoiceRevenueData() {
        List<Invoice> paidInvoices = invoiceRepository.findPaidInvoiceAmounts();
        List<Invoice> outstandingInvoices = invoiceRepository.findOutstandingInvoiceAmounts();

        Double totalRevenue = paidInvoices.stream()
            .mapToDouble(Invoice::getTotalAmount)
            .sum();

        Double outstandingAmount = outstandingInvoices.stream()
            .mapToDouble(Invoice::getBalanceAmount)
            .sum();

        return java.util.Map.of(
            "totalRevenue", totalRevenue,
            "outstandingAmount", outstandingAmount,
            "averageInvoiceAmount", paidInvoices.isEmpty() ? 0 : totalRevenue / paidInvoices.size()
        );
    }

    public List<Invoice> getInvoicesPendingReminder() {
        LocalDate reminderDate = LocalDate.now().plusDays(3); // Send reminder 3 days before due
        return invoiceRepository.findInvoicesPendingReminder(reminderDate);
    }

    private String generateInvoiceNumber() {
        return "INV" + System.currentTimeMillis();
    }

    private Double calculateItemDiscount(Fee fee, Student student) {
        // Implement discount calculation logic
        // This is a simplified implementation
        if (fee.getDiscountRules() != null && !fee.getDiscountRules().isEmpty()) {
            return fee.getDiscountRules().stream()
                .filter(rule -> rule.isActive())
                .findFirst()
                .map(rule -> {
                    if ("PERCENTAGE".equals(rule.getDiscountType())) {
                        return fee.getAmount() * rule.getDiscountValue() / 100;
                    } else if ("FIXED_AMOUNT".equals(rule.getDiscountType())) {
                        return Math.min(fee.getAmount(), rule.getDiscountValue());
                    }
                    return 0.0;
                })
                .orElse(0.0);
        }
        return 0.0;
    }

    public Double getStudentOutstandingBalance(String studentId) {
        List<Invoice> unpaidInvoices = invoiceRepository.findByStudentStudentId(studentId)
            .stream()
            .filter(invoice -> !"PAID".equals(invoice.getPaymentStatus()) && !"CANCELLED".equals(invoice.getStatus()))
            .toList();

        return unpaidInvoices.stream()
            .mapToDouble(Invoice::getBalanceAmount)
            .sum();
    }

    public Object getDashboardStatistics() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);

        List<Invoice> thisMonthInvoices = getInvoicesBetween(startOfMonth, now);
        Long thisMonthPaidInvoices = invoiceRepository.countPaidInvoicesBetween(
            startOfMonth.atStartOfDay(),
            now.atTime(23, 59, 59)
        );

        return java.util.Map.of(
            "monthlyInvoices", thisMonthInvoices.size(),
            "monthlyPaidInvoices", thisMonthPaidInvoices,
            "pendingInvoices", invoiceRepository.countByPaymentStatus("UNPAID"),
            "overdueInvoices", getOverdueInvoices().size(),
            "monthlyCollectionRate", thisMonthInvoices.isEmpty() ? 0 :
                (thisMonthPaidInvoices * 100.0 / thisMonthInvoices.size())
        );
    }
}
