package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.Transaction;
import com.eduai.schoolmanagement.entity.Transaction.TransactionType;
import com.eduai.schoolmanagement.entity.Transaction.TransactionCategory;
import com.eduai.schoolmanagement.entity.Transaction.TransactionStatus;
import com.eduai.schoolmanagement.entity.Transaction.ApprovalStatus;
import com.eduai.schoolmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Create new transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return ResponseEntity.ok(createdTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create transaction", "message", e.getMessage()));
        }
    }

    // Create simple income transaction
    @PostMapping("/income")
    public ResponseEntity<?> createIncomeTransaction(@RequestBody Map<String, Object> request) {
        try {
            String institutionId = (String) request.get("institutionId");
            String description = (String) request.get("description");
            String incomeAccountId = (String) request.get("incomeAccountId");
            String cashAccountId = (String) request.get("cashAccountId");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String createdBy = (String) request.get("createdBy");

            Transaction transaction = transactionService.createIncomeTransaction(
                institutionId, description, incomeAccountId, cashAccountId, amount, createdBy);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create income transaction", "message", e.getMessage()));
        }
    }

    // Create simple expense transaction
    @PostMapping("/expense")
    public ResponseEntity<?> createExpenseTransaction(@RequestBody Map<String, Object> request) {
        try {
            String institutionId = (String) request.get("institutionId");
            String description = (String) request.get("description");
            String expenseAccountId = (String) request.get("expenseAccountId");
            String cashAccountId = (String) request.get("cashAccountId");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String createdBy = (String) request.get("createdBy");

            Transaction transaction = transactionService.createExpenseTransaction(
                institutionId, description, expenseAccountId, cashAccountId, amount, createdBy);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create expense transaction", "message", e.getMessage()));
        }
    }

    // Create transfer transaction
    @PostMapping("/transfer")
    public ResponseEntity<?> createTransferTransaction(@RequestBody Map<String, Object> request) {
        try {
            String institutionId = (String) request.get("institutionId");
            String description = (String) request.get("description");
            String fromAccountId = (String) request.get("fromAccountId");
            String toAccountId = (String) request.get("toAccountId");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String createdBy = (String) request.get("createdBy");

            Transaction transaction = transactionService.createTransferTransaction(
                institutionId, description, fromAccountId, toAccountId, amount, createdBy);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create transfer transaction", "message", e.getMessage()));
        }
    }

    // Update transaction
    @PutMapping("/{transactionId}")
    public ResponseEntity<?> updateTransaction(@PathVariable String transactionId,
                                             @RequestBody Transaction transaction) {
        try {
            Transaction updatedTransaction = transactionService.updateTransaction(transactionId, transaction);
            return ResponseEntity.ok(updatedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to update transaction", "message", e.getMessage()));
        }
    }

    // Get transaction by ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable String transactionId) {
        try {
            Optional<Transaction> transaction = transactionService.getTransactionById(transactionId);
            if (transaction.isPresent()) {
                return ResponseEntity.ok(transaction.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Transaction not found", "transactionId", transactionId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transaction", "message", e.getMessage()));
        }
    }

    // Get transactions by institution (with pagination)
    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<?> getTransactionsByInstitution(@PathVariable String institutionId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @RequestParam(defaultValue = "createdDate") String sortBy,
                                                        @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Transaction> transactions = transactionService.getTransactionsByInstitution(institutionId, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transactions", "message", e.getMessage()));
        }
    }

    // Get transactions by status
    @GetMapping("/institution/{institutionId}/status/{status}")
    public ResponseEntity<?> getTransactionsByStatus(@PathVariable String institutionId,
                                                   @PathVariable TransactionStatus status) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByStatus(institutionId, status);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transactions by status", "message", e.getMessage()));
        }
    }

    // Get transactions by date range
    @GetMapping("/institution/{institutionId}/date-range")
    public ResponseEntity<?> getTransactionsByDateRange(@PathVariable String institutionId,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByDateRange(institutionId, startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transactions by date range", "message", e.getMessage()));
        }
    }

    // Get transactions by account
    @GetMapping("/institution/{institutionId}/account/{accountId}")
    public ResponseEntity<?> getTransactionsByAccount(@PathVariable String institutionId,
                                                    @PathVariable String accountId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByAccount(institutionId, accountId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transactions by account", "message", e.getMessage()));
        }
    }

    // Get pending approval transactions
    @GetMapping("/institution/{institutionId}/pending-approval")
    public ResponseEntity<?> getPendingApprovalTransactions(@PathVariable String institutionId) {
        try {
            List<Transaction> transactions = transactionService.getPendingApprovalTransactions(institutionId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve pending approval transactions", "message", e.getMessage()));
        }
    }

    // Get unbalanced transactions
    @GetMapping("/institution/{institutionId}/unbalanced")
    public ResponseEntity<?> getUnbalancedTransactions(@PathVariable String institutionId) {
        try {
            List<Transaction> transactions = transactionService.getUnbalancedTransactions(institutionId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve unbalanced transactions", "message", e.getMessage()));
        }
    }

    // Search transactions
    @GetMapping("/institution/{institutionId}/search")
    public ResponseEntity<?> searchTransactions(@PathVariable String institutionId,
                                               @RequestParam String searchTerm) {
        try {
            List<Transaction> transactions = transactionService.searchTransactions(institutionId, searchTerm);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to search transactions", "message", e.getMessage()));
        }
    }

    // Submit transaction for approval
    @PostMapping("/{transactionId}/submit-for-approval")
    public ResponseEntity<?> submitForApproval(@PathVariable String transactionId,
                                             @RequestParam String submittedBy) {
        try {
            Transaction transaction = transactionService.submitForApproval(transactionId, submittedBy);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to submit transaction for approval", "message", e.getMessage()));
        }
    }

    // Approve transaction
    @PostMapping("/{transactionId}/approve")
    public ResponseEntity<?> approveTransaction(@PathVariable String transactionId,
                                              @RequestParam String approvedBy,
                                              @RequestParam(required = false) String comments) {
        try {
            Transaction transaction = transactionService.approveTransaction(transactionId, approvedBy, comments);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to approve transaction", "message", e.getMessage()));
        }
    }

    // Post transaction
    @PostMapping("/{transactionId}/post")
    public ResponseEntity<?> postTransaction(@PathVariable String transactionId,
                                           @RequestParam String postedBy) {
        try {
            Transaction transaction = transactionService.postTransaction(transactionId, postedBy);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to post transaction", "message", e.getMessage()));
        }
    }

    // Reverse transaction
    @PostMapping("/{transactionId}/reverse")
    public ResponseEntity<?> reverseTransaction(@PathVariable String transactionId,
                                              @RequestParam String reason,
                                              @RequestParam String reversedBy) {
        try {
            Transaction reversalTransaction = transactionService.reverseTransaction(transactionId, reason, reversedBy);
            return ResponseEntity.ok(reversalTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to reverse transaction", "message", e.getMessage()));
        }
    }

    // Get transaction statistics
    @GetMapping("/institution/{institutionId}/statistics")
    public ResponseEntity<?> getTransactionStatistics(@PathVariable String institutionId,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> stats = transactionService.getTransactionStatistics(institutionId, startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transaction statistics", "message", e.getMessage()));
        }
    }

    // Delete transaction
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String transactionId) {
        try {
            transactionService.deleteTransaction(transactionId);
            return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to delete transaction", "message", e.getMessage()));
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "TransactionController",
            "timestamp", java.time.LocalDateTime.now()
        ));
    }

    // Get transaction types enum
    @GetMapping("/types")
    public ResponseEntity<?> getTransactionTypes() {
        try {
            TransactionType[] types = TransactionType.values();
            return ResponseEntity.ok(java.util.Arrays.stream(types)
                .map(type -> Map.of("value", type.name(), "displayName", type.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transaction types", "message", e.getMessage()));
        }
    }

    // Get transaction categories enum
    @GetMapping("/categories")
    public ResponseEntity<?> getTransactionCategories() {
        try {
            TransactionCategory[] categories = TransactionCategory.values();
            return ResponseEntity.ok(java.util.Arrays.stream(categories)
                .map(category -> Map.of("value", category.name(), "displayName", category.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transaction categories", "message", e.getMessage()));
        }
    }

    // Get transaction statuses enum
    @GetMapping("/statuses")
    public ResponseEntity<?> getTransactionStatuses() {
        try {
            TransactionStatus[] statuses = TransactionStatus.values();
            return ResponseEntity.ok(java.util.Arrays.stream(statuses)
                .map(status -> Map.of("value", status.name(), "displayName", status.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve transaction statuses", "message", e.getMessage()));
        }
    }

    // Get approval statuses enum
    @GetMapping("/approval-statuses")
    public ResponseEntity<?> getApprovalStatuses() {
        try {
            ApprovalStatus[] statuses = ApprovalStatus.values();
            return ResponseEntity.ok(java.util.Arrays.stream(statuses)
                .map(status -> Map.of("value", status.name(), "displayName", status.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve approval statuses", "message", e.getMessage()));
        }
    }

    // Bulk operations
    @PostMapping("/bulk/approve")
    public ResponseEntity<?> bulkApproveTransactions(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> transactionIds = (List<String>) request.get("transactionIds");
            String approvedBy = (String) request.get("approvedBy");
            String comments = (String) request.get("comments");

            List<Transaction> approvedTransactions = new java.util.ArrayList<>();
            List<String> errors = new java.util.ArrayList<>();

            for (String transactionId : transactionIds) {
                try {
                    Transaction approved = transactionService.approveTransaction(transactionId, approvedBy, comments);
                    approvedTransactions.add(approved);
                } catch (Exception e) {
                    errors.add("Transaction " + transactionId + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                "approved", approvedTransactions,
                "errors", errors,
                "approvedCount", approvedTransactions.size(),
                "errorCount", errors.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to bulk approve transactions", "message", e.getMessage()));
        }
    }

    @PostMapping("/bulk/post")
    public ResponseEntity<?> bulkPostTransactions(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> transactionIds = (List<String>) request.get("transactionIds");
            String postedBy = (String) request.get("postedBy");

            List<Transaction> postedTransactions = new java.util.ArrayList<>();
            List<String> errors = new java.util.ArrayList<>();

            for (String transactionId : transactionIds) {
                try {
                    Transaction posted = transactionService.postTransaction(transactionId, postedBy);
                    postedTransactions.add(posted);
                } catch (Exception e) {
                    errors.add("Transaction " + transactionId + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                "posted", postedTransactions,
                "errors", errors,
                "postedCount", postedTransactions.size(),
                "errorCount", errors.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to bulk post transactions", "message", e.getMessage()));
        }
    }
}
