package com.eduai.schoolmanagement.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "transactions")
public class Transaction {

    @Id
    private String transactionId;

    @Indexed
    private String transactionNumber;

    private String description;
    private String reference;
    private TransactionType transactionType;
    private TransactionCategory category;
    private TransactionStatus status;

    // Double-entry bookkeeping
    private List<JournalEntry> journalEntries;
    private BigDecimal totalAmount;
    private String currency;

    // Dates
    private LocalDate transactionDate;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    // Related entities
    private String studentId; // If related to student fees
    private String employeeId; // If related to salary/employee expenses
    private String vendorId; // If related to vendor payments
    private String invoiceId; // If related to an invoice
    private String receiptId; // If related to a receipt

    // Approval workflow
    private ApprovalStatus approvalStatus;
    private String approvedBy;
    private LocalDateTime approvedDate;
    private String approvalComments;

    // Metadata
    private String createdBy;
    private String lastModifiedBy;
    private String institutionId;
    private String academicYear;

    // Reconciliation
    private Boolean isReconciled;
    private String bankStatementId;
    private LocalDate reconciledDate;
    private String reconciledBy;

    // Attachments and notes
    private List<String> attachmentUrls;
    private String notes;
    private String internalNotes;

    // Payment details
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private String checkNumber;
    private String bankTransactionId;

