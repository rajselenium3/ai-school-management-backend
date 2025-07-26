package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Account;
import com.eduai.schoolmanagement.entity.Account.AccountType;
import com.eduai.schoolmanagement.entity.Account.AccountCategory;
import com.eduai.schoolmanagement.entity.Account.AccountSubCategory;
import com.eduai.schoolmanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // Create new account
    public Account createAccount(Account account) throws Exception {
        // Validate account data
        validateAccount(account);

        // Check for duplicate account code
        if (accountRepository.existsByAccountCodeAndInstitutionId(account.getAccountCode(), account.getInstitutionId())) {
            throw new Exception("Account code already exists: " + account.getAccountCode());
        }

        // Set default values
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getDebitBalance() == null) {
            account.setDebitBalance(BigDecimal.ZERO);
        }
        if (account.getCreditBalance() == null) {
            account.setCreditBalance(BigDecimal.ZERO);
        }

        account.setCreatedDate(LocalDateTime.now());
        account.setLastModifiedDate(LocalDateTime.now());

        // Set level based on parent
        if (account.getParentAccountId() != null) {
            Optional<Account> parent = accountRepository.findById(account.getParentAccountId());
            if (parent.isPresent()) {
                account.setLevel(parent.get().getLevel() + 1);
                // Add this account as child to parent
                parent.get().addChildAccount(account.getAccountId());
                accountRepository.save(parent.get());
            }
        } else {
            account.setLevel(0); // Top-level account
        }

        return accountRepository.save(account);
    }

    // Update existing account
    public Account updateAccount(String accountId, Account updatedAccount) throws Exception {
        Optional<Account> existingAccountOpt = accountRepository.findById(accountId);
        if (!existingAccountOpt.isPresent()) {
            throw new Exception("Account not found with ID: " + accountId);
        }

        Account existingAccount = existingAccountOpt.get();

        // Validate updated account data
        validateAccount(updatedAccount);

        // Check for duplicate account code (excluding current account)
        Optional<Account> duplicateAccount = accountRepository.findByAccountCodeAndInstitutionId(
            updatedAccount.getAccountCode(), updatedAccount.getInstitutionId());
        if (duplicateAccount.isPresent() && !duplicateAccount.get().getAccountId().equals(accountId)) {
            throw new Exception("Account code already exists: " + updatedAccount.getAccountCode());
        }

        // Update fields
        existingAccount.setAccountCode(updatedAccount.getAccountCode());
        existingAccount.setAccountName(updatedAccount.getAccountName());
        existingAccount.setDescription(updatedAccount.getDescription());
        existingAccount.setAccountType(updatedAccount.getAccountType());
        existingAccount.setCategory(updatedAccount.getCategory());
        existingAccount.setSubCategory(updatedAccount.getSubCategory());
        existingAccount.setCurrency(updatedAccount.getCurrency());
        existingAccount.setIsActive(updatedAccount.getIsActive());
        existingAccount.setBankName(updatedAccount.getBankName());
        existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        existingAccount.setRoutingNumber(updatedAccount.getRoutingNumber());
        existingAccount.setIban(updatedAccount.getIban());
        existingAccount.setSwiftCode(updatedAccount.getSwiftCode());
        existingAccount.setTaxCode(updatedAccount.getTaxCode());
        existingAccount.setIsTaxable(updatedAccount.getIsTaxable());
        existingAccount.setComplianceCategory(updatedAccount.getComplianceCategory());
        existingAccount.setBudgetLimit(updatedAccount.getBudgetLimit());
        existingAccount.setWarningThreshold(updatedAccount.getWarningThreshold());
        existingAccount.setBudgetPeriod(updatedAccount.getBudgetPeriod());
        existingAccount.updateLastModified(updatedAccount.getLastModifiedBy());

        return accountRepository.save(existingAccount);
    }

    // Get account by ID
    public Optional<Account> getAccountById(String accountId) {
        return accountRepository.findById(accountId);
    }

    // Get account by code
    public Optional<Account> getAccountByCode(String accountCode, String institutionId) {
        return accountRepository.findByAccountCodeAndInstitutionId(accountCode, institutionId);
    }

    // Get all accounts for institution
    public List<Account> getAllAccounts(String institutionId) {
        return accountRepository.findByInstitutionId(institutionId);
    }

    // Get active accounts for institution
    public List<Account> getActiveAccounts(String institutionId) {
        return accountRepository.findByInstitutionIdAndIsActive(institutionId, true);
    }

    // Get accounts by type
    public List<Account> getAccountsByType(String institutionId, AccountType accountType) {
        return accountRepository.findByInstitutionIdAndAccountTypeAndIsActive(institutionId, accountType, true);
    }

    // Get accounts by category
    public List<Account> getAccountsByCategory(String institutionId, AccountCategory category) {
        return accountRepository.findByInstitutionIdAndCategoryAndIsActive(institutionId, category, true);
    }

    // Get chart of accounts (hierarchical structure)
    public List<Account> getChartOfAccounts(String institutionId) {
        List<Account> allAccounts = accountRepository.findActiveAccountsForChartOfAccounts(institutionId);
        return buildAccountHierarchy(allAccounts);
    }

    // Get account hierarchy for a specific account
    public List<Account> getAccountHierarchy(String accountId) {
        List<Account> hierarchy = new ArrayList<>();
        Optional<Account> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            hierarchy.add(account);

            // Get children recursively
            if (account.getChildAccountIds() != null) {
                for (String childId : account.getChildAccountIds()) {
                    hierarchy.addAll(getAccountHierarchy(childId));
                }
            }
        }

        return hierarchy;
    }

    // Search accounts
    public List<Account> searchAccounts(String institutionId, String searchTerm) {
        return accountRepository.searchByNameOrCode(institutionId, searchTerm);
    }

    // Delete account
    public void deleteAccount(String accountId) throws Exception {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new Exception("Account not found with ID: " + accountId);
        }

        Account account = accountOpt.get();

        // Check if account has children
        if (account.isParentAccount()) {
            throw new Exception("Cannot delete account with child accounts. Please delete or reassign child accounts first.");
        }

        // Check if account has transactions (would need to check with TransactionService)
        // For now, we'll just mark as inactive instead of hard delete
        account.setIsActive(false);
        account.updateLastModified("system");
        accountRepository.save(account);

        // Remove from parent's children list if it has a parent
        if (account.getParentAccountId() != null) {
            Optional<Account> parentOpt = accountRepository.findById(account.getParentAccountId());
            if (parentOpt.isPresent()) {
                Account parent = parentOpt.get();
                parent.removeChildAccount(accountId);
                accountRepository.save(parent);
            }
        }
    }

    // Update account balance
    public void updateAccountBalance(String accountId, BigDecimal debitAmount, BigDecimal creditAmount) throws Exception {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent()) {
            throw new Exception("Account not found with ID: " + accountId);
        }

        Account account = accountOpt.get();

        // Update balances
        if (debitAmount != null) {
            account.setDebitBalance(account.getDebitBalance().add(debitAmount));
        }
        if (creditAmount != null) {
            account.setCreditBalance(account.getCreditBalance().add(creditAmount));
        }

        // Calculate net balance
        account.setBalance(account.getNetBalance());
        account.updateLastModified("system");

        accountRepository.save(account);
    }

    // Get cash and bank accounts
    public List<Account> getCashAndBankAccounts(String institutionId) {
        return accountRepository.findCashAndBankAccounts(institutionId);
    }

    // Get receivable accounts
    public List<Account> getReceivableAccounts(String institutionId) {
        return accountRepository.findReceivableAccounts(institutionId);
    }

    // Get payable accounts
    public List<Account> getPayableAccounts(String institutionId) {
        return accountRepository.findPayableAccounts(institutionId);
    }

    // Get income accounts
    public List<Account> getIncomeAccounts(String institutionId) {
        return accountRepository.findIncomeAccounts(institutionId);
    }

    // Get expense accounts
    public List<Account> getExpenseAccounts(String institutionId) {
        return accountRepository.findExpenseAccounts(institutionId);
    }

    // Get accounts approaching budget limit
    public List<Account> getAccountsApproachingBudgetLimit(String institutionId) {
        return accountRepository.findAccountsApproachingBudgetLimit(institutionId);
    }

    // Get accounts exceeding budget limit
    public List<Account> getAccountsExceedingBudgetLimit(String institutionId) {
        return accountRepository.findAccountsExceedingBudgetLimit(institutionId);
    }

    // Create default chart of accounts for new institution
    public void createDefaultChartOfAccounts(String institutionId, String createdBy) {
        List<Account> defaultAccounts = getDefaultAccountStructure(institutionId, createdBy);

        // Save parent accounts first
        Map<String, Account> accountMap = new HashMap<>();
        for (Account account : defaultAccounts) {
            if (account.getLevel() == 0) {
                Account savedAccount = accountRepository.save(account);
                accountMap.put(account.getAccountCode(), savedAccount);
            }
        }

        // Save child accounts and link to parents
        for (Account account : defaultAccounts) {
            if (account.getLevel() > 0 && account.getParentAccountId() != null) {
                // Find parent by code
                for (Account parent : accountMap.values()) {
                    if (parent.getAccountCode().equals(account.getParentAccountId())) {
                        account.setParentAccountId(parent.getAccountId());
                        Account savedAccount = accountRepository.save(account);
                        parent.addChildAccount(savedAccount.getAccountId());
                        accountRepository.save(parent);
                        accountMap.put(account.getAccountCode(), savedAccount);
                        break;
                    }
                }
            }
        }
    }

    // Get account statistics
    public Map<String, Object> getAccountStatistics(String institutionId) {
        Map<String, Object> stats = new HashMap<>();

        // Count by type
        for (AccountType type : AccountType.values()) {
            long count = accountRepository.countByInstitutionIdAndAccountType(institutionId, type);
            stats.put(type.name().toLowerCase() + "_count", count);
        }

        // Total active accounts
        long totalActive = accountRepository.countActiveAccountsByInstitutionId(institutionId);
        stats.put("total_active_accounts", totalActive);

        // Accounts with budget limits
        List<Account> budgetAccounts = getAccountsApproachingBudgetLimit(institutionId);
        stats.put("accounts_approaching_budget", budgetAccounts.size());

        List<Account> exceededAccounts = getAccountsExceedingBudgetLimit(institutionId);
        stats.put("accounts_exceeding_budget", exceededAccounts.size());

        return stats;
    }

    // Private helper methods
    private void validateAccount(Account account) throws Exception {
        if (account.getAccountCode() == null || account.getAccountCode().trim().isEmpty()) {
            throw new Exception("Account code is required");
        }

        if (account.getAccountName() == null || account.getAccountName().trim().isEmpty()) {
            throw new Exception("Account name is required");
        }

        if (account.getAccountType() == null) {
            throw new Exception("Account type is required");
        }

        if (account.getCategory() == null) {
            throw new Exception("Account category is required");
        }

        if (account.getInstitutionId() == null || account.getInstitutionId().trim().isEmpty()) {
            throw new Exception("Institution ID is required");
        }

        // Validate account code format (should be alphanumeric)
        if (!account.getAccountCode().matches("^[A-Za-z0-9-_]+$")) {
            throw new Exception("Account code can only contain letters, numbers, hyphens, and underscores");
        }
    }

    private List<Account> buildAccountHierarchy(List<Account> accounts) {
        Map<String, Account> accountMap = accounts.stream()
            .collect(Collectors.toMap(Account::getAccountId, account -> account));

        // Sort by level first, then by account code
        return accounts.stream()
            .sorted(Comparator.comparing(Account::getLevel)
                             .thenComparing(Account::getAccountCode))
            .collect(Collectors.toList());
    }

    private List<Account> getDefaultAccountStructure(String institutionId, String createdBy) {
        List<Account> accounts = new ArrayList<>();

        // ASSETS
        accounts.add(createDefaultAccount("1000", "ASSETS", "Assets", AccountType.ASSET,
                                        AccountCategory.CURRENT_ASSETS, null, institutionId, createdBy, 0));

        // Current Assets
        accounts.add(createDefaultAccount("1100", "CURRENT_ASSETS", "Current Assets", AccountType.ASSET,
                                        AccountCategory.CURRENT_ASSETS, "1000", institutionId, createdBy, 1));
        accounts.add(createDefaultAccount("1110", "CASH", "Cash", AccountType.ASSET,
                                        AccountCategory.CURRENT_ASSETS, "1100", institutionId, createdBy, 2));
        accounts.add(createDefaultAccount("1120", "BANK_CHECKING", "Bank - Checking", AccountType.ASSET,
                                        AccountCategory.CURRENT_ASSETS, "1100", institutionId, createdBy, 2));
        accounts.add(createDefaultAccount("1130", "ACCOUNTS_RECEIVABLE", "Accounts Receivable", AccountType.ASSET,
                                        AccountCategory.CURRENT_ASSETS, "1100", institutionId, createdBy, 2));
        accounts.add(createDefaultAccount("1140", "STUDENT_FEES_RECEIVABLE", "Student Fees Receivable", AccountType.ASSET,
                                        AccountCategory.CURRENT_ASSETS, "1100", institutionId, createdBy, 2));

        // Fixed Assets
        accounts.add(createDefaultAccount("1200", "FIXED_ASSETS", "Fixed Assets", AccountType.ASSET,
                                        AccountCategory.FIXED_ASSETS, "1000", institutionId, createdBy, 1));
        accounts.add(createDefaultAccount("1210", "BUILDINGS", "Buildings", AccountType.ASSET,
                                        AccountCategory.FIXED_ASSETS, "1200", institutionId, createdBy, 2));
        accounts.add(createDefaultAccount("1220", "EQUIPMENT", "Equipment", AccountType.ASSET,
                                        AccountCategory.FIXED_ASSETS, "1200", institutionId, createdBy, 2));

        // LIABILITIES
        accounts.add(createDefaultAccount("2000", "LIABILITIES", "Liabilities", AccountType.LIABILITY,
                                        AccountCategory.CURRENT_LIABILITIES, null, institutionId, createdBy, 0));

        // Current Liabilities
        accounts.add(createDefaultAccount("2100", "CURRENT_LIABILITIES", "Current Liabilities", AccountType.LIABILITY,
                                        AccountCategory.CURRENT_LIABILITIES, "2000", institutionId, createdBy, 1));
        accounts.add(createDefaultAccount("2110", "ACCOUNTS_PAYABLE", "Accounts Payable", AccountType.LIABILITY,
                                        AccountCategory.CURRENT_LIABILITIES, "2100", institutionId, createdBy, 2));
        accounts.add(createDefaultAccount("2120", "SALARIES_PAYABLE", "Salaries Payable", AccountType.LIABILITY,
                                        AccountCategory.CURRENT_LIABILITIES, "2100", institutionId, createdBy, 2));

        // EQUITY
        accounts.add(createDefaultAccount("3000", "EQUITY", "Equity", AccountType.EQUITY,
                                        AccountCategory.OWNERS_EQUITY, null, institutionId, createdBy, 0));
        accounts.add(createDefaultAccount("3100", "RETAINED_EARNINGS", "Retained Earnings", AccountType.EQUITY,
                                        AccountCategory.RETAINED_EARNINGS, "3000", institutionId, createdBy, 1));

        // INCOME
        accounts.add(createDefaultAccount("4000", "INCOME", "Income", AccountType.INCOME,
                                        AccountCategory.OPERATING_INCOME, null, institutionId, createdBy, 0));

        // Operating Income
        accounts.add(createDefaultAccount("4100", "TUITION_INCOME", "Tuition Income", AccountType.INCOME,
                                        AccountCategory.OPERATING_INCOME, "4000", institutionId, createdBy, 1));
        accounts.add(createDefaultAccount("4110", "REGISTRATION_FEES", "Registration Fees", AccountType.INCOME,
                                        AccountCategory.OPERATING_INCOME, "4100", institutionId, createdBy, 2));
        accounts.add(createDefaultAccount("4120", "EXAMINATION_FEES", "Examination Fees", AccountType.INCOME,
                                        AccountCategory.OPERATING_INCOME, "4100", institutionId, createdBy, 2));

        // EXPENSES
        accounts.add(createDefaultAccount("5000", "EXPENSES", "Expenses", AccountType.EXPENSE,
                                        AccountCategory.OPERATING_EXPENSES, null, institutionId, createdBy, 0));

        // Operating Expenses
        accounts.add(createDefaultAccount("5100", "SALARIES_EXPENSE", "Salaries and Wages", AccountType.EXPENSE,
                                        AccountCategory.OPERATING_EXPENSES, "5000", institutionId, createdBy, 1));
        accounts.add(createDefaultAccount("5200", "UTILITIES", "Utilities", AccountType.EXPENSE,
                                        AccountCategory.OPERATING_EXPENSES, "5000", institutionId, createdBy, 1));
        accounts.add(createDefaultAccount("5300", "SUPPLIES", "Supplies", AccountType.EXPENSE,
                                        AccountCategory.OPERATING_EXPENSES, "5000", institutionId, createdBy, 1));

        return accounts;
    }

    private Account createDefaultAccount(String code, String name, String displayName, AccountType type,
                                       AccountCategory category, String parentCode, String institutionId,
                                       String createdBy, int level) {
        Account account = new Account();
        account.setAccountCode(code);
        account.setAccountName(displayName);
        account.setDescription("Default " + displayName + " account");
        account.setAccountType(type);
        account.setCategory(category);
        account.setParentAccountId(parentCode); // Will be resolved to actual ID later
        account.setLevel(level);
        account.setInstitutionId(institutionId);
        account.setCreatedBy(createdBy);
        account.setLastModifiedBy(createdBy);
        account.setIsActive(true);
        account.setCurrency("USD");
        account.setBalance(BigDecimal.ZERO);
        account.setDebitBalance(BigDecimal.ZERO);
        account.setCreditBalance(BigDecimal.ZERO);

        return account;
    }
}
