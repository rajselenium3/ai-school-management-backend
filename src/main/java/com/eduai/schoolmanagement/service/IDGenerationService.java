package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.IDConfiguration;
import com.eduai.schoolmanagement.repository.IDConfigurationRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
import com.eduai.schoolmanagement.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class IDGenerationService {

    private final IDConfigurationRepository idConfigRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    // Thread-safe locks for each ID type to prevent race conditions
    private final ReentrantLock studentIdLock = new ReentrantLock();
    private final ReentrantLock admissionNumberLock = new ReentrantLock();
    private final ReentrantLock rollNumberLock = new ReentrantLock();
    private final ReentrantLock employeeIdLock = new ReentrantLock();

    /**
     * Generate Student ID based on grade and section
     */
    @Transactional
    public String generateStudentId(String grade, String section) {
        studentIdLock.lock();
        try {
            IDConfiguration config = getActiveConfiguration("STUDENT_ID");
            String generatedId = generateIdFromConfig(config, grade, section);

            // Check for duplicates
            while (studentRepository.existsByStudentId(generatedId)) {
                config.setCurrentCounter(config.getCurrentCounter() + 1);
                idConfigRepository.save(config);
                generatedId = generateIdFromConfig(config, grade, section);
            }

            // Increment counter for next use
            config.setCurrentCounter(config.getCurrentCounter() + 1);
            idConfigRepository.save(config);

            log.info("Generated Student ID: {} for Grade: {}, Section: {}", generatedId, grade, section);
            return generatedId;

        } finally {
            studentIdLock.unlock();
        }
    }

    /**
     * Generate Admission Number
     */
    @Transactional
    public String generateAdmissionNumber() {
        admissionNumberLock.lock();
        try {
            IDConfiguration config = getActiveConfiguration("ADMISSION_NUMBER");
            String generatedId = generateIdFromConfig(config, null, null);

            // Check for duplicates
            while (studentRepository.existsByAdmissionNumber(generatedId)) {
                config.setCurrentCounter(config.getCurrentCounter() + 1);
                idConfigRepository.save(config);
                generatedId = generateIdFromConfig(config, null, null);
            }

            // Increment counter for next use
            config.setCurrentCounter(config.getCurrentCounter() + 1);
            idConfigRepository.save(config);

            log.info("Generated Admission Number: {}", generatedId);
            return generatedId;

        } finally {
            admissionNumberLock.unlock();
        }
    }

    /**
     * Generate Roll Number for a specific grade and section
     */
    @Transactional
    public String generateRollNumber(String grade, String section) {
        rollNumberLock.lock();
        try {
            // For roll numbers, we need to find the next available number for the specific grade/section
            long maxRollNumber = studentRepository.findMaxRollNumberByGradeAndSection(grade, section);
            String nextRollNumber = String.format("%03d", maxRollNumber + 1);

            log.info("Generated Roll Number: {} for Grade: {}, Section: {}", nextRollNumber, grade, section);
            return nextRollNumber;

        } finally {
            rollNumberLock.unlock();
        }
    }

    /**
     * Generate Employee ID
     */
    @Transactional
    public String generateEmployeeId() {
        employeeIdLock.lock();
        try {
            IDConfiguration config = getActiveConfiguration("EMPLOYEE_ID");
            String generatedId = generateIdFromConfig(config, null, null);

            // Check for duplicates (assuming we have an employee repository or teacher repository)
            while (teacherRepository.existsByEmployeeId(generatedId)) {
                config.setCurrentCounter(config.getCurrentCounter() + 1);
                idConfigRepository.save(config);
                generatedId = generateIdFromConfig(config, null, null);
            }

            // Increment counter for next use
            config.setCurrentCounter(config.getCurrentCounter() + 1);
            idConfigRepository.save(config);

            log.info("Generated Employee ID: {}", generatedId);
            return generatedId;

        } finally {
            employeeIdLock.unlock();
        }
    }

    /**
     * Validate if an ID already exists in the system
     */
    public boolean isIdDuplicate(String idType, String id) {
        switch (idType.toUpperCase()) {
            case "STUDENT_ID":
                return studentRepository.existsByStudentId(id);
            case "ADMISSION_NUMBER":
                return studentRepository.existsByAdmissionNumber(id);
            case "EMPLOYEE_ID":
                return teacherRepository.existsByEmployeeId(id);
            default:
                return false;
        }
    }

    /**
     * Get preview of next ID that would be generated
     */
    public String previewNextId(String idType, String grade, String section) {
        IDConfiguration config = getActiveConfiguration(idType);
        return generateIdFromConfig(config, grade, section);
    }

    /**
     * Reset counter for a specific ID type (admin function)
     */
    @Transactional
    public void resetCounter(String idType, Long newCounter) {
        Optional<IDConfiguration> configOpt = idConfigRepository.findByIdTypeAndActive(idType, true);
        if (configOpt.isPresent()) {
            IDConfiguration config = configOpt.get();
            config.setCurrentCounter(newCounter);
            idConfigRepository.save(config);
            log.info("Reset counter for {} to {}", idType, newCounter);
        } else {
            throw new RuntimeException("ID Configuration not found for type: " + idType);
        }
    }

    /**
     * Get active configuration for ID type
     */
    private IDConfiguration getActiveConfiguration(String idType) {
        return idConfigRepository.findByIdTypeAndActive(idType, true)
                .orElseThrow(() -> new RuntimeException("No active configuration found for ID type: " + idType));
    }

    /**
     * Generate ID from configuration format
     */
    private String generateIdFromConfig(IDConfiguration config, String grade, String section) {
        String format = config.getFormat();
        String result = format;

        // Replace placeholders
        if (config.getIncludeYear() && result.contains("{YEAR}")) {
            result = result.replace("{YEAR}", String.valueOf(Year.now().getValue()));
        }

        if (config.getIncludeGradeSection() && grade != null) {
            result = result.replace("{GRADE}", grade);
            if (section != null) {
                result = result.replace("{SECTION}", section);
            }
        }

        // Handle counter with padding
        if (result.contains("{COUNTER:")) {
            String counterPattern = result.substring(result.indexOf("{COUNTER:"), result.indexOf("}", result.indexOf("{COUNTER:")) + 1);
            String paddingStr = counterPattern.substring(counterPattern.indexOf(':') + 1, counterPattern.indexOf('}'));
            int padding = Integer.parseInt(paddingStr);
            String formattedCounter = String.format("%0" + padding + "d", config.getCurrentCounter() + 1);
            result = result.replace(counterPattern, formattedCounter);
        } else if (result.contains("{COUNTER}")) {
            result = result.replace("{COUNTER}", String.valueOf(config.getCurrentCounter() + 1));
        }

        return result;
    }

    /**
     * Initialize default configurations if they don't exist
     */
    @Transactional
    public void initializeDefaultConfigurations() {
        if (!idConfigRepository.existsByIdType("STUDENT_ID")) {
            idConfigRepository.save(IDConfiguration.createStudentIDConfig());
            log.info("Initialized default Student ID configuration");
        }

        if (!idConfigRepository.existsByIdType("ADMISSION_NUMBER")) {
            idConfigRepository.save(IDConfiguration.createAdmissionNumberConfig());
            log.info("Initialized default Admission Number configuration");
        }

        if (!idConfigRepository.existsByIdType("ROLL_NUMBER")) {
            idConfigRepository.save(IDConfiguration.createRollNumberConfig());
            log.info("Initialized default Roll Number configuration");
        }

        if (!idConfigRepository.existsByIdType("EMPLOYEE_ID")) {
            idConfigRepository.save(IDConfiguration.createEmployeeIDConfig());
            log.info("Initialized default Employee ID configuration");
        }
    }

	public String generateEmployeeId(String department) {
		// TODO Auto-generated method stub
		return null;
	}
}