    public enum TransactionType {
        INCOME("Income"),
        EXPENSE("Expense"),
        TRANSFER("Transfer"),
        ADJUSTMENT("Adjustment"),
        OPENING_BALANCE("Opening Balance"),
        CLOSING_BALANCE("Closing Balance");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TransactionCategory {
        // Income categories
        STUDENT_FEES("Student Fees"),
        REGISTRATION_FEES("Registration Fees"),
        EXAMINATION_FEES("Examination Fees"),
        LIBRARY_FEES("Library Fees"),
        TRANSPORTATION_FEES("Transportation Fees"),
        LATE_FEES("Late Fees"),
        DONATIONS("Donations"),
        GRANTS("Grants"),
        INVESTMENT_INCOME("Investment Income"),
        OTHER_INCOME("Other Income"),

        // Expense categories
        SALARIES_AND_WAGES("Salaries and Wages"),
        BENEFITS("Benefits"),
        UTILITIES("Utilities"),
        RENT("Rent"),
        MAINTENANCE("Maintenance"),
        SUPPLIES("Supplies"),
        EQUIPMENT("Equipment"),
        INSURANCE("Insurance"),
        MARKETING("Marketing"),
        PROFESSIONAL_SERVICES("Professional Services"),
        TRAVEL("Travel"),
        TRAINING("Training"),
        DEPRECIATION("Depreciation"),
        TAXES("Taxes"),
        INTEREST("Interest"),
        OTHER_EXPENSES("Other Expenses"),

        // Transfer categories
        BANK_TRANSFER("Bank Transfer"),
        CASH_TRANSFER("Cash Transfer"),
        ACCOUNT_TRANSFER("Account Transfer");

        private final String displayName;

        TransactionCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TransactionStatus {
        DRAFT("Draft"),
        PENDING("Pending"),
        APPROVED("Approved"),
        POSTED("Posted"),
        CANCELLED("Cancelled"),
        REVERSED("Reversed");

        private final String displayName;

        TransactionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ApprovalStatus {
        NOT_REQUIRED("Not Required"),
        PENDING("Pending Approval"),
        APPROVED("Approved"),
        REJECTED("Rejected");

        private final String displayName;

        ApprovalStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentMethod {
        CASH("Cash"),
        CHECK("Check"),
        BANK_TRANSFER("Bank Transfer"),
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        ONLINE_PAYMENT("Online Payment"),
        MOBILE_PAYMENT("Mobile Payment"),
        CRYPTOCURRENCY("Cryptocurrency"),
        OTHER("Other");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Inner class for journal entries (double-entry bookkeeping)
    public static class JournalEntry {
        private String accountId;
        private String accountCode;
        private String accountName;
        private BigDecimal debitAmount;
        private BigDecimal creditAmount;
        private String description;

        // Constructors
        public JournalEntry() {}

        public JournalEntry(String accountId, String accountCode, String accountName,
                           BigDecimal debitAmount, BigDecimal creditAmount, String description) {
            this.accountId = accountId;
            this.accountCode = accountCode;
            this.accountName = accountName;
            this.debitAmount = debitAmount != null ? debitAmount : BigDecimal.ZERO;
            this.creditAmount = creditAmount != null ? creditAmount : BigDecimal.ZERO;
            this.description = description;
        }

        // Getters and Setters
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }

        public String getAccountCode() { return accountCode; }
        public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }

        public BigDecimal getDebitAmount() { return debitAmount; }
        public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount; }

        public BigDecimal getCreditAmount() { return creditAmount; }
        public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getAmount() {
            if (debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) {
                return debitAmount;
            } else if (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
                return creditAmount;
            }
            return BigDecimal.ZERO;
        }

        public boolean isDebit() {
            return debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0;
        }

        public boolean isCredit() {
            return creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0;
        }
    }

    // Constructors
    public Transaction() {
        this.journalEntries = new ArrayList<>();
        this.attachmentUrls = new ArrayList<>();
        this.currency = "USD";
        this.status = TransactionStatus.DRAFT;
        this.approvalStatus = ApprovalStatus.NOT_REQUIRED;
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.transactionDate = LocalDate.now();
        this.isReconciled = false;
    }

    public Transaction(String description, TransactionType transactionType,
                      TransactionCategory category, String institutionId) {
        this();
        this.description = description;
        this.transactionType = transactionType;
        this.category = category;
        this.institutionId = institutionId;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getTransactionNumber() { return transactionNumber; }
    public void setTransactionNumber(String transactionNumber) { this.transactionNumber = transactionNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public TransactionCategory getCategory() { return category; }
    public void setCategory(TransactionCategory category) { this.category = category; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public List<JournalEntry> getJournalEntries() { return journalEntries; }
    public void setJournalEntries(List<JournalEntry> journalEntries) { this.journalEntries = journalEntries; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getReceiptId() { return receiptId; }
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime approvedDate) { this.approvedDate = approvedDate; }

    public String getApprovalComments() { return approvalComments; }
    public void setApprovalComments(String approvalComments) { this.approvalComments = approvalComments; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getInstitutionId() { return institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Boolean getIsReconciled() { return isReconciled; }
    public void setIsReconciled(Boolean isReconciled) { this.isReconciled = isReconciled; }

    public String getBankStatementId() { return bankStatementId; }
    public void setBankStatementId(String bankStatementId) { this.bankStatementId = bankStatementId; }

    public LocalDate getReconciledDate() { return reconciledDate; }
    public void setReconciledDate(LocalDate reconciledDate) { this.reconciledDate = reconciledDate; }

    public String getReconciledBy() { return reconciledBy; }
    public void setReconciledBy(String reconciledBy) { this.reconciledBy = reconciledBy; }

    public List<String> getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(List<String> attachmentUrls) { this.attachmentUrls = attachmentUrls; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public String getCheckNumber() { return checkNumber; }
    public void setCheckNumber(String checkNumber) { this.checkNumber = checkNumber; }

    public String getBankTransactionId() { return bankTransactionId; }
    public void setBankTransactionId(String bankTransactionId) { this.bankTransactionId = bankTransactionId; }

    // Helper methods
    public void addJournalEntry(JournalEntry entry) {
        if (this.journalEntries == null) {
            this.journalEntries = new ArrayList<>();
        }
        this.journalEntries.add(entry);
    }

    public void addAttachment(String attachmentUrl) {
        if (this.attachmentUrls == null) {
            this.attachmentUrls = new ArrayList<>();
        }
        this.attachmentUrls.add(attachmentUrl);
    }

    public BigDecimal getTotalDebits() {
        return journalEntries.stream()
                .map(JournalEntry::getDebitAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCredits() {
        return journalEntries.stream()
                .map(JournalEntry::getCreditAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isBalanced() {
        return getTotalDebits().compareTo(getTotalCredits()) == 0;
    }

    public void updateLastModified(String modifiedBy) {
        this.lastModifiedDate = LocalDateTime.now();
        this.lastModifiedBy = modifiedBy;
    }

    public boolean canBeApproved() {
        return status == TransactionStatus.PENDING &&
               approvalStatus == ApprovalStatus.PENDING &&
               isBalanced();
    }

    public boolean canBePosted() {
        return status == TransactionStatus.APPROVED &&
               approvalStatus == ApprovalStatus.APPROVED &&
               isBalanced();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", description='" + description + '\'' +
                ", transactionType=" + transactionType +
                ", totalAmount=" + totalAmount +
                ", currency='" + currency + '\'' +
                ", transactionDate=" + transactionDate +
                ", status=" + status +
                '}';
    }
}
