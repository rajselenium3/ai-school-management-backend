package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Transaction;
import com.eduai.schoolmanagement.entity.Transaction.TransactionType;
import com.eduai.schoolmanagement.entity.Transaction.TransactionCategory;
import com.eduai.schoolmanagement.entity.Transaction.TransactionStatus;
import com.eduai.schoolmanagement.entity.Transaction.ApprovalStatus;
import com.eduai.schoolmanagement.entity.Transaction.JournalEntry;
import com.eduai.schoolmanagement.entity.Account;
import com.eduai.schoolmanagement.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    // Create new transaction
    public Transaction createTransaction(Transaction transaction) throws Exception {
        // Validate transaction
        validateTransaction(transaction);

        // Generate transaction number
        transaction.setTransactionNumber(generateTransactionNumber(transaction.getInstitutionId()));

        // Set default values
        transaction.setCreatedDate(LocalDateTime.now());
        transaction.setLastModifiedDate(LocalDateTime.now());

        if (transaction.getStatus() == null) {
            transaction.setStatus(TransactionStatus.DRAFT);
        }

        if (transaction.getApprovalStatus() == null) {
            transaction.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
        }

        // Calculate total amount from journal entries
        calculateTotalAmount(transaction);

        return transactionRepository.save(transaction);
    }

    // Create simple income transaction
    public Transaction createIncomeTransaction(String institutionId, String description,
                                             String incomeAccountId, String cashAccountId,
                                             BigDecimal amount, String createdBy) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setInstitutionId(institutionId);
        transaction.setDescription(description);
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setCategory(TransactionCategory.STUDENT_FEES);
        transaction.setCreatedBy(createdBy);
        transaction.setLastModifiedBy(createdBy);

        // Get account details
        Optional<Account> incomeAccount = accountService.getAccountById(incomeAccountId);
        Optional<Account> cashAccount = accountService.getAccountById(cashAccountId);

        if (!incomeAccount.isPresent() || !cashAccount.isPresent()) {
            throw new Exception("Invalid account IDs provided");
        }

        // Create journal entries (Dr. Cash, Cr. Income)
        List<JournalEntry> entries = new ArrayList<>();
        entries.add(new JournalEntry(cashAccountId, cashAccount.get().getAccountCode(),
                                   cashAccount.get().getAccountName(), amount, BigDecimal.ZERO, description));
        entries.add(new JournalEntry(incomeAccountId, incomeAccount.get().getAccountCode(),
                                   incomeAccount.get().getAccountName(), BigDecimal.ZERO, amount, description));

        transaction.setJournalEntries(entries);
        transaction.setTotalAmount(amount);

        return createTransaction(transaction);
    }

    // Create simple expense transaction
    public Transaction createExpenseTransaction(String institutionId, String description,
                                              String expenseAccountId, String cashAccountId,
                                              BigDecimal amount, String createdBy) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setInstitutionId(institutionId);
        transaction.setDescription(description);
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setCategory(TransactionCategory.UTILITIES);
        transaction.setCreatedBy(createdBy);
        transaction.setLastModifiedBy(createdBy);

        // Get account details
        Optional<Account> expenseAccount = accountService.getAccountById(expenseAccountId);
        Optional<Account> cashAccount = accountService.getAccountById(cashAccountId);

        if (!expenseAccount.isPresent() || !cashAccount.isPresent()) {
            throw new Exception("Invalid account IDs provided");
        }

        // Create journal entries (Dr. Expense, Cr. Cash)
        List<JournalEntry> entries = new ArrayList<>();
        entries.add(new JournalEntry(expenseAccountId, expenseAccount.get().getAccountCode(),
                                   expenseAccount.get().getAccountName(), amount, BigDecimal.ZERO, description));
        entries.add(new JournalEntry(cashAccountId, cashAccount.get().getAccountCode(),
                                   cashAccount.get().getAccountName(), BigDecimal.ZERO, amount, description));

        transaction.setJournalEntries(entries);
        transaction.setTotalAmount(amount);

        return createTransaction(transaction);
    }

    // Create transfer transaction
    public Transaction createTransferTransaction(String institutionId, String description,
                                               String fromAccountId, String toAccountId,
                                               BigDecimal amount, String createdBy) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setInstitutionId(institutionId);
        transaction.setDescription(description);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setCategory(TransactionCategory.ACCOUNT_TRANSFER);
        transaction.setCreatedBy(createdBy);
        transaction.setLastModifiedBy(createdBy);

        // Get account details
        Optional<Account> fromAccount = accountService.getAccountById(fromAccountId);
        Optional<Account> toAccount = accountService.getAccountById(toAccountId);

        if (!fromAccount.isPresent() || !toAccount.isPresent()) {
            throw new Exception("Invalid account IDs provided");
        }

        // Create journal entries (Dr. To Account, Cr. From Account)
        List<JournalEntry> entries = new ArrayList<>();
        entries.add(new JournalEntry(toAccountId, toAccount.get().getAccountCode(),
                                   toAccount.get().getAccountName(), amount, BigDecimal.ZERO, description));
        entries.add(new JournalEntry(fromAccountId, fromAccount.get().getAccountCode(),
                                   fromAccount.get().getAccountName(), BigDecimal.ZERO, amount, description));

        transaction.setJournalEntries(entries);
        transaction.setTotalAmount(amount);

        return createTransaction(transaction);
    }

    // Update transaction
    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) throws Exception {
        Optional<Transaction> existingTransactionOpt = transactionRepository.findById(transactionId);
        if (!existingTransactionOpt.isPresent()) {
            throw new Exception("Transaction not found with ID: " + transactionId);
        }

        Transaction existingTransaction = existingTransactionOpt.get();

        // Check if transaction can be modified
        if (existingTransaction.getStatus() == TransactionStatus.POSTED) {
            throw new Exception("Cannot modify posted transaction");
        }

        // Validate updated transaction
        validateTransaction(updatedTransaction);

        // Update fields
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setReference(updatedTransaction.getReference());
        existingTransaction.setTransactionType(updatedTransaction.getTransactionType());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setTransactionDate(updatedTransaction.getTransactionDate());
        existingTransaction.setJournalEntries(updatedTransaction.getJournalEntries());
        existingTransaction.setStudentId(updatedTransaction.getStudentId());
        existingTransaction.setEmployeeId(updatedTransaction.getEmployeeId());
        existingTransaction.setVendorId(updatedTransaction.getVendorId());
        existingTransaction.setInvoiceId(updatedTransaction.getInvoiceId());
        existingTransaction.setNotes(updatedTransaction.getNotes());
        existingTransaction.setPaymentMethod(updatedTransaction.getPaymentMethod());
        existingTransaction.setPaymentReference(updatedTransaction.getPaymentReference());
        existingTransaction.setCheckNumber(updatedTransaction.getCheckNumber());
        existingTransaction.updateLastModified(updatedTransaction.getLastModifiedBy());

        // Recalculate total amount
        calculateTotalAmount(existingTransaction);

        return transactionRepository.save(existingTransaction);
    }

    // Submit transaction for approval
    public Transaction submitForApproval(String transactionId, String submittedBy) throws Exception {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (!transactionOpt.isPresent()) {
            throw new Exception("Transaction not found with ID: " + transactionId);
        }

        Transaction transaction = transactionOpt.get();

        // Validate transaction can be submitted
        if (transaction.getStatus() != TransactionStatus.DRAFT) {
            throw new Exception("Only draft transactions can be submitted for approval");
        }

        if (!transaction.isBalanced()) {
            throw new Exception("Transaction is not balanced and cannot be submitted");
        }

        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setApprovalStatus(ApprovalStatus.PENDING);
        transaction.updateLastModified(submittedBy);

        return transactionRepository.save(transaction);
    }

    // Approve transaction
    public Transaction approveTransaction(String transactionId, String approvedBy, String comments) throws Exception {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (!transactionOpt.isPresent()) {
            throw new Exception("Transaction not found with ID: " + transactionId);
        }

        Transaction transaction = transactionOpt.get();

        if (!transaction.canBeApproved()) {
            throw new Exception("Transaction cannot be approved in its current state");
        }

        transaction.setStatus(TransactionStatus.APPROVED);
        transaction.setApprovalStatus(ApprovalStatus.APPROVED);
        transaction.setApprovedBy(approvedBy);
        transaction.setApprovedDate(LocalDateTime.now());
        transaction.setApprovalComments(comments);
        transaction.updateLastModified(approvedBy);

        return transactionRepository.save(transaction);
    }

    // Post transaction (update account balances)
    public Transaction postTransaction(String transactionId, String postedBy) throws Exception {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (!transactionOpt.isPresent()) {
            throw new Exception("Transaction not found with ID: " + transactionId);
        }

        Transaction transaction = transactionOpt.get();

        if (!transaction.canBePosted()) {
            throw new Exception("Transaction cannot be posted in its current state");
        }

        // Update account balances
        for (JournalEntry entry : transaction.getJournalEntries()) {
            accountService.updateAccountBalance(entry.getAccountId(),
                                              entry.getDebitAmount(),
                                              entry.getCreditAmount());
        }

        transaction.setStatus(TransactionStatus.POSTED);
        transaction.updateLastModified(postedBy);

        return transactionRepository.save(transaction);
    }

    // Reverse transaction
    public Transaction reverseTransaction(String transactionId, String reason, String reversedBy) throws Exception {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (!transactionOpt.isPresent()) {
            throw new Exception("Transaction not found with ID: " + transactionId);
        }

        Transaction originalTransaction = transactionOpt.get();

        if (originalTransaction.getStatus() != TransactionStatus.POSTED) {
            throw new Exception("Only posted transactions can be reversed");
        }

        // Create reversal transaction
        Transaction reversalTransaction = new Transaction();
        reversalTransaction.setInstitutionId(originalTransaction.getInstitutionId());
        reversalTransaction.setDescription("REVERSAL: " + originalTransaction.getDescription());
        reversalTransaction.setReference("REV-" + originalTransaction.getTransactionNumber());
        reversalTransaction.setTransactionType(originalTransaction.getTransactionType());
        reversalTransaction.setCategory(originalTransaction.getCategory());
        reversalTransaction.setTransactionDate(LocalDate.now());
        reversalTransaction.setCreatedBy(reversedBy);
        reversalTransaction.setLastModifiedBy(reversedBy);
        reversalTransaction.setNotes("Reversal of transaction " + originalTransaction.getTransactionNumber() + ". Reason: " + reason);

        // Reverse journal entries (flip debits and credits)
        List<JournalEntry> reversalEntries = new ArrayList<>();
        for (JournalEntry entry : originalTransaction.getJournalEntries()) {
            JournalEntry reversalEntry = new JournalEntry();
            reversalEntry.setAccountId(entry.getAccountId());
            reversalEntry.setAccountCode(entry.getAccountCode());
            reversalEntry.setAccountName(entry.getAccountName());
            reversalEntry.setDebitAmount(entry.getCreditAmount()); // Flip
            reversalEntry.setCreditAmount(entry.getDebitAmount()); // Flip
            reversalEntry.setDescription("Reversal: " + entry.getDescription());
            reversalEntries.add(reversalEntry);
        }

        reversalTransaction.setJournalEntries(reversalEntries);
        reversalTransaction.setTotalAmount(originalTransaction.getTotalAmount());
        reversalTransaction.setStatus(TransactionStatus.APPROVED);
        reversalTransaction.setApprovalStatus(ApprovalStatus.APPROVED);
        reversalTransaction.setApprovedBy(reversedBy);
        reversalTransaction.setApprovedDate(LocalDateTime.now());

        // Save reversal transaction
        Transaction savedReversalTransaction = createTransaction(reversalTransaction);

        // Post the reversal transaction
        postTransaction(savedReversalTransaction.getTransactionId(), reversedBy);

        // Mark original transaction as reversed
        originalTransaction.setStatus(TransactionStatus.REVERSED);
        originalTransaction.setNotes(originalTransaction.getNotes() + " | REVERSED on " + LocalDate.now() + " by " + reversedBy);
        originalTransaction.updateLastModified(reversedBy);
        transactionRepository.save(originalTransaction);

        return savedReversalTransaction;
    }

    // Get transaction by ID
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

    // Get transactions by institution
    public List<Transaction> getTransactionsByInstitution(String institutionId) {
        return transactionRepository.findByInstitutionId(institutionId);
    }

    public Page<Transaction> getTransactionsByInstitution(String institutionId, Pageable pageable) {
        return transactionRepository.findByInstitutionId(institutionId, pageable);
    }

    // Get transactions by status
    public List<Transaction> getTransactionsByStatus(String institutionId, TransactionStatus status) {
        return transactionRepository.findByInstitutionIdAndStatus(institutionId, status);
    }

    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(String institutionId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByInstitutionIdAndTransactionDateBetween(institutionId, startDate, endDate);
    }

    // Get transactions by account
    public List<Transaction> getTransactionsByAccount(String institutionId, String accountId) {
        return transactionRepository.findByInstitutionIdAndAccountId(institutionId, accountId);
    }

    // Get pending approval transactions
    public List<Transaction> getPendingApprovalTransactions(String institutionId) {
        return transactionRepository.findPendingApprovalTransactions(institutionId);
    }

    // Get unbalanced transactions
    public List<Transaction> getUnbalancedTransactions(String institutionId) {
        return transactionRepository.findUnbalancedTransactions(institutionId);
    }

    // Search transactions
    public List<Transaction> searchTransactions(String institutionId, String searchTerm) {
        return transactionRepository.searchByDescriptionOrReference(institutionId, searchTerm);
    }

    // Get transaction statistics
    public Map<String, Object> getTransactionStatistics(String institutionId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();

        // Count by status
        for (TransactionStatus status : TransactionStatus.values()) {
            long count = transactionRepository.findByInstitutionIdAndTransactionDateBetween(
                institutionId, startDate, endDate).stream()
                .filter(t -> t.getStatus() == status)
                .count();
            stats.put(status.name().toLowerCase() + "_count", count);
        }

        // Count by type
        for (TransactionType type : TransactionType.values()) {
            long count = transactionRepository.findByInstitutionIdAndTransactionDateBetween(
                institutionId, startDate, endDate).stream()
                .filter(t -> t.getTransactionType() == type)
                .count();
            stats.put(type.name().toLowerCase() + "_count", count);
        }

        // Total transaction amount
        BigDecimal totalAmount = transactionRepository.findByInstitutionIdAndTransactionDateBetween(
            institutionId, startDate, endDate).stream()
            .filter(t -> t.getStatus() == TransactionStatus.POSTED)
            .map(Transaction::getTotalAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("total_transaction_amount", totalAmount);

        // Pending approvals
        long pendingApprovals = transactionRepository.findPendingApprovalTransactions(institutionId).size();
        stats.put("pending_approvals", pendingApprovals);

        return stats;
    }

    // Delete transaction (only if not posted)
    public void deleteTransaction(String transactionId) throws Exception {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (!transactionOpt.isPresent()) {
            throw new Exception("Transaction not found with ID: " + transactionId);
        }

        Transaction transaction = transactionOpt.get();

        if (transaction.getStatus() == TransactionStatus.POSTED) {
            throw new Exception("Cannot delete posted transaction. Use reverse transaction instead.");
        }

        transactionRepository.delete(transaction);
    }

    // Private helper methods
    private void validateTransaction(Transaction transaction) throws Exception {
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new Exception("Transaction description is required");
        }

        if (transaction.getTransactionType() == null) {
            throw new Exception("Transaction type is required");
        }

        if (transaction.getInstitutionId() == null || transaction.getInstitutionId().trim().isEmpty()) {
            throw new Exception("Institution ID is required");
        }

        if (transaction.getJournalEntries() == null || transaction.getJournalEntries().isEmpty()) {
            throw new Exception("Transaction must have at least one journal entry");
        }

        if (transaction.getJournalEntries().size() < 2) {
            throw new Exception("Transaction must have at least two journal entries for double-entry bookkeeping");
        }

        // Validate journal entries
        for (JournalEntry entry : transaction.getJournalEntries()) {
            if (entry.getAccountId() == null || entry.getAccountId().trim().isEmpty()) {
                throw new Exception("Journal entry must have a valid account ID");
            }

            if ((entry.getDebitAmount() == null || entry.getDebitAmount().compareTo(BigDecimal.ZERO) == 0) &&
                (entry.getCreditAmount() == null || entry.getCreditAmount().compareTo(BigDecimal.ZERO) == 0)) {
                throw new Exception("Journal entry must have either a debit or credit amount");
            }

            if (entry.getDebitAmount() != null && entry.getDebitAmount().compareTo(BigDecimal.ZERO) > 0 &&
                entry.getCreditAmount() != null && entry.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
                throw new Exception("Journal entry cannot have both debit and credit amounts");
            }
        }

        // Validate that transaction is balanced
        if (!isTransactionBalanced(transaction)) {
            throw new Exception("Transaction debits must equal credits");
        }
    }

    private boolean isTransactionBalanced(Transaction transaction) {
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (JournalEntry entry : transaction.getJournalEntries()) {
            if (entry.getDebitAmount() != null) {
                totalDebits = totalDebits.add(entry.getDebitAmount());
            }
            if (entry.getCreditAmount() != null) {
                totalCredits = totalCredits.add(entry.getCreditAmount());
            }
        }

        return totalDebits.compareTo(totalCredits) == 0;
    }

    private void calculateTotalAmount(Transaction transaction) {
        BigDecimal totalDebits = BigDecimal.ZERO;

        for (JournalEntry entry : transaction.getJournalEntries()) {
            if (entry.getDebitAmount() != null) {
                totalDebits = totalDebits.add(entry.getDebitAmount());
            }
        }

        transaction.setTotalAmount(totalDebits);
    }

    private String generateTransactionNumber(String institutionId) {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = transactionRepository.countByInstitutionIdAndCreatedDateBetween(
            institutionId,
            LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
            LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusSeconds(1)
        );

        return String.format("TXN-%s-%s-%06d", institutionId.substring(0, Math.min(3, institutionId.length())).toUpperCase(),
                           datePrefix, count + 1);
    }
}
