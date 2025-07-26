package com.eduai.schoolmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Teacher;
import com.eduai.schoolmanagement.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(String id) {
        return teacherRepository.findById(id);
    }

    public Optional<Teacher> getTeacherByEmployeeId(String employeeId) {
        return teacherRepository.findByEmployeeId(employeeId);
    }

    public Optional<Teacher> getTeacherByEmail(String email) {
        return teacherRepository.findByUserEmail(email);
    }

    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }

    public List<Teacher> getTeachersByEmploymentType(String employmentType) {
        return teacherRepository.findByEmploymentType(employmentType);
    }

    public List<Teacher> getTeachersBySubject(String subject) {
        return teacherRepository.findBySubjectsHandled(subject);
    }

    public List<Teacher> searchTeachersByName(String name) {
        return teacherRepository.searchByNameEmailOrEmployeeId(name);
    }

    public Page<Teacher> searchTeachersByName(String name, Pageable pageable) {
        return teacherRepository.searchByNameEmailOrEmployeeIdPageable(name, pageable);
    }

    public Page<Teacher> getTeachersByFirstName(String firstName, Pageable pageable) {
        return teacherRepository.findByUserFirstNameContainingIgnoreCase(firstName, pageable);
    }

    public Page<Teacher> getTeachersByLastName(String lastName, Pageable pageable) {
        return teacherRepository.findByUserLastNameContainingIgnoreCase(lastName, pageable);
    }

    public List<Teacher> getHighPerformingTeachers(double performanceThreshold) {
        return teacherRepository.findByPerformanceScoreGreaterThan(performanceThreshold);
    }

    public Teacher saveTeacher(Teacher teacher) {
        log.info("Saving teacher: {}", teacher.getEmployeeId());
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(String id, Teacher teacher) {
        teacher.setId(id);
        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(String id) {
        log.info("Deleting teacher with id: {}", id);
        teacherRepository.deleteById(id);
    }

    public long getTeacherCountByDepartment(String department) {
        return teacherRepository.countByDepartment(department);
    }

    public long getTeacherCountByEmploymentType(String employmentType) {
        return teacherRepository.countByEmploymentType(employmentType);
    }

    // Additional enhanced methods using the new repository methods

    public List<Teacher> getActiveTeachers() {
        return teacherRepository.findActiveTeachers();
    }

    public List<Teacher> getContractTeachers() {
        return teacherRepository.findContractTeachers();
    }

    public List<Teacher> getTopPerformers() {
        return teacherRepository.findTopPerformers();
    }

    public List<Teacher> getHighestRatedTeachers() {
        return teacherRepository.findHighestRatedTeachers();
    }

    public List<Teacher> getTeachersByClassAssigned(String className) {
        return teacherRepository.findByClassesAssigned(className);
    }

    public List<Teacher> getTeachersByPerformanceAndRating(double minPerformance, double minRating) {
        return teacherRepository.findByPerformanceAndRating(minPerformance, minRating);
    }

    public List<Teacher> getTeachersWithLowWorkload(int maxClasses) {
        return teacherRepository.findTeachersWithLowWorkload(maxClasses);
    }

    public List<Teacher> getTeachersByQualification(String qualification) {
        return teacherRepository.findByQualificationsContaining(qualification);
    }

    public List<Teacher> getTeachersBySpecialization(String specialization) {
        return teacherRepository.findBySpecializationContaining(specialization);
    }

    public List<Teacher> getTeachersBySalaryRange(double minSalary, double maxSalary) {
        return teacherRepository.findBySalaryRange(minSalary, maxSalary);
    }

    public List<Teacher> getRecentJoiners(LocalDate fromDate) {
        return teacherRepository.findRecentJoiners(fromDate);
    }

    public List<Teacher> getSeniorTeachers(LocalDate beforeDate) {
        return teacherRepository.findSeniorTeachers(beforeDate);
    }

    public List<Teacher> getTeachersJoinedBetween(LocalDate startDate, LocalDate endDate) {
        return teacherRepository.findByJoiningDateBetween(startDate, endDate);
    }

    // Validation and business logic methods

    public Teacher createTeacher(Teacher teacher) {
        log.info("Creating new teacher: {}", teacher.getEmployeeId());

        // Validate employee ID uniqueness
        if (teacherRepository.existsByEmployeeId(teacher.getEmployeeId())) {
            throw new IllegalArgumentException("Teacher with employee ID " + teacher.getEmployeeId() + " already exists");
        }

        // Validate email uniqueness
        if (teacher.getUser() != null && teacher.getUser().getEmail() != null) {
            if (teacherRepository.existsByUserEmail(teacher.getUser().getEmail())) {
                throw new IllegalArgumentException("Teacher with email " + teacher.getUser().getEmail() + " already exists");
            }
        }

        // Set default values
        if (teacher.getJoiningDate() == null) {
            teacher.setJoiningDate(LocalDate.now());
        }
        if (teacher.getEmploymentType() == null) {
            teacher.setEmploymentType("FULL_TIME");
        }
        if (teacher.getPerformanceScore() == 0) {
            teacher.setPerformanceScore(75.0); // Default performance score
        }
        if (teacher.getStudentRating() == 0) {
            teacher.setStudentRating(4.0); // Default rating
        }

        return teacherRepository.save(teacher);
    }

    public boolean existsByEmployeeId(String employeeId) {
        return teacherRepository.existsByEmployeeId(employeeId);
    }

    public boolean existsByEmail(String email) {
        return teacherRepository.existsByUserEmail(email);
    }

    public long getTotalTeachersCount() {
        return teacherRepository.count();
    }

    public long getTeacherCountByDesignation(String designation) {
        return teacherRepository.countByDesignation(designation);
    }
}
