package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Transaction;
import com.eduai.schoolmanagement.entity.Transaction.TransactionType;
import com.eduai.schoolmanagement.entity.Transaction.TransactionCategory;
import com.eduai.schoolmanagement.entity.Transaction.TransactionStatus;
import com.eduai.schoolmanagement.entity.Transaction.ApprovalStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    // Find by unique fields
    Optional<Transaction> findByTransactionNumber(String transactionNumber);
    Optional<Transaction> findByTransactionNumberAndInstitutionId(String transactionNumber, String institutionId);

    // Find by institution
    List<Transaction> findByInstitutionId(String institutionId);
    Page<Transaction> findByInstitutionId(String institutionId, Pageable pageable);

    // Find by status
    List<Transaction> findByInstitutionIdAndStatus(String institutionId, TransactionStatus status);
    Page<Transaction> findByInstitutionIdAndStatus(String institutionId, TransactionStatus status, Pageable pageable);

    // Find by approval status
    List<Transaction> findByInstitutionIdAndApprovalStatus(String institutionId, ApprovalStatus approvalStatus);
    Page<Transaction> findByInstitutionIdAndApprovalStatus(String institutionId, ApprovalStatus approvalStatus, Pageable pageable);

    // Find by transaction type
    List<Transaction> findByInstitutionIdAndTransactionType(String institutionId, TransactionType transactionType);
    Page<Transaction> findByInstitutionIdAndTransactionType(String institutionId, TransactionType transactionType, Pageable pageable);

    // Find by category
    List<Transaction> findByInstitutionIdAndCategory(String institutionId, TransactionCategory category);
    Page<Transaction> findByInstitutionIdAndCategory(String institutionId, TransactionCategory category, Pageable pageable);

    // Find by date ranges
    List<Transaction> findByInstitutionIdAndTransactionDateBetween(String institutionId, LocalDate startDate, LocalDate endDate);
    Page<Transaction> findByInstitutionIdAndTransactionDateBetween(String institutionId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Transaction> findByInstitutionIdAndCreatedDateBetween(String institutionId, LocalDateTime startDate, LocalDateTime endDate);

    // Find by related entities
    List<Transaction> findByInstitutionIdAndStudentId(String institutionId, String studentId);
    List<Transaction> findByInstitutionIdAndEmployeeId(String institutionId, String employeeId);
    List<Transaction> findByInstitutionIdAndVendorId(String institutionId, String vendorId);
    List<Transaction> findByInstitutionIdAndInvoiceId(String institutionId, String invoiceId);

    // Find by reconciliation status
    List<Transaction> findByInstitutionIdAndIsReconciled(String institutionId, Boolean isReconciled);
    Page<Transaction> findByInstitutionIdAndIsReconciled(String institutionId, Boolean isReconciled, Pageable pageable);

    // Find by account involvement
    @Query("{ 'institutionId': ?0, 'journalEntries.accountId': ?1 }")
    List<Transaction> findByInstitutionIdAndAccountId(String institutionId, String accountId);

    @Query("{ 'institutionId': ?0, 'journalEntries.accountCode': ?1 }")
    List<Transaction> findByInstitutionIdAndAccountCode(String institutionId, String accountCode);

    // Find transactions with specific account involvement in date range
    @Query("{ 'institutionId': ?0, 'journalEntries.accountId': ?1, 'transactionDate': { $gte: ?2, $lte: ?3 } }")
    List<Transaction> findByAccountIdAndDateRange(String institutionId, String accountId, LocalDate startDate, LocalDate endDate);

    // Search by description or reference
    @Query("{ 'institutionId': ?0, $or: [ { 'description': { $regex: ?1, $options: 'i' } }, { 'reference': { $regex: ?1, $options: 'i' } } ] }")
    List<Transaction> searchByDescriptionOrReference(String institutionId, String searchTerm);

    // Find by academic year
    List<Transaction> findByInstitutionIdAndAcademicYear(String institutionId, String academicYear);

    // Find transactions pending approval
    @Query("{ 'institutionId': ?0, 'status': 'PENDING', 'approvalStatus': 'PENDING' }")
    List<Transaction> findPendingApprovalTransactions(String institutionId);

    // Find transactions ready to post
    @Query("{ 'institutionId': ?0, 'status': 'APPROVED', 'approvalStatus': 'APPROVED' }")
    List<Transaction> findReadyToPostTransactions(String institutionId);

    // Find unbalanced transactions
    @Query("{ 'institutionId': ?0, $expr: { $ne: [ { $sum: '$journalEntries.debitAmount' }, { $sum: '$journalEntries.creditAmount' } ] } }")
    List<Transaction> findUnbalancedTransactions(String institutionId);

    // Find transactions by amount range
    @Query("{ 'institutionId': ?0, 'totalAmount': { $gte: ?1, $lte: ?2 } }")
    List<Transaction> findByAmountRange(String institutionId, Double minAmount, Double maxAmount);

    // Find transactions by payment method
    List<Transaction> findByInstitutionIdAndPaymentMethod(String institutionId, Transaction.PaymentMethod paymentMethod);

    // Find transactions by created user
    List<Transaction> findByInstitutionIdAndCreatedBy(String institutionId, String createdBy);
    List<Transaction> findByInstitutionIdAndApprovedBy(String institutionId, String approvedBy);

    // Statistical queries
    @Query(value = "{ 'institutionId': ?0, 'transactionType': ?1, 'status': 'POSTED' }", count = true)
    long countPostedTransactionsByType(String institutionId, TransactionType transactionType);

    @Query(value = "{ 'institutionId': ?0, 'transactionDate': { $gte: ?1, $lte: ?2 }, 'status': 'POSTED' }", count = true)
    long countPostedTransactionsInDateRange(String institutionId, LocalDate startDate, LocalDate endDate);

    // Sum queries for financial reporting
    @Query("{ $match: { 'institutionId': ?0, 'transactionDate': { $gte: ?1, $lte: ?2 }, 'status': 'POSTED' } }, " +
           "{ $group: { _id: '$transactionType', totalAmount: { $sum: '$totalAmount' } } }")
    List<Object> sumAmountByTypeInDateRange(String institutionId, LocalDate startDate, LocalDate endDate);

    @Query("{ $match: { 'institutionId': ?0, 'journalEntries.accountId': ?1, 'status': 'POSTED' } }, " +
           "{ $group: { _id: null, totalDebits: { $sum: '$journalEntries.debitAmount' }, totalCredits: { $sum: '$journalEntries.creditAmount' } } }")
    List<Object> sumDebitsAndCreditsByAccount(String institutionId, String accountId);

    // Find transactions for trial balance
    @Query("{ 'institutionId': ?0, 'transactionDate': { $lte: ?1 }, 'status': 'POSTED' }")
    List<Transaction> findPostedTransactionsUpToDate(String institutionId, LocalDate asOfDate);

    // Find transactions for cash flow statement
    @Query("{ 'institutionId': ?0, 'transactionDate': { $gte: ?1, $lte: ?2 }, 'status': 'POSTED', " +
           "'journalEntries.accountId': { $in: ?3 } }")
    List<Transaction> findCashFlowTransactions(String institutionId, LocalDate startDate, LocalDate endDate, List<String> cashAccountIds);

    // Find transactions for income statement
    @Query("{ 'institutionId': ?0, 'transactionDate': { $gte: ?1, $lte: ?2 }, 'status': 'POSTED', " +
           "'transactionType': { $in: ['INCOME', 'EXPENSE'] } }")
    List<Transaction> findIncomeStatementTransactions(String institutionId, LocalDate startDate, LocalDate endDate);

    // Find duplicate transactions (same amount, date, description)
    @Query("{ 'institutionId': ?0, 'totalAmount': ?1, 'transactionDate': ?2, 'description': ?3 }")
    List<Transaction> findPotentialDuplicates(String institutionId, Double amount, LocalDate date, String description);

    // Find transactions requiring reconciliation
    @Query("{ 'institutionId': ?0, 'paymentMethod': { $in: ['BANK_TRANSFER', 'CHECK'] }, 'isReconciled': false, 'status': 'POSTED' }")
    List<Transaction> findTransactionsNeedingReconciliation(String institutionId);

    // Find transactions by bank statement
    List<Transaction> findByInstitutionIdAndBankStatementId(String institutionId, String bankStatementId);

    // Find transactions modified recently
    @Query("{ 'institutionId': ?0, 'lastModifiedDate': { $gte: ?1 } }")
    List<Transaction> findRecentlyModifiedTransactions(String institutionId, LocalDateTime since);

    // Find transactions with attachments
    @Query("{ 'institutionId': ?0, 'attachmentUrls': { $exists: true, $not: { $size: 0 } } }")
    List<Transaction> findTransactionsWithAttachments(String institutionId);

    // Find transactions without attachments (for compliance checking)
    @Query("{ 'institutionId': ?0, 'totalAmount': { $gte: ?1 }, $or: [ { 'attachmentUrls': { $exists: false } }, { 'attachmentUrls': { $size: 0 } } ] }")
    List<Transaction> findLargeTransactionsWithoutAttachments(String institutionId, Double amountThreshold);

    // Validation queries
    boolean existsByTransactionNumberAndInstitutionId(String transactionNumber, String institutionId);

    // Custom delete operations
    void deleteByInstitutionIdAndTransactionNumber(String institutionId, String transactionNumber);

    // Find transactions for audit trail
    @Query("{ 'institutionId': ?0, 'createdDate': { $gte: ?1, $lte: ?2 } }")
    List<Transaction> findTransactionsForAudit(String institutionId, LocalDateTime startDate, LocalDateTime endDate);

    // Count transactions by month for given year
    @Query("{ 'institutionId': ?0, 'createdDate': { $gte: ?1, $lte: ?2 } }")
    long countByInstitutionIdAndCreatedDateBetween(String institutionId, LocalDateTime startDate, LocalDateTime endDate);
}
