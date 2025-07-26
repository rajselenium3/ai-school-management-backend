package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.Account;
import com.eduai.schoolmanagement.entity.Account.AccountType;
import com.eduai.schoolmanagement.entity.Account.AccountCategory;
import com.eduai.schoolmanagement.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Create new account
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        try {
            Account createdAccount = accountService.createAccount(account);
            return ResponseEntity.ok(createdAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create account", "message", e.getMessage()));
        }
    }

    // Update existing account
    @PutMapping("/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable String accountId, @RequestBody Account account) {
        try {
            Account updatedAccount = accountService.updateAccount(accountId, account);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to update account", "message", e.getMessage()));
        }
    }

    // Get account by ID
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable String accountId) {
        try {
            Optional<Account> account = accountService.getAccountById(accountId);
            if (account.isPresent()) {
                return ResponseEntity.ok(account.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found", "accountId", accountId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve account", "message", e.getMessage()));
        }
    }

    // Get account by code
    @GetMapping("/by-code/{accountCode}")
    public ResponseEntity<?> getAccountByCode(@PathVariable String accountCode,
                                            @RequestParam String institutionId) {
        try {
            Optional<Account> account = accountService.getAccountByCode(accountCode, institutionId);
            if (account.isPresent()) {
                return ResponseEntity.ok(account.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found", "accountCode", accountCode));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve account", "message", e.getMessage()));
        }
    }

    // Get all accounts for institution
    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<?> getAllAccounts(@PathVariable String institutionId,
                                          @RequestParam(defaultValue = "false") boolean activeOnly) {
        try {
            List<Account> accounts;
            if (activeOnly) {
                accounts = accountService.getActiveAccounts(institutionId);
            } else {
                accounts = accountService.getAllAccounts(institutionId);
            }
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve accounts", "message", e.getMessage()));
        }
    }

    // Get accounts by type
    @GetMapping("/institution/{institutionId}/type/{accountType}")
    public ResponseEntity<?> getAccountsByType(@PathVariable String institutionId,
                                             @PathVariable AccountType accountType) {
        try {
            List<Account> accounts = accountService.getAccountsByType(institutionId, accountType);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve accounts by type", "message", e.getMessage()));
        }
    }

    // Get accounts by category
    @GetMapping("/institution/{institutionId}/category/{category}")
    public ResponseEntity<?> getAccountsByCategory(@PathVariable String institutionId,
                                                 @PathVariable AccountCategory category) {
        try {
            List<Account> accounts = accountService.getAccountsByCategory(institutionId, category);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve accounts by category", "message", e.getMessage()));
        }
    }

    // Get chart of accounts
    @GetMapping("/institution/{institutionId}/chart-of-accounts")
    public ResponseEntity<?> getChartOfAccounts(@PathVariable String institutionId) {
        try {
            List<Account> chartOfAccounts = accountService.getChartOfAccounts(institutionId);
            return ResponseEntity.ok(chartOfAccounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve chart of accounts", "message", e.getMessage()));
        }
    }

    // Get account hierarchy
    @GetMapping("/{accountId}/hierarchy")
    public ResponseEntity<?> getAccountHierarchy(@PathVariable String accountId) {
        try {
            List<Account> hierarchy = accountService.getAccountHierarchy(accountId);
            return ResponseEntity.ok(hierarchy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve account hierarchy", "message", e.getMessage()));
        }
    }

    // Search accounts
    @GetMapping("/institution/{institutionId}/search")
    public ResponseEntity<?> searchAccounts(@PathVariable String institutionId,
                                          @RequestParam String searchTerm) {
        try {
            List<Account> accounts = accountService.searchAccounts(institutionId, searchTerm);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to search accounts", "message", e.getMessage()));
        }
    }

    // Get cash and bank accounts
    @GetMapping("/institution/{institutionId}/cash-and-bank")
    public ResponseEntity<?> getCashAndBankAccounts(@PathVariable String institutionId) {
        try {
            List<Account> accounts = accountService.getCashAndBankAccounts(institutionId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve cash and bank accounts", "message", e.getMessage()));
        }
    }

    // Get receivable accounts
    @GetMapping("/institution/{institutionId}/receivables")
    public ResponseEntity<?> getReceivableAccounts(@PathVariable String institutionId) {
        try {
            List<Account> accounts = accountService.getReceivableAccounts(institutionId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve receivable accounts", "message", e.getMessage()));
        }
    }

    // Get payable accounts
    @GetMapping("/institution/{institutionId}/payables")
    public ResponseEntity<?> getPayableAccounts(@PathVariable String institutionId) {
        try {
            List<Account> accounts = accountService.getPayableAccounts(institutionId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve payable accounts", "message", e.getMessage()));
        }
    }

    // Get income accounts
    @GetMapping("/institution/{institutionId}/income")
    public ResponseEntity<?> getIncomeAccounts(@PathVariable String institutionId) {
        try {
            List<Account> accounts = accountService.getIncomeAccounts(institutionId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve income accounts", "message", e.getMessage()));
        }
    }

    // Get expense accounts
    @GetMapping("/institution/{institutionId}/expenses")
    public ResponseEntity<?> getExpenseAccounts(@PathVariable String institutionId) {
        try {
            List<Account> accounts = accountService.getExpenseAccounts(institutionId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve expense accounts", "message", e.getMessage()));
        }
    }

    // Get accounts approaching budget limit
    @GetMapping("/institution/{institutionId}/budget-alerts/approaching")
    public ResponseEntity<?> getAccountsApproachingBudgetLimit(@PathVariable String institutionId) {
        try {
            List<Account> accounts = accountService.getAccountsApproachingBudgetLimit(institutionId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve accounts approaching budget limit", "message", e.getMessage()));
        }
    }

    // Get accounts exceeding budget limit
    @GetMapping("/institution/{institutionId}/budget-alerts/exceeding")
    public ResponseEntity<?> getAccountsExceedingBudgetLimit(@PathVariable String institutionId) {
        try {
            List<Account> accounts = accountService.getAccountsExceedingBudgetLimit(institutionId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve accounts exceeding budget limit", "message", e.getMessage()));
        }
    }

    // Create default chart of accounts
    @PostMapping("/institution/{institutionId}/default-chart")
    public ResponseEntity<?> createDefaultChartOfAccounts(@PathVariable String institutionId,
                                                         @RequestParam String createdBy) {
        try {
            accountService.createDefaultChartOfAccounts(institutionId, createdBy);
            return ResponseEntity.ok(Map.of("message", "Default chart of accounts created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create default chart of accounts", "message", e.getMessage()));
        }
    }

    // Get account statistics
    @GetMapping("/institution/{institutionId}/statistics")
    public ResponseEntity<?> getAccountStatistics(@PathVariable String institutionId) {
        try {
            Map<String, Object> stats = accountService.getAccountStatistics(institutionId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve account statistics", "message", e.getMessage()));
        }
    }

    // Update account balance (for system use)
    @PostMapping("/{accountId}/update-balance")
    public ResponseEntity<?> updateAccountBalance(@PathVariable String accountId,
                                                 @RequestParam(required = false) String debitAmount,
                                                 @RequestParam(required = false) String creditAmount) {
        try {
            java.math.BigDecimal debit = debitAmount != null ? new java.math.BigDecimal(debitAmount) : null;
            java.math.BigDecimal credit = creditAmount != null ? new java.math.BigDecimal(creditAmount) : null;

            accountService.updateAccountBalance(accountId, debit, credit);
            return ResponseEntity.ok(Map.of("message", "Account balance updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to update account balance", "message", e.getMessage()));
        }
    }

    // Delete account
    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable String accountId) {
        try {
            accountService.deleteAccount(accountId);
            return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to delete account", "message", e.getMessage()));
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "AccountController",
            "timestamp", java.time.LocalDateTime.now()
        ));
    }

    // Get account types enum
    @GetMapping("/types")
    public ResponseEntity<?> getAccountTypes() {
        try {
            AccountType[] types = AccountType.values();
            return ResponseEntity.ok(java.util.Arrays.stream(types)
                .map(type -> Map.of("value", type.name(), "displayName", type.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve account types", "message", e.getMessage()));
        }
    }

    // Get account categories enum
    @GetMapping("/categories")
    public ResponseEntity<?> getAccountCategories() {
        try {
            AccountCategory[] categories = AccountCategory.values();
            return ResponseEntity.ok(java.util.Arrays.stream(categories)
                .map(category -> Map.of("value", category.name(), "displayName", category.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve account categories", "message", e.getMessage()));
        }
    }
}
