package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.Salary;
import com.eduai.schoolmanagement.entity.Salary.SalaryStatus;
import com.eduai.schoolmanagement.entity.Salary.PaymentMethod;
import com.eduai.schoolmanagement.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "*")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    // Basic CRUD operations
    @PostMapping
    public ResponseEntity<?> createSalary(@RequestBody Salary salary) {
        try {
            Salary savedSalary = salaryService.createSalary(salary);
            return ResponseEntity.ok(savedSalary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to create salary", "message", e.getMessage()));
        }
    }

    @PutMapping("/{salaryId}")
    public ResponseEntity<?> updateSalary(@PathVariable String salaryId,
                                         @RequestBody Salary salary) {
        try {
            Salary updatedSalary = salaryService.updateSalary(salaryId, salary);
            return ResponseEntity.ok(updatedSalary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to update salary", "message", e.getMessage()));
        }
    }

    @GetMapping("/{salaryId}")
    public ResponseEntity<?> getSalary(@PathVariable String salaryId) {
        try {
            Optional<Salary> salary = salaryService.getSalary(salaryId);
            if (salary.isPresent()) {
                return ResponseEntity.ok(salary.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Salary record not found", "salaryId", salaryId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve salary", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{salaryId}")
    public ResponseEntity<?> deleteSalary(@PathVariable String salaryId) {
        try {
            salaryService.deleteSalary(salaryId);
            return ResponseEntity.ok(Map.of("message", "Salary record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to delete salary", "message", e.getMessage()));
        }
    }

    // Bulk salary generation
    @PostMapping("/generate-monthly")
    public ResponseEntity<?> generateMonthlySalaries(@RequestBody Map<String, Object> request) {
        try {
            String institutionId = (String) request.get("institutionId");
            Integer payrollMonth = (Integer) request.get("payrollMonth");
            Integer payrollYear = (Integer) request.get("payrollYear");
            @SuppressWarnings("unchecked")
            List<String> employeeIds = (List<String>) request.get("employeeIds");
            String processedBy = (String) request.get("processedBy");

            List<Salary> generatedSalaries = salaryService.generateMonthlySalaries(
                institutionId, payrollMonth, payrollYear, employeeIds, processedBy);

            return ResponseEntity.ok(Map.of(
                "message", "Monthly salaries generated successfully",
                "recordsGenerated", generatedSalaries.size(),
                "salaries", generatedSalaries
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to generate monthly salaries", "message", e.getMessage()));
        }
    }

    // Salary processing workflow
    @PostMapping("/{salaryId}/process")
    public ResponseEntity<?> processSalary(@PathVariable String salaryId,
                                          @RequestParam String processedBy) {
        try {
            Salary processedSalary = salaryService.processSalary(salaryId, processedBy);
            return ResponseEntity.ok(Map.of(
                "message", "Salary processed successfully",
                "salary", processedSalary
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to process salary", "message", e.getMessage()));
        }
    }

    @PostMapping("/{salaryId}/approve")
    public ResponseEntity<?> approveSalary(@PathVariable String salaryId,
                                          @RequestParam String approvedBy,
                                          @RequestParam(required = false) String comments) {
        try {
            Salary approvedSalary = salaryService.approveSalary(salaryId, approvedBy, comments);
            return ResponseEntity.ok(Map.of(
                "message", "Salary approved successfully",
                "salary", approvedSalary
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to approve salary", "message", e.getMessage()));
        }
    }

    @PostMapping("/{salaryId}/pay")
    public ResponseEntity<?> paySalary(@PathVariable String salaryId,
                                      @RequestBody Map<String, Object> paymentRequest) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.valueOf((String) paymentRequest.get("paymentMethod"));
            String transactionId = (String) paymentRequest.get("transactionId");
            String paidBy = (String) paymentRequest.get("paidBy");

            Salary paidSalary = salaryService.paySalary(salaryId, paymentMethod, transactionId, paidBy);
            return ResponseEntity.ok(Map.of(
                "message", "Salary payment recorded successfully",
                "salary", paidSalary
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to record salary payment", "message", e.getMessage()));
        }
    }

    // Query operations
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getSalariesByEmployee(@PathVariable String employeeId,
                                                  @RequestParam String institutionId) {
        try {
            List<Salary> salaries = salaryService.getSalariesByEmployee(employeeId, institutionId);
            return ResponseEntity.ok(salaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve employee salaries", "message", e.getMessage()));
        }
    }

    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<?> getSalariesByInstitution(@PathVariable String institutionId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size,
                                                     @RequestParam(defaultValue = "payrollYear") String sortBy,
                                                     @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Salary> salaries = salaryService.getSalariesByInstitution(institutionId, pageable);
            return ResponseEntity.ok(salaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve institution salaries", "message", e.getMessage()));
        }
    }

    @GetMapping("/period/{institutionId}")
    public ResponseEntity<?> getSalariesForPeriod(@PathVariable String institutionId,
                                                 @RequestParam Integer payrollMonth,
                                                 @RequestParam Integer payrollYear) {
        try {
            List<Salary> salaries = salaryService.getSalariesForPeriod(institutionId, payrollMonth, payrollYear);
            return ResponseEntity.ok(salaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve salaries for period", "message", e.getMessage()));
        }
    }

    @GetMapping("/pending-approvals/{institutionId}")
    public ResponseEntity<?> getPendingApprovals(@PathVariable String institutionId) {
        try {
            List<Salary> pendingApprovals = salaryService.getPendingApprovals(institutionId);
            return ResponseEntity.ok(pendingApprovals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve pending approvals", "message", e.getMessage()));
        }
    }

    @GetMapping("/unpaid/{institutionId}")
    public ResponseEntity<?> getUnpaidSalaries(@PathVariable String institutionId) {
        try {
            List<Salary> unpaidSalaries = salaryService.getUnpaidSalaries(institutionId);
            return ResponseEntity.ok(unpaidSalaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve unpaid salaries", "message", e.getMessage()));
        }
    }

    // Analytics and reporting
    @GetMapping("/summary/{institutionId}")
    public ResponseEntity<?> getPayrollSummary(@PathVariable String institutionId,
                                              @RequestParam Integer payrollMonth,
                                              @RequestParam Integer payrollYear) {
        try {
            Map<String, Object> summary = salaryService.getPayrollSummary(institutionId, payrollMonth, payrollYear);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve payroll summary", "message", e.getMessage()));
        }
    }

    @GetMapping("/analytics/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeSalaryAnalytics(@PathVariable String employeeId,
                                                       @RequestParam String institutionId,
                                                       @RequestParam String financialYear) {
        try {
            Map<String, Object> analytics = salaryService.getEmployeeSalaryAnalytics(
                employeeId, institutionId, financialYear);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve employee salary analytics", "message", e.getMessage()));
        }
    }

    @GetMapping("/report/department/{institutionId}")
    public ResponseEntity<?> getDepartmentPayrollReport(@PathVariable String institutionId,
                                                       @RequestParam Integer payrollMonth,
                                                       @RequestParam Integer payrollYear) {
        try {
            List<Map<String, Object>> report = salaryService.getDepartmentPayrollReport(
                institutionId, payrollMonth, payrollYear);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve department payroll report", "message", e.getMessage()));
        }
    }

    // Export functionality
    @GetMapping("/export/csv/{institutionId}")
    public ResponseEntity<?> exportPayrollToCsv(@PathVariable String institutionId,
                                               @RequestParam Integer payrollMonth,
                                               @RequestParam Integer payrollYear) {
        try {
            String csvData = salaryService.exportPayrollToCsv(institutionId, payrollMonth, payrollYear);
            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=payroll-export.csv")
                .body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to export payroll data", "message", e.getMessage()));
        }
    }

    // Search functionality
    @GetMapping("/search/{institutionId}")
    public ResponseEntity<?> searchSalaries(@PathVariable String institutionId,
                                           @RequestParam String searchTerm,
                                           @RequestParam(required = false) Integer payrollMonth,
                                           @RequestParam(required = false) Integer payrollYear) {
        try {
            List<Salary> searchResults = salaryService.searchSalaries(
                institutionId, searchTerm, payrollMonth, payrollYear);
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to search salary records", "message", e.getMessage()));
        }
    }

    // Salary recalculation
    @PostMapping("/{salaryId}/recalculate")
    public ResponseEntity<?> recalculateSalary(@PathVariable String salaryId,
                                              @RequestParam String recalculatedBy) {
        try {
            Optional<Salary> salaryOpt = salaryService.getSalary(salaryId);
            if (!salaryOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Salary record not found"));
            }

            Salary salary = salaryOpt.get();
            salaryService.calculateSalaryComponents(salary);
            salary.setLastModifiedBy(recalculatedBy);

            Salary updatedSalary = salaryService.updateSalary(salaryId, salary);
            return ResponseEntity.ok(Map.of(
                "message", "Salary recalculated successfully",
                "salary", updatedSalary
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to recalculate salary", "message", e.getMessage()));
        }
    }

    // Bulk operations
    @PostMapping("/bulk-approve")
    public ResponseEntity<?> bulkApproveSalaries(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> salaryIds = (List<String>) request.get("salaryIds");
            String approvedBy = (String) request.get("approvedBy");
            String comments = (String) request.get("comments");

            List<Salary> approvedSalaries = new java.util.ArrayList<>();
            List<String> errors = new java.util.ArrayList<>();

            for (String salaryId : salaryIds) {
                try {
                    Salary approved = salaryService.approveSalary(salaryId, approvedBy, comments);
                    approvedSalaries.add(approved);
                } catch (Exception e) {
                    errors.add("Salary " + salaryId + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                "approved", approvedSalaries,
                "errors", errors,
                "approvedCount", approvedSalaries.size(),
                "errorCount", errors.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to bulk approve salaries", "message", e.getMessage()));
        }
    }

    @PostMapping("/bulk-process")
    public ResponseEntity<?> bulkProcessSalaries(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> salaryIds = (List<String>) request.get("salaryIds");
            String processedBy = (String) request.get("processedBy");

            List<Salary> processedSalaries = new java.util.ArrayList<>();
            List<String> errors = new java.util.ArrayList<>();

            for (String salaryId : salaryIds) {
                try {
                    Salary processed = salaryService.processSalary(salaryId, processedBy);
                    processedSalaries.add(processed);
                } catch (Exception e) {
                    errors.add("Salary " + salaryId + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                "processed", processedSalaries,
                "errors", errors,
                "processedCount", processedSalaries.size(),
                "errorCount", errors.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to bulk process salaries", "message", e.getMessage()));
        }
    }

    @PostMapping("/bulk-pay")
    public ResponseEntity<?> bulkPaySalaries(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> salaryIds = (List<String>) request.get("salaryIds");
            PaymentMethod paymentMethod = PaymentMethod.valueOf((String) request.get("paymentMethod"));
            String paidBy = (String) request.get("paidBy");
            String batchTransactionId = (String) request.get("batchTransactionId");

            List<Salary> paidSalaries = new java.util.ArrayList<>();
            List<String> errors = new java.util.ArrayList<>();

            for (int i = 0; i < salaryIds.size(); i++) {
                String salaryId = salaryIds.get(i);
                try {
                    String transactionId = batchTransactionId + "-" + (i + 1);
                    Salary paid = salaryService.paySalary(salaryId, paymentMethod, transactionId, paidBy);
                    paidSalaries.add(paid);
                } catch (Exception e) {
                    errors.add("Salary " + salaryId + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                "paid", paidSalaries,
                "errors", errors,
                "paidCount", paidSalaries.size(),
                "errorCount", errors.size(),
                "totalAmount", paidSalaries.stream()
                    .filter(s -> s.getNetSalary() != null)
                    .mapToDouble(Salary::getNetSalary).sum()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to bulk pay salaries", "message", e.getMessage()));
        }
    }

    // Status update endpoints
    @PostMapping("/{salaryId}/hold")
    public ResponseEntity<?> holdSalary(@PathVariable String salaryId,
                                       @RequestParam String reason,
                                       @RequestParam String heldBy) {
        try {
            Optional<Salary> salaryOpt = salaryService.getSalary(salaryId);
            if (!salaryOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Salary record not found"));
            }

            Salary salary = salaryOpt.get();
            salary.setStatus(SalaryStatus.ON_HOLD);
            salary.setSalaryOnHold(true);
            salary.setApprovalComments("HELD: " + reason);
            salary.setLastModifiedBy(heldBy);

            Salary updatedSalary = salaryService.updateSalary(salaryId, salary);
            return ResponseEntity.ok(Map.of(
                "message", "Salary put on hold successfully",
                "salary", updatedSalary
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to hold salary", "message", e.getMessage()));
        }
    }

    @PostMapping("/{salaryId}/release-hold")
    public ResponseEntity<?> releaseHold(@PathVariable String salaryId,
                                        @RequestParam String releasedBy) {
        try {
            Optional<Salary> salaryOpt = salaryService.getSalary(salaryId);
            if (!salaryOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Salary record not found"));
            }

            Salary salary = salaryOpt.get();
            salary.setStatus(SalaryStatus.PENDING_APPROVAL);
            salary.setSalaryOnHold(false);
            salary.setApprovalComments("Hold released by " + releasedBy);
            salary.setLastModifiedBy(releasedBy);

            Salary updatedSalary = salaryService.updateSalary(salaryId, salary);
            return ResponseEntity.ok(Map.of(
                "message", "Salary hold released successfully",
                "salary", updatedSalary
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to release salary hold", "message", e.getMessage()));
        }
    }

    // Get enum values for frontend
    @GetMapping("/enums/statuses")
    public ResponseEntity<?> getSalaryStatuses() {
        try {
            SalaryStatus[] statuses = SalaryStatus.values();
            return ResponseEntity.ok(java.util.Arrays.stream(statuses)
                .map(status -> Map.of("value", status.name(), "displayName", status.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve salary statuses", "message", e.getMessage()));
        }
    }

    @GetMapping("/enums/payment-methods")
    public ResponseEntity<?> getPaymentMethods() {
        try {
            PaymentMethod[] methods = PaymentMethod.values();
            return ResponseEntity.ok(java.util.Arrays.stream(methods)
                .map(method -> Map.of("value", method.name(), "displayName", method.getDisplayName()))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve payment methods", "message", e.getMessage()));
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "SalaryController",
            "timestamp", java.time.LocalDateTime.now()
        ));
    }

    // Dashboard statistics
    @GetMapping("/dashboard/{institutionId}")
    public ResponseEntity<?> getDashboardStatistics(@PathVariable String institutionId) {
        try {
            // Get current month statistics
            LocalDate now = LocalDate.now();
            Integer currentMonth = now.getMonthValue();
            Integer currentYear = now.getYear();

            Map<String, Object> stats = salaryService.getPayrollSummary(
                institutionId, currentMonth, currentYear);

            // Add pending counts
            List<Salary> pendingApprovals = salaryService.getPendingApprovals(institutionId);
            List<Salary> unpaidSalaries = salaryService.getUnpaidSalaries(institutionId);

            stats.put("pendingApprovalsCount", pendingApprovals.size());
            stats.put("unpaidSalariesCount", unpaidSalaries.size());
            stats.put("currentMonth", currentMonth);
            stats.put("currentYear", currentYear);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve dashboard statistics", "message", e.getMessage()));
        }
    }
}
