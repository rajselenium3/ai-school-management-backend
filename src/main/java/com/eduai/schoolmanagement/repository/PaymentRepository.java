package com.eduai.schoolmanagement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.entity.Payment;
import com.eduai.schoolmanagement.entity.Student;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByStudent(Student student);

    List<Payment> findByFee(Fee fee);

    List<Payment> findByStatus(String status);

    List<Payment> findByPaymentMethod(String paymentMethod);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    Optional<Payment> findByReceiptNumber(String receiptNumber);

    @Query("{'student.studentId': ?0}")
    List<Payment> findByStudentStudentId(String studentId);

    @Query("{'paymentDate': {$gte: ?0, $lte: ?1}}")
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{'dueDate': {$lt: ?0}, 'status': {$in: ['PENDING', 'FAILED']}}")
    List<Payment> findOverduePayments(LocalDateTime currentDate);

    @Query("{'student': ?0, 'status': 'COMPLETED'}")
    List<Payment> findCompletedPaymentsByStudent(Student student);

    @Query("{'student': ?0, 'status': {$in: ['PENDING', 'FAILED']}}")
    List<Payment> findPendingPaymentsByStudent(Student student);

    @Query("{'fee.feeType': ?0}")
    List<Payment> findByFeeType(String feeType);

    @Query("{'paymentGateway': ?0}")
    List<Payment> findByPaymentGateway(String gateway);

    // Analytics queries
    @Query("{'status': 'COMPLETED', 'paymentDate': {$gte: ?0, $lte: ?1}}")
    List<Payment> findCompletedPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "{'status': 'COMPLETED', 'paymentDate': {$gte: ?0, $lte: ?1}}",
           fields = "{'totalAmount': 1}")
    List<Payment> findPaymentAmountsBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Aggregation queries for statistics
    long countByStatus(String status);

    long countByPaymentMethod(String paymentMethod);

    @Query(value = "{'status': 'COMPLETED'}", count = true)
    long countCompletedPayments();

    @Query(value = "{'status': {$in: ['PENDING', 'FAILED']}}", count = true)
    long countPendingPayments();

    // Student-specific queries
    @Query("{'student': ?0, 'fee': ?1}")
    Optional<Payment> findByStudentAndFee(Student student, Fee fee);

    @Query("{'student.studentId': ?0, 'status': 'COMPLETED'}")
    List<Payment> findStudentCompletedPayments(String studentId);

    @Query("{'student.studentId': ?0, 'paymentDate': {$gte: ?1, $lte: ?2}}")
    List<Payment> findStudentPaymentsBetween(String studentId, LocalDateTime startDate, LocalDateTime endDate);
}
