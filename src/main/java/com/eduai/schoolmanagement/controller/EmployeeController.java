package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.Employee;
import com.eduai.schoolmanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Employee management operations")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    // ====================
    // BASIC CRUD OPERATIONS
    // ====================

    @GetMapping
    @Operation(summary = "Get all employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee-id/{employeeId}")
    @Operation(summary = "Get employee by employee ID")
    public ResponseEntity<Employee> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeByEmployeeId(employeeId);
        return employee.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get employee by email")
    public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {
        Optional<Employee> employee = employeeService.getEmployeeByEmail(email);
        return employee.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new employee")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(createdEmployee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @Valid @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    // ====================
    // FILTERING AND SEARCH
    // ====================

    @GetMapping("/department/{department}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/position/{position}")
    @Operation(summary = "Get employees by position")
    public ResponseEntity<List<Employee>> getEmployeesByPosition(@PathVariable String position) {
        List<Employee> employees = employeeService.getEmployeesByPosition(position);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employment-type/{employmentType}")
    @Operation(summary = "Get employees by employment type")
    public ResponseEntity<List<Employee>> getEmployeesByEmploymentType(@PathVariable String employmentType) {
        List<Employee> employees = employeeService.getEmployeesByEmploymentType(employmentType);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get employees by status")
    public ResponseEntity<List<Employee>> getEmployeesByStatus(@PathVariable String status) {
        List<Employee> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by name")
    public ResponseEntity<Page<Employee>> searchEmployeesByName(
            @RequestParam String name,
            Pageable pageable) {
        Page<Employee> employees = employeeService.searchEmployeesByName(name, pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/manager/{manager}")
    @Operation(summary = "Get employees by reporting manager")
    public ResponseEntity<List<Employee>> getEmployeesByManager(@PathVariable String manager) {
        List<Employee> employees = employeeService.getEmployeesByManager(manager);
        return ResponseEntity.ok(employees);
    }

    // ====================
    // LOGIN MANAGEMENT
    // ====================

    @PostMapping("/{employeeId}/account")
    @Operation(summary = "Create employee account")
    public ResponseEntity<Map<String, Object>> createEmployeeAccount(
            @PathVariable String employeeId,
            @RequestBody Map<String, Object> accountData) {
        Map<String, Object> result = employeeService.createEmployeeAccount(employeeId, accountData);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{employeeId}/account/lock")
    @Operation(summary = "Lock employee account")
    public ResponseEntity<Map<String, Object>> lockEmployeeAccount(@PathVariable String employeeId) {
        Map<String, Object> result = employeeService.lockEmployeeAccount(employeeId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{employeeId}/account/unlock")
    @Operation(summary = "Unlock employee account")
    public ResponseEntity<Map<String, Object>> unlockEmployeeAccount(@PathVariable String employeeId) {
        Map<String, Object> result = employeeService.unlockEmployeeAccount(employeeId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{employeeId}/account/reset-password")
    @Operation(summary = "Reset employee password")
    public ResponseEntity<Map<String, Object>> resetEmployeePassword(@PathVariable String employeeId) {
        Map<String, Object> result = employeeService.resetEmployeePassword(employeeId);
        return ResponseEntity.ok(result);
    }

    // ====================
    // DOCUMENT GENERATION
    // ====================

    @PostMapping("/id-cards")
    @Operation(summary = "Generate employee ID cards")
    public ResponseEntity<Map<String, Object>> generateIDCards(@RequestBody Map<String, Object> criteria) {
        Map<String, Object> result = employeeService.generateIDCards(criteria);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{employeeId}/job-letter")
    @Operation(summary = "Generate job letter")
    public ResponseEntity<Map<String, Object>> generateJobLetter(
            @PathVariable String employeeId,
            @RequestBody Map<String, Object> letterData) {
        Map<String, Object> result = employeeService.generateJobLetter(employeeId, letterData);
        return ResponseEntity.ok(result);
    }

    // ====================
    // BULK OPERATIONS
    // ====================

    @PostMapping("/bulk-create-accounts")
    @Operation(summary = "Bulk create employee accounts")
    public ResponseEntity<Map<String, Object>> bulkCreateAccounts(@RequestBody Map<String, Object> criteria) {
        Map<String, Object> result = employeeService.bulkCreateAccounts(criteria);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bulk-send-credentials")
    @Operation(summary = "Bulk send credentials")
    public ResponseEntity<Map<String, Object>> bulkSendCredentials(@RequestBody Map<String, Object> criteria) {
        Map<String, Object> result = employeeService.bulkSendCredentials(criteria);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bulk-reset-passwords")
    @Operation(summary = "Bulk reset passwords")
    public ResponseEntity<Map<String, Object>> bulkResetPasswords(@RequestBody Map<String, Object> criteria) {
        Map<String, Object> result = employeeService.bulkResetPasswords(criteria);
        return ResponseEntity.ok(result);
    }

    // ====================
    // ANALYTICS AND REPORTS
    // ====================

    @GetMapping("/analytics")
    @Operation(summary = "Get employee analytics")
    public ResponseEntity<Map<String, Object>> getEmployeeAnalytics(
            @RequestParam(defaultValue = "30d") String timeframe) {
        Map<String, Object> analytics = employeeService.getEmployeeAnalytics(timeframe);
        return ResponseEntity.ok(analytics);
    }

    // ====================
    // STATISTICS
    // ====================

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Get employee count by status")
    public ResponseEntity<Long> getEmployeeCountByStatus(@PathVariable String status) {
        long count = employeeService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/department/{department}")
    @Operation(summary = "Get employee count by department")
    public ResponseEntity<Long> getEmployeeCountByDepartment(@PathVariable String department) {
        long count = employeeService.countByDepartment(department);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/employment-type/{employmentType}")
    @Operation(summary = "Get employee count by employment type")
    public ResponseEntity<Long> getEmployeeCountByEmploymentType(@PathVariable String employmentType) {
        long count = employeeService.countByEmploymentType(employmentType);
        return ResponseEntity.ok(count);
    }
}
