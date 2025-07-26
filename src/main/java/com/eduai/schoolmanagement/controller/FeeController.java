package com.eduai.schoolmanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.service.FeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/fees")
@RequiredArgsConstructor
@Tag(name = "Fee Management", description = "Fee structure and management operations")
@CrossOrigin(origins = "*")
public class FeeController {

    private final FeeService feeService;

    @GetMapping
    @Operation(summary = "Get all fees")
    public ResponseEntity<List<Fee>> getAllFees() {
        List<Fee> fees = feeService.getAllFees();
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fee by ID")
    public ResponseEntity<Fee> getFeeById(@PathVariable String id) {
        Optional<Fee> fee = feeService.getFeeById(id);
        return fee.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{feeType}")
    @Operation(summary = "Get fees by type")
    public ResponseEntity<List<Fee>> getFeesByType(@PathVariable String feeType) {
        List<Fee> fees = feeService.getFeesByType(feeType);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get fees by category")
    public ResponseEntity<List<Fee>> getFeesByCategory(@PathVariable String category) {
        List<Fee> fees = feeService.getFeesByCategory(category);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/academic-year/{academicYear}")
    @Operation(summary = "Get fees by academic year")
    public ResponseEntity<List<Fee>> getFeesByAcademicYear(@PathVariable String academicYear) {
        List<Fee> fees = feeService.getFeesByAcademicYear(academicYear);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/applicable")
    @Operation(summary = "Get applicable fees for student")
    public ResponseEntity<List<Fee>> getApplicableFeesForStudent(
            @RequestParam String grade,
            @RequestParam String department,
            @RequestParam String academicYear) {
        List<Fee> fees = feeService.getApplicableFeesForStudent(grade, department, academicYear);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active fees for grade and year")
    public ResponseEntity<List<Fee>> getActiveFeesForGradeAndYear(
            @RequestParam String grade,
            @RequestParam String academicYear) {
        List<Fee> fees = feeService.getActiveFeesForGradeAndYear(grade, academicYear);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue fees")
    public ResponseEntity<List<Fee>> getOverdueFees() {
        List<Fee> fees = feeService.getOverdueFees();
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/scholarship-eligible")
    @Operation(summary = "Get scholarship eligible fees")
    public ResponseEntity<List<Fee>> getScholarshipEligibleFees() {
        List<Fee> fees = feeService.getScholarshipEligibleFees();
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/search")
    @Operation(summary = "Search fees by name")
    public ResponseEntity<List<Fee>> searchFees(@RequestParam String searchTerm) {
        List<Fee> fees = feeService.searchFees(searchTerm);
        return ResponseEntity.ok(fees);
    }

    @PostMapping
    @Operation(summary = "Create new fee")
    public ResponseEntity<Fee> createFee(@Valid @RequestBody Fee fee) {
        Fee savedFee = feeService.saveFee(fee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update fee")
    public ResponseEntity<Fee> updateFee(@PathVariable String id, @Valid @RequestBody Fee fee) {
        Fee updatedFee = feeService.updateFee(id, fee);
        return ResponseEntity.ok(updatedFee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete fee")
    public ResponseEntity<Void> deleteFee(@PathVariable String id) {
        feeService.deleteFee(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate fee")
    public ResponseEntity<Fee> activateFee(@PathVariable String id) {
        try {
            Fee fee = feeService.activateFee(id);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate fee")
    public ResponseEntity<Fee> deactivateFee(@PathVariable String id) {
        try {
            Fee fee = feeService.deactivateFee(id);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple fees")
    public ResponseEntity<List<Fee>> createBulkFees(@Valid @RequestBody List<Fee> fees) {
        List<Fee> savedFees = feeService.createBulkFees(fees);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFees);
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate fee")
    public ResponseEntity<Fee> duplicateFee(
            @PathVariable String id,
            @RequestParam String newName,
            @RequestParam String newAcademicYear) {
        try {
            Fee duplicatedFee = feeService.duplicateFee(id, newName, newAcademicYear);
            return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedFee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/calculate-total")
    @Operation(summary = "Calculate total fee for student")
    public ResponseEntity<Object> calculateTotalFeeForStudent(
            @RequestParam String grade,
            @RequestParam String department,
            @RequestParam String academicYear) {
        Double totalFee = feeService.calculateTotalFeeForStudent(grade, department, academicYear);
        return ResponseEntity.ok(java.util.Map.of(
            "grade", grade,
            "department", department,
            "academicYear", academicYear,
            "totalMandatoryFee", totalFee
        ));
    }

    @GetMapping("/calculate-discounted")
    @Operation(summary = "Calculate discounted amount for fee")
    public ResponseEntity<Object> calculateDiscountedAmount(
            @RequestParam String feeId,
            @RequestParam String discountType) {
        try {
            Optional<Fee> feeOpt = feeService.getFeeById(feeId);
            if (feeOpt.isPresent()) {
                Fee fee = feeOpt.get();
                Double discountedAmount = feeService.calculateDiscountedAmount(fee, discountType);
                Double discount = fee.getAmount() - discountedAmount;

                return ResponseEntity.ok(java.util.Map.of(
                    "originalAmount", fee.getAmount(),
                    "discountedAmount", discountedAmount,
                    "discountAmount", discount,
                    "discountPercentage", fee.getAmount() > 0 ? (discount * 100 / fee.getAmount()) : 0
                ));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get fee statistics")
    public ResponseEntity<Object> getFeeStatistics() {
        Object statistics = feeService.getFeeStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/types")
    @Operation(summary = "Get available fee types")
    public ResponseEntity<List<String>> getFeeTypes() {
        List<String> feeTypes = List.of(
            "TUITION", "LIBRARY", "LAB", "SPORTS", "TRANSPORT",
            "HOSTEL", "EXAMINATION", "MISCELLANEOUS"
        );
        return ResponseEntity.ok(feeTypes);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get available fee categories")
    public ResponseEntity<List<String>> getFeeCategories() {
        List<String> categories = List.of("MONTHLY", "QUARTERLY", "YEARLY", "ONE_TIME");
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Get dashboard statistics for fees")
    public ResponseEntity<Object> getDashboardStats() {
        Object stats = feeService.getFeeStatistics();
        List<Fee> overdueFees = feeService.getOverdueFees();

        return ResponseEntity.ok(java.util.Map.of(
            "feeStatistics", stats,
            "overdueFeesCount", overdueFees.size(),
            "recentFees", feeService.getAllFees().stream().limit(5).toList()
        ));
    }

    @GetMapping("/academic-years")
    @Operation(summary = "Get available academic years")
    public ResponseEntity<List<String>> getAcademicYears() {
        // In a real application, this would come from the database
        List<String> academicYears = List.of("2023-24", "2024-25", "2025-26");
        return ResponseEntity.ok(academicYears);
    }
}
