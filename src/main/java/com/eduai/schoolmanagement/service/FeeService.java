package com.eduai.schoolmanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Fee;
import com.eduai.schoolmanagement.repository.FeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeService {

    private final FeeRepository feeRepository;

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public Optional<Fee> getFeeById(String id) {
        return feeRepository.findById(id);
    }

    public List<Fee> getFeesByType(String feeType) {
        return feeRepository.findByFeeType(feeType);
    }

    public List<Fee> getFeesByCategory(String category) {
        return feeRepository.findByCategory(category);
    }

    public List<Fee> getFeesByAcademicYear(String academicYear) {
        return feeRepository.findByAcademicYear(academicYear);
    }

    public List<Fee> getActiveFeesForGradeAndYear(String grade, String academicYear) {
        return feeRepository.findActiveFeesForGradeAndYear(academicYear, grade);
    }

    public List<Fee> getApplicableFeesForStudent(String grade, String department, String academicYear) {
        List<Fee> gradeFees = feeRepository.findByApplicableGrade(grade);
        List<Fee> departmentFees = feeRepository.findByApplicableDepartment(department);
        List<Fee> yearFees = feeRepository.findActiveFeesForAcademicYear(academicYear);

        // Combine and filter for applicable fees
        return gradeFees.stream()
            .filter(fee -> fee.getAcademicYear().equals(academicYear))
            .filter(fee -> fee.getStatus().equals("ACTIVE"))
            .filter(fee -> fee.getApplicableGrades() == null ||
                          fee.getApplicableGrades().isEmpty() ||
                          fee.getApplicableGrades().contains(grade))
            .filter(fee -> fee.getApplicableDepartments() == null ||
                          fee.getApplicableDepartments().isEmpty() ||
                          fee.getApplicableDepartments().contains(department))
            .collect(Collectors.toList());
    }

    public List<Fee> getOverdueFees() {
        return feeRepository.findOverdueFees(LocalDate.now());
    }

    public List<Fee> getScholarshipEligibleFees() {
        return feeRepository.findScholarshipEligibleFees();
    }

    public Fee saveFee(Fee fee) {
        log.info("Saving fee: {}", fee.getFeeName());

        // Set default values
        if (fee.getStatus() == null) {
            fee.setStatus("ACTIVE");
        }

        if (fee.getCreatedAt() == null) {
            fee.setCreatedAt(LocalDateTime.now());
        }

        return feeRepository.save(fee);
    }

    public Fee updateFee(String id, Fee fee) {
        fee.setId(id);
        log.info("Updating fee with id: {}", id);
        return feeRepository.save(fee);
    }

    public void deleteFee(String id) {
        log.info("Deleting fee with id: {}", id);
        feeRepository.deleteById(id);
    }

    public Fee activateFee(String id) {
        Optional<Fee> feeOpt = feeRepository.findById(id);
        if (feeOpt.isPresent()) {
            Fee fee = feeOpt.get();
            fee.setStatus("ACTIVE");
            return feeRepository.save(fee);
        }
        throw new RuntimeException("Fee not found with id: " + id);
    }

    public Fee deactivateFee(String id) {
        Optional<Fee> feeOpt = feeRepository.findById(id);
        if (feeOpt.isPresent()) {
            Fee fee = feeOpt.get();
            fee.setStatus("INACTIVE");
            return feeRepository.save(fee);
        }
        throw new RuntimeException("Fee not found with id: " + id);
    }

    public Double calculateTotalFeeForStudent(String grade, String department, String academicYear) {
        List<Fee> applicableFees = getApplicableFeesForStudent(grade, department, academicYear);
        return applicableFees.stream()
            .filter(Fee::isMandatory)
            .mapToDouble(Fee::getAmount)
            .sum();
    }

    public Double calculateDiscountedAmount(Fee fee, String discountType) {
        if (fee.getDiscountRules() == null || fee.getDiscountRules().isEmpty()) {
            return fee.getAmount();
        }

        return fee.getDiscountRules().stream()
            .filter(rule -> rule.getDiscountType().equals(discountType))
            .filter(Fee.DiscountRule::isActive)
            .filter(rule -> isDiscountRuleValid(rule))
            .findFirst()
            .map(rule -> calculateDiscountAmount(fee.getAmount(), rule))
            .orElse(fee.getAmount());
    }

    private boolean isDiscountRuleValid(Fee.DiscountRule rule) {
        LocalDate now = LocalDate.now();
        return (rule.getValidFrom() == null || !now.isBefore(rule.getValidFrom())) &&
               (rule.getValidTo() == null || !now.isAfter(rule.getValidTo()));
    }

    private Double calculateDiscountAmount(Double amount, Fee.DiscountRule rule) {
        if ("PERCENTAGE".equals(rule.getDiscountType())) {
            return amount - (amount * rule.getDiscountValue() / 100);
        } else if ("FIXED_AMOUNT".equals(rule.getDiscountType())) {
            return Math.max(0, amount - rule.getDiscountValue());
        }
        return amount;
    }

    public List<Fee> searchFees(String searchTerm) {
        return feeRepository.findByFeeNameContainingIgnoreCase(searchTerm);
    }

    public Object getFeeStatistics() {
        long totalFees = feeRepository.count();
        long activeFees = feeRepository.countByStatus("ACTIVE");
        long inactiveFees = feeRepository.countByStatus("INACTIVE");

        return java.util.Map.of(
            "totalFees", totalFees,
            "activeFees", activeFees,
            "inactiveFees", inactiveFees,
            "feeTypeBreakdown", getFeeTypeBreakdown(),
            "categoryBreakdown", getCategoryBreakdown()
        );
    }

    private Object getFeeTypeBreakdown() {
        return java.util.Map.of(
            "TUITION", feeRepository.countByFeeType("TUITION"),
            "LIBRARY", feeRepository.countByFeeType("LIBRARY"),
            "LAB", feeRepository.countByFeeType("LAB"),
            "SPORTS", feeRepository.countByFeeType("SPORTS"),
            "TRANSPORT", feeRepository.countByFeeType("TRANSPORT"),
            "HOSTEL", feeRepository.countByFeeType("HOSTEL"),
            "EXAMINATION", feeRepository.countByFeeType("EXAMINATION"),
            "MISCELLANEOUS", feeRepository.countByFeeType("MISCELLANEOUS")
        );
    }

    private Object getCategoryBreakdown() {
        return java.util.Map.of(
            "MONTHLY", feeRepository.findByCategory("MONTHLY").size(),
            "QUARTERLY", feeRepository.findByCategory("QUARTERLY").size(),
            "YEARLY", feeRepository.findByCategory("YEARLY").size(),
            "ONE_TIME", feeRepository.findByCategory("ONE_TIME").size()
        );
    }

    public List<Fee> createBulkFees(List<Fee> fees) {
        log.info("Creating {} fees in bulk", fees.size());
        return feeRepository.saveAll(fees);
    }

    public Fee duplicateFee(String feeId, String newName, String newAcademicYear) {
        Optional<Fee> originalOpt = feeRepository.findById(feeId);
        if (originalOpt.isPresent()) {
            Fee original = originalOpt.get();
            Fee duplicate = new Fee();

            // Copy all properties except ID
            duplicate.setFeeName(newName);
            duplicate.setDescription(original.getDescription());
            duplicate.setAmount(original.getAmount());
            duplicate.setFeeType(original.getFeeType());
            duplicate.setCategory(original.getCategory());
            duplicate.setAcademicYear(newAcademicYear);
            duplicate.setApplicableGrades(original.getApplicableGrades());
            duplicate.setApplicableDepartments(original.getApplicableDepartments());
            duplicate.setMandatory(original.isMandatory());
            duplicate.setDueDate(original.getDueDate());
            duplicate.setLateFeeDueDate(original.getLateFeeDueDate());
            duplicate.setLateFeeAmount(original.getLateFeeAmount());
            duplicate.setLateFeePercentage(original.getLateFeePercentage());
            duplicate.setDiscountRules(original.getDiscountRules());
            duplicate.setScholarshipEligible(original.isScholarshipEligible());
            duplicate.setStatus("ACTIVE");

            return feeRepository.save(duplicate);
        }
        throw new RuntimeException("Fee not found with id: " + feeId);
    }
}
