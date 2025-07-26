package com.eduai.schoolmanagement.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "accounts")
public class Account {

    @Id
    private String accountId;

    @Indexed(unique = true)
    private String accountCode;

    private String accountName;
    private String description;
    private AccountType accountType;
    private AccountCategory category;
    private AccountSubCategory subCategory;

    // Hierarchical structure
    private String parentAccountId;
    private List<String> childAccountIds;
    private Integer level; // 0 = main category, 1 = subcategory, etc.

    // Financial data
    private BigDecimal balance;
    private BigDecimal debitBalance;
    private BigDecimal creditBalance;
    private String currency;
    private Boolean isActive;

    // Bank account specific
    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private String iban;
    private String swiftCode;

    // Metadata
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String institutionId;

    // Tax and compliance
    private String taxCode;
    private Boolean isTaxable;
    private String complianceCategory;

    // Budget and limits
    private BigDecimal budgetLimit;
    private BigDecimal warningThreshold;
    private BudgetPeriod budgetPeriod;

    public enum AccountType {
        ASSET("Asset"),
        LIABILITY("Liability"),
        EQUITY("Equity"),
        INCOME("Income"),
        EXPENSE("Expense");

        private final String displayName;

        AccountType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AccountCategory {
        // Assets
        CURRENT_ASSETS("Current Assets"),
        FIXED_ASSETS("Fixed Assets"),
        INTANGIBLE_ASSETS("Intangible Assets"),

        // Liabilities
        CURRENT_LIABILITIES("Current Liabilities"),
        LONG_TERM_LIABILITIES("Long Term Liabilities"),

        // Equity
        OWNERS_EQUITY("Owner's Equity"),
        RETAINED_EARNINGS("Retained Earnings"),

        // Income
        OPERATING_INCOME("Operating Income"),
        NON_OPERATING_INCOME("Non-Operating Income"),

        // Expenses
        OPERATING_EXPENSES("Operating Expenses"),
        ADMINISTRATIVE_EXPENSES("Administrative Expenses"),
        FINANCIAL_EXPENSES("Financial Expenses");

        private final String displayName;

        AccountCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AccountSubCategory {
        // Current Assets
        CASH_AND_CASH_EQUIVALENTS("Cash and Cash Equivalents"),
        ACCOUNTS_RECEIVABLE("Accounts Receivable"),
        STUDENT_FEES_RECEIVABLE("Student Fees Receivable"),
        INVENTORY("Inventory"),
        PREPAID_EXPENSES("Prepaid Expenses"),

        // Fixed Assets
        BUILDINGS("Buildings"),
        EQUIPMENT("Equipment"),
        FURNITURE("Furniture"),
        VEHICLES("Vehicles"),
        ACCUMULATED_DEPRECIATION("Accumulated Depreciation"),

        // Current Liabilities
        ACCOUNTS_PAYABLE("Accounts Payable"),
        SALARIES_PAYABLE("Salaries Payable"),
        TAXES_PAYABLE("Taxes Payable"),
        STUDENT_DEPOSITS("Student Deposits"),

        // Income
        TUITION_FEES("Tuition Fees"),
        REGISTRATION_FEES("Registration Fees"),
        EXAMINATION_FEES("Examination Fees"),
        LIBRARY_FEES("Library Fees"),
        TRANSPORTATION_FEES("Transportation Fees"),
        DONATIONS("Donations"),
        GRANTS("Grants"),

        // Expenses
        TEACHING_SALARIES("Teaching Salaries"),
        ADMINISTRATIVE_SALARIES("Administrative Salaries"),
        UTILITIES("Utilities"),
        MAINTENANCE("Maintenance"),
        SUPPLIES("Supplies"),
        INSURANCE("Insurance"),
        MARKETING("Marketing"),
        DEPRECIATION("Depreciation");

        private final String displayName;

        AccountSubCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum BudgetPeriod {
        MONTHLY("Monthly"),
        QUARTERLY("Quarterly"),
        ANNUALLY("Annually");

        private final String displayName;

        BudgetPeriod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Account() {
        this.childAccountIds = new ArrayList<>();
        this.balance = BigDecimal.ZERO;
        this.debitBalance = BigDecimal.ZERO;
        this.creditBalance = BigDecimal.ZERO;
        this.currency = "USD";
        this.isActive = true;
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.isTaxable = false;
        this.level = 0;
    }

    public Account(String accountCode, String accountName, AccountType accountType,
                   AccountCategory category, String institutionId) {
        this();
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.category = category;
        this.institutionId = institutionId;
    }

    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public AccountCategory getCategory() { return category; }
    public void setCategory(AccountCategory category) { this.category = category; }

    public AccountSubCategory getSubCategory() { return subCategory; }
    public void setSubCategory(AccountSubCategory subCategory) { this.subCategory = subCategory; }

    public String getParentAccountId() { return parentAccountId; }
    public void setParentAccountId(String parentAccountId) { this.parentAccountId = parentAccountId; }

    public List<String> getChildAccountIds() { return childAccountIds; }
    public void setChildAccountIds(List<String> childAccountIds) { this.childAccountIds = childAccountIds; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getDebitBalance() { return debitBalance; }
    public void setDebitBalance(BigDecimal debitBalance) { this.debitBalance = debitBalance; }

    public BigDecimal getCreditBalance() { return creditBalance; }
    public void setCreditBalance(BigDecimal creditBalance) { this.creditBalance = creditBalance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getInstitutionId() { return institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public Boolean getIsTaxable() { return isTaxable; }
    public void setIsTaxable(Boolean isTaxable) { this.isTaxable = isTaxable; }

    public String getComplianceCategory() { return complianceCategory; }
    public void setComplianceCategory(String complianceCategory) { this.complianceCategory = complianceCategory; }

    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }

    public BigDecimal getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(BigDecimal warningThreshold) { this.warningThreshold = warningThreshold; }

    public BudgetPeriod getBudgetPeriod() { return budgetPeriod; }
    public void setBudgetPeriod(BudgetPeriod budgetPeriod) { this.budgetPeriod = budgetPeriod; }

    // Helper methods
    public void addChildAccount(String childAccountId) {
        if (this.childAccountIds == null) {
            this.childAccountIds = new ArrayList<>();
        }
        if (!this.childAccountIds.contains(childAccountId)) {
            this.childAccountIds.add(childAccountId);
        }
    }

    public void removeChildAccount(String childAccountId) {
        if (this.childAccountIds != null) {
            this.childAccountIds.remove(childAccountId);
        }
    }

    public boolean isParentAccount() {
        return this.childAccountIds != null && !this.childAccountIds.isEmpty();
    }

    public boolean isChildAccount() {
        return this.parentAccountId != null && !this.parentAccountId.isEmpty();
    }

    public BigDecimal getNetBalance() {
        // For asset and expense accounts, debit increases balance
        // For liability, equity, and income accounts, credit increases balance
        if (accountType == AccountType.ASSET || accountType == AccountType.EXPENSE) {
            return debitBalance.subtract(creditBalance);
        } else {
            return creditBalance.subtract(debitBalance);
        }
    }

    public void updateLastModified(String modifiedBy) {
        this.lastModifiedDate = LocalDateTime.now();
        this.lastModifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountType=" + accountType +
                ", category=" + category +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
