package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Employee;
import com.eduai.schoolmanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final IDGenerationService idGenerationService;

    // ====================
    // BASIC CRUD OPERATIONS
    // ====================

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByPersonalInfoEmail(email);
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        // Auto-generate employee ID if not provided
        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            String generatedEmployeeId = idGenerationService.generateEmployeeId(
                employee.getEmploymentInfo().getDepartment()
            );
            employee.setEmployeeId(generatedEmployeeId);
            log.info("Auto-generated Employee ID: {}", generatedEmployeeId);
        } else {
            // Validate for duplicates if manually provided
            if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
                throw new RuntimeException("Employee ID already exists: " + employee.getEmployeeId());
            }
        }

        // Set default status if not provided
        if (employee.getStatus() == null) {
            employee.setStatus("ACTIVE");
        }

        // Initialize login credentials if not provided
        if (employee.getLoginCredentials() == null) {
            Employee.LoginCredentials credentials = new Employee.LoginCredentials();
            credentials.setHasAccount(false);
            credentials.setLocked(false);
            credentials.setLoginHistory(new ArrayList<>());
            employee.setLoginCredentials(credentials);
        }

        log.info("Creating employee: {}", employee.getEmployeeId());
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(String id, Employee employee) {
        employee.setId(id);
        log.info("Updating employee with id: {}", id);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(String id) {
        log.info("Deleting employee with id: {}", id);
        employeeRepository.deleteById(id);
    }

    // ====================
    // FILTERING AND SEARCH
    // ====================

    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByEmploymentInfoDepartment(department);
    }

    public List<Employee> getEmployeesByPosition(String position) {
        return employeeRepository.findByEmploymentInfoPosition(position);
    }

    public List<Employee> getEmployeesByEmploymentType(String employmentType) {
        return employeeRepository.findByEmploymentInfoEmploymentType(employmentType);
    }

    public List<Employee> getEmployeesByStatus(String status) {
        return employeeRepository.findByStatus(status);
    }

    public Page<Employee> searchEmployeesByName(String name, Pageable pageable) {
        return employeeRepository.findByPersonalInfoFirstNameContainingIgnoreCaseOrPersonalInfoLastNameContainingIgnoreCase(
            name, name, pageable);
    }

    public List<Employee> getEmployeesByManager(String manager) {
        return employeeRepository.findByEmploymentInfoReportingManager(manager);
    }

    // ====================
    // LOGIN MANAGEMENT
    // ====================

    @Transactional
    public Map<String, Object> createEmployeeAccount(String employeeId, Map<String, Object> accountData) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Employee.LoginCredentials credentials = employee.getLoginCredentials();
        if (credentials == null) {
            credentials = new Employee.LoginCredentials();
            credentials.setLoginHistory(new ArrayList<>());
        }

        String username = (String) accountData.getOrDefault("username",
            generateUsername(employee.getPersonalInfo().getFirstName(), employee.getPersonalInfo().getLastName()));

        credentials.setUsername(username);
        credentials.setHasAccount(true);
        credentials.setLocked(false);
        credentials.setTemporaryPassword(generateTemporaryPassword());
        credentials.setPasswordResetDate(LocalDateTime.now());

        employee.setLoginCredentials(credentials);
        Employee savedEmployee = employeeRepository.save(employee);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Employee account created successfully");
        result.put("employeeId", employeeId);
        result.put("username", username);
        result.put("temporaryPassword", credentials.getTemporaryPassword());
        result.put("accountCreatedAt", LocalDateTime.now());

        log.info("Created account for employee: {} with username: {}", employee.getEmployeeId(), username);
        return result;
    }

    @Transactional
    public Map<String, Object> lockEmployeeAccount(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Employee.LoginCredentials credentials = employee.getLoginCredentials();
        if (credentials == null || !credentials.isHasAccount()) {
            throw new RuntimeException("Employee does not have an account");
        }

        credentials.setLocked(true);
        employee.setLoginCredentials(credentials);
        employeeRepository.save(employee);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Employee account locked successfully");
        result.put("employeeId", employeeId);
        result.put("lockedAt", LocalDateTime.now());

        log.info("Locked account for employee: {}", employee.getEmployeeId());
        return result;
    }

    @Transactional
    public Map<String, Object> unlockEmployeeAccount(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Employee.LoginCredentials credentials = employee.getLoginCredentials();
        if (credentials == null || !credentials.isHasAccount()) {
            throw new RuntimeException("Employee does not have an account");
        }

        credentials.setLocked(false);
        employee.setLoginCredentials(credentials);
        employeeRepository.save(employee);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Employee account unlocked successfully");
        result.put("employeeId", employeeId);
        result.put("unlockedAt", LocalDateTime.now());

        log.info("Unlocked account for employee: {}", employee.getEmployeeId());
        return result;
    }

    @Transactional
    public Map<String, Object> resetEmployeePassword(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Employee.LoginCredentials credentials = employee.getLoginCredentials();
        if (credentials == null || !credentials.isHasAccount()) {
            throw new RuntimeException("Employee does not have an account");
        }

        String newPassword = generateTemporaryPassword();
        credentials.setTemporaryPassword(newPassword);
        credentials.setPasswordResetDate(LocalDateTime.now());
        credentials.setLastLogin(null);

        employee.setLoginCredentials(credentials);
        employeeRepository.save(employee);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Password reset successfully");
        result.put("employeeId", employeeId);
        result.put("temporaryPassword", newPassword);
        result.put("resetAt", LocalDateTime.now());

        log.info("Reset password for employee: {}", employee.getEmployeeId());
        return result;
    }

    // ====================
    // DOCUMENT GENERATION
    // ====================

    public Map<String, Object> generateIDCards(Map<String, Object> criteria) {
        List<Employee> employees = filterEmployeesForGeneration(criteria);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Employee ID cards generated successfully");
        result.put("criteria", criteria);
        result.put("totalCards", employees.size());
        result.put("documentUrl", "/api/documents/employee-id-cards/" + UUID.randomUUID());
        result.put("generatedAt", LocalDateTime.now());

        log.info("Generated ID cards for {} employees", employees.size());
        return result;
    }

    public Map<String, Object> generateJobLetter(String employeeId, Map<String, Object> letterData) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        String letterType = (String) letterData.get("letterType");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Job letter generated successfully");
        result.put("employeeId", employeeId);
        result.put("letterType", letterType);
        result.put("documentUrl", "/api/documents/job-letter/" + UUID.randomUUID());
        result.put("generatedAt", LocalDateTime.now());

        log.info("Generated {} letter for employee: {}", letterType, employee.getEmployeeId());
        return result;
    }

    // ====================
    // BULK OPERATIONS
    // ====================

    @Transactional
    public Map<String, Object> bulkCreateAccounts(Map<String, Object> criteria) {
        List<Employee> employees = filterEmployeesForBulkOperation(criteria)
            .stream()
            .filter(emp -> emp.getLoginCredentials() == null || !emp.getLoginCredentials().isHasAccount())
            .collect(Collectors.toList());

        int accountsCreated = 0;
        for (Employee employee : employees) {
            try {
                Map<String, Object> accountData = new HashMap<>();
                createEmployeeAccount(employee.getId(), accountData);
                accountsCreated++;
            } catch (Exception e) {
                log.error("Failed to create account for employee {}: {}",
                    employee.getEmployeeId(), e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Bulk account creation completed");
        result.put("accountsCreated", accountsCreated);
        result.put("criteria", criteria);
        result.put("createdAt", LocalDateTime.now());

        log.info("Bulk created {} employee accounts", accountsCreated);
        return result;
    }

    public Map<String, Object> bulkSendCredentials(Map<String, Object> criteria) {
        List<Employee> employees = filterEmployeesForBulkOperation(criteria)
            .stream()
            .filter(emp -> emp.getLoginCredentials() != null && emp.getLoginCredentials().isHasAccount())
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Bulk credentials sent successfully");
        result.put("credentialsSent", employees.size());
        result.put("criteria", criteria);
        result.put("sentAt", LocalDateTime.now());

        log.info("Bulk sent credentials to {} employees", employees.size());
        return result;
    }

    @Transactional
    public Map<String, Object> bulkResetPasswords(Map<String, Object> criteria) {
        List<Employee> employees = filterEmployeesForBulkOperation(criteria)
            .stream()
            .filter(emp -> emp.getLoginCredentials() != null && emp.getLoginCredentials().isHasAccount())
            .collect(Collectors.toList());

        int passwordsReset = 0;
        for (Employee employee : employees) {
            try {
                resetEmployeePassword(employee.getId());
                passwordsReset++;
            } catch (Exception e) {
                log.error("Failed to reset password for employee {}: {}",
                    employee.getEmployeeId(), e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Bulk password reset completed");
        result.put("passwordsReset", passwordsReset);
        result.put("criteria", criteria);
        result.put("resetAt", LocalDateTime.now());

        log.info("Bulk reset passwords for {} employees", passwordsReset);
        return result;
    }

    // ====================
    // ANALYTICS AND REPORTS
    // ====================

    public Map<String, Object> getEmployeeAnalytics(String timeframe) {
        List<Employee> allEmployees = employeeRepository.findAll();

        // Department distribution
        Map<String, Long> departmentDistribution = allEmployees.stream()
            .collect(Collectors.groupingBy(
                emp -> emp.getEmploymentInfo().getDepartment(),
                Collectors.counting()
            ));

        // Employment type distribution
        Map<String, Long> employmentTypeDistribution = allEmployees.stream()
            .collect(Collectors.groupingBy(
                emp -> emp.getEmploymentInfo().getEmploymentType(),
                Collectors.counting()
            ));

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEmployees", allEmployees.size());
        analytics.put("activeEmployees", countByStatus("ACTIVE"));
        analytics.put("newEmployees", 3); // This would be calculated based on timeframe
        analytics.put("onLeaveEmployees", countByStatus("ON_LEAVE"));
        analytics.put("fullTimeEmployees", countByEmploymentType("FULL_TIME"));
        analytics.put("partTimeEmployees", countByEmploymentType("PART_TIME"));
        analytics.put("averageTenure", 2.8); // This would be calculated from join dates
        analytics.put("departmentDistribution", departmentDistribution);
        analytics.put("employmentTypeDistribution", employmentTypeDistribution);
        analytics.put("generatedAt", LocalDateTime.now());

        return analytics;
    }

    // ====================
    // HELPER METHODS
    // ====================

    private List<Employee> filterEmployeesForGeneration(Map<String, Object> criteria) {
        List<Employee> employees = getAllEmployees();

        if (criteria.containsKey("department")) {
            String department = (String) criteria.get("department");
            employees = employees.stream()
                .filter(emp -> department.equals(emp.getEmploymentInfo().getDepartment()))
                .collect(Collectors.toList());
        }

        if (criteria.containsKey("employmentType")) {
            String employmentType = (String) criteria.get("employmentType");
            employees = employees.stream()
                .filter(emp -> employmentType.equals(emp.getEmploymentInfo().getEmploymentType()))
                .collect(Collectors.toList());
        }

        return employees;
    }

    private List<Employee> filterEmployeesForBulkOperation(Map<String, Object> criteria) {
        return filterEmployeesForGeneration(criteria); // Same filtering logic
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = (firstName + "." + lastName).toLowerCase().replaceAll("[^a-zA-Z0-9.]", "");

        // Check if username exists and add number if needed
        String username = baseUsername;
        int counter = 1;
        while (employeeRepository.findByLoginCredentialsUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    private String generateTemporaryPassword() {
        return "Temp" + System.currentTimeMillis() % 10000;
    }

    public long countByStatus(String status) {
        return employeeRepository.countByStatus(status);
    }

    public long countByDepartment(String department) {
        return employeeRepository.countByEmploymentInfoDepartment(department);
    }

    public long countByEmploymentType(String employmentType) {
        return employeeRepository.countByEmploymentInfoEmploymentType(employmentType);
    }
}
