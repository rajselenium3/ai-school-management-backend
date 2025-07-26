package com.eduai.schoolmanagement.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Invoice;
import com.eduai.schoolmanagement.entity.Student;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByStudent(Student student);

    List<Invoice> findByStatus(String status);

    List<Invoice> findByPaymentStatus(String paymentStatus);

    List<Invoice> findByAcademicYear(String academicYear);

    List<Invoice> findByTerm(String term);

    @Query("{'student.studentId': ?0}")
    List<Invoice> findByStudentStudentId(String studentId);

    @Query("{'dueDate': {$gte: ?0, $lte: ?1}}")
    List<Invoice> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("{'invoiceDate': {$gte: ?0, $lte: ?1}}")
    List<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("{'dueDate': {$lt: ?0}, 'paymentStatus': {$in: ['UNPAID', 'PARTIAL']}}")
    List<Invoice> findOverdueInvoices(LocalDate currentDate);

    @Query("{'student': ?0, 'academicYear': ?1}")
    List<Invoice> findByStudentAndAcademicYear(Student student, String academicYear);

    @Query("{'student': ?0, 'paymentStatus': 'UNPAID'}")
    List<Invoice> findUnpaidInvoicesByStudent(Student student);

    @Query("{'student': ?0, 'paymentStatus': 'PARTIAL'}")
    List<Invoice> findPartiallyPaidInvoicesByStudent(Student student);

    @Query("{'student': ?0, 'paymentStatus': 'PAID'}")
    List<Invoice> findPaidInvoicesByStudent(Student student);

    @Query("{'emailSent': false, 'status': 'SENT'}")
    List<Invoice> findInvoicesPendingEmailNotification();

    @Query("{'smsSent': false, 'status': 'SENT'}")
    List<Invoice> findInvoicesPendingSMSNotification();

    @Query("{'dueDate': {$lte: ?0}, 'paymentStatus': {$in: ['UNPAID', 'PARTIAL']}, 'reminderCount': {$lt: 3}}")
    List<Invoice> findInvoicesPendingReminder(LocalDate reminderDate);

    // Analytics and statistics
    long countByStatus(String status);

    long countByPaymentStatus(String paymentStatus);

    long countByAcademicYear(String academicYear);

    @Query(value = "{'paymentStatus': 'PAID', 'paidDate': {$gte: ?0, $lte: ?1}}", count = true)
    long countPaidInvoicesBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "{'dueDate': {$lt: ?0}, 'paymentStatus': {$in: ['UNPAID', 'PARTIAL']}}", count = true)
    long countOverdueInvoices(LocalDate currentDate);

    // Sum calculations for financial reporting
    @Query(value = "{'paymentStatus': 'PAID'}", fields = "{'totalAmount': 1}")
    List<Invoice> findPaidInvoiceAmounts();

    @Query(value = "{'paymentStatus': {$in: ['UNPAID', 'PARTIAL']}}", fields = "{'balanceAmount': 1}")
    List<Invoice> findOutstandingInvoiceAmounts();

    @Query(value = "{'academicYear': ?0, 'paymentStatus': 'PAID'}", fields = "{'totalAmount': 1}")
    List<Invoice> findPaidInvoiceAmountsByYear(String academicYear);

    @Query("{'student.studentId': ?0, 'academicYear': ?1, 'term': ?2}")
    Optional<Invoice> findByStudentAcademicYearAndTerm(String studentId, String academicYear, String term);
}
