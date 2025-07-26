package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.*;
import com.eduai.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasterDataService {

    private final GradeLevelRepository gradeLevelRepository;
    private final SectionRepository sectionRepository;
    private final DepartmentRepository departmentRepository;
    private final EmploymentTypeRepository employmentTypeRepository;
    private final SemesterRepository semesterRepository;

    // ====================
    // GRADE LEVEL METHODS
    // ====================

    public List<GradeLevel> getAllGradeLevels() {
        return gradeLevelRepository.findByActiveTrueOrderByDisplayOrder();
    }

    public Optional<GradeLevel> getGradeLevelByCode(String gradeCode) {
        return gradeLevelRepository.findByGradeCodeAndActive(gradeCode, true);
    }

    public GradeLevel createGradeLevel(GradeLevel gradeLevel) {
        if (gradeLevelRepository.existsByGradeCodeAndActive(gradeLevel.getGradeCode(), true)) {
            throw new RuntimeException("Grade level with code " + gradeLevel.getGradeCode() + " already exists");
        }
        gradeLevel.setActive(true);
        return gradeLevelRepository.save(gradeLevel);
    }

    public GradeLevel updateGradeLevel(String id, GradeLevel gradeLevelDetails) {
        return gradeLevelRepository.findById(id)
                .map(gradeLevel -> {
                    gradeLevel.setGradeName(gradeLevelDetails.getGradeName());
                    gradeLevel.setGradeLevel(gradeLevelDetails.getGradeLevel());
                    gradeLevel.setDescription(gradeLevelDetails.getDescription());
                    gradeLevel.setMinimumAge(gradeLevelDetails.getMinimumAge());
                    gradeLevel.setMaximumAge(gradeLevelDetails.getMaximumAge());
                    gradeLevel.setDisplayOrder(gradeLevelDetails.getDisplayOrder());
                    return gradeLevelRepository.save(gradeLevel);
                })
                .orElseThrow(() -> new RuntimeException("Grade level not found with id: " + id));
    }

    public void deleteGradeLevel(String id) {
        gradeLevelRepository.findById(id)
                .ifPresentOrElse(
                        gradeLevel -> {
                            gradeLevel.setActive(false);
                            gradeLevelRepository.save(gradeLevel);
                        },
                        () -> { throw new RuntimeException("Grade level not found with id: " + id); }
                );
    }

    // ====================
    // SECTION METHODS
    // ====================

    public List<Section> getAllSections() {
        return sectionRepository.findByActiveTrueOrderByDisplayOrder();
    }

    public Optional<Section> getSectionByCode(String sectionCode) {
        return sectionRepository.findBySectionCodeAndActive(sectionCode, true);
    }

    public Section createSection(Section section) {
        if (sectionRepository.existsBySectionCodeAndActive(section.getSectionCode(), true)) {
            throw new RuntimeException("Section with code " + section.getSectionCode() + " already exists");
        }
        section.setActive(true);
        if (section.getCurrentEnrollment() == null) {
            section.setCurrentEnrollment(0);
        }
        return sectionRepository.save(section);
    }

    public Section updateSection(String id, Section sectionDetails) {
        return sectionRepository.findById(id)
                .map(section -> {
                    section.setSectionName(sectionDetails.getSectionName());
                    section.setDescription(sectionDetails.getDescription());
                    section.setMaxCapacity(sectionDetails.getMaxCapacity());
                    section.setDisplayOrder(sectionDetails.getDisplayOrder());
                    section.setColor(sectionDetails.getColor());
                    return sectionRepository.save(section);
                })
                .orElseThrow(() -> new RuntimeException("Section not found with id: " + id));
    }

    public void deleteSection(String id) {
        sectionRepository.findById(id)
                .ifPresentOrElse(
                        section -> {
                            section.setActive(false);
                            sectionRepository.save(section);
                        },
                        () -> { throw new RuntimeException("Section not found with id: " + id); }
                );
    }

    // ====================
    // DEPARTMENT METHODS
    // ====================

    public List<Department> getAllDepartments() {
        return departmentRepository.findByActiveTrueOrderByDisplayOrder();
    }

    public Optional<Department> getDepartmentByCode(String departmentCode) {
        return departmentRepository.findByDepartmentCodeAndActive(departmentCode, true);
    }

    public Department createDepartment(Department department) {
        if (departmentRepository.existsByDepartmentCodeAndActive(department.getDepartmentCode(), true)) {
            throw new RuntimeException("Department with code " + department.getDepartmentCode() + " already exists");
        }
        department.setActive(true);
        if (department.getCurrentTeachers() == null) {
            department.setCurrentTeachers(0);
        }
        return departmentRepository.save(department);
    }

    public Department updateDepartment(String id, Department departmentDetails) {
        return departmentRepository.findById(id)
                .map(department -> {
                    department.setDepartmentName(departmentDetails.getDepartmentName());
                    department.setDescription(departmentDetails.getDescription());
                    department.setHeadOfDepartment(departmentDetails.getHeadOfDepartment());
                    department.setContactEmail(departmentDetails.getContactEmail());
                    department.setContactPhone(departmentDetails.getContactPhone());
                    department.setLocation(departmentDetails.getLocation());
                    department.setColor(departmentDetails.getColor());
                    department.setDisplayOrder(departmentDetails.getDisplayOrder());
                    department.setMaxTeachers(departmentDetails.getMaxTeachers());
                    return departmentRepository.save(department);
                })
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    public void deleteDepartment(String id) {
        departmentRepository.findById(id)
                .ifPresentOrElse(
                        department -> {
                            department.setActive(false);
                            departmentRepository.save(department);
                        },
                        () -> { throw new RuntimeException("Department not found with id: " + id); }
                );
    }

    // ====================
    // EMPLOYMENT TYPE METHODS
    // ====================

    public List<EmploymentType> getAllEmploymentTypes() {
        return employmentTypeRepository.findByActiveTrueOrderByDisplayOrder();
    }

    public Optional<EmploymentType> getEmploymentTypeByCode(String typeCode) {
        return employmentTypeRepository.findByTypeCodeAndActive(typeCode, true);
    }

    public EmploymentType createEmploymentType(EmploymentType employmentType) {
        if (employmentTypeRepository.existsByTypeCodeAndActive(employmentType.getTypeCode(), true)) {
            throw new RuntimeException("Employment type with code " + employmentType.getTypeCode() + " already exists");
        }
        employmentType.setActive(true);
        return employmentTypeRepository.save(employmentType);
    }

    public EmploymentType updateEmploymentType(String id, EmploymentType employmentTypeDetails) {
        return employmentTypeRepository.findById(id)
                .map(employmentType -> {
                    employmentType.setTypeName(employmentTypeDetails.getTypeName());
                    employmentType.setDescription(employmentTypeDetails.getDescription());
                    employmentType.setMinHoursPerWeek(employmentTypeDetails.getMinHoursPerWeek());
                    employmentType.setMaxHoursPerWeek(employmentTypeDetails.getMaxHoursPerWeek());
                    employmentType.setEligibleForBenefits(employmentTypeDetails.getEligibleForBenefits());
                    employmentType.setRequiresContract(employmentTypeDetails.getRequiresContract());
                    employmentType.setContractDurationMonths(employmentTypeDetails.getContractDurationMonths());
                    employmentType.setColor(employmentTypeDetails.getColor());
                    employmentType.setDisplayOrder(employmentTypeDetails.getDisplayOrder());
                    return employmentTypeRepository.save(employmentType);
                })
                .orElseThrow(() -> new RuntimeException("Employment type not found with id: " + id));
    }

    public void deleteEmploymentType(String id) {
        employmentTypeRepository.findById(id)
                .ifPresentOrElse(
                        employmentType -> {
                            employmentType.setActive(false);
                            employmentTypeRepository.save(employmentType);
                        },
                        () -> { throw new RuntimeException("Employment type not found with id: " + id); }
                );
    }

    // ====================
    // SEMESTER METHODS
    // ====================

    public List<Semester> getAllSemesters() {
        return semesterRepository.findByActiveTrueOrderByDisplayOrder();
    }

    public Optional<Semester> getSemesterByCode(String semesterCode) {
        return semesterRepository.findBySemesterCodeAndActive(semesterCode, true);
    }

    public Semester createSemester(Semester semester) {
        if (semesterRepository.existsBySemesterCodeAndActive(semester.getSemesterCode(), true)) {
            throw new RuntimeException("Semester with code " + semester.getSemesterCode() + " already exists");
        }
        semester.setActive(true);
        return semesterRepository.save(semester);
    }

    public Semester updateSemester(String id, Semester semesterDetails) {
        return semesterRepository.findById(id)
                .map(semester -> {
                    semester.setSemesterName(semesterDetails.getSemesterName());
                    semester.setAcademicYear(semesterDetails.getAcademicYear());
                    semester.setStartDate(semesterDetails.getStartDate());
                    semester.setEndDate(semesterDetails.getEndDate());
                    semester.setRegistrationStartDate(semesterDetails.getRegistrationStartDate());
                    semester.setRegistrationEndDate(semesterDetails.getRegistrationEndDate());
                    semester.setDescription(semesterDetails.getDescription());
                    semester.setSemesterType(semesterDetails.getSemesterType());
                    semester.setDisplayOrder(semesterDetails.getDisplayOrder());
                    semester.setMaxCourses(semesterDetails.getMaxCourses());
                    semester.setMaxCredits(semesterDetails.getMaxCredits());
                    return semesterRepository.save(semester);
                })
                .orElseThrow(() -> new RuntimeException("Semester not found with id: " + id));
    }

    public void deleteSemester(String id) {
        semesterRepository.findById(id)
                .ifPresentOrElse(
                        semester -> {
                            semester.setActive(false);
                            semesterRepository.save(semester);
                        },
                        () -> { throw new RuntimeException("Semester not found with id: " + id); }
                );
    }

    // ====================
    // INITIALIZATION METHODS
    // ====================

    @Transactional
    public void initializeDefaultMasterData() {
        initializeDefaultGradeLevels();
        initializeDefaultSections();
        initializeDefaultDepartments();
        initializeDefaultEmploymentTypes();
        initializeDefaultSemesters();
    }

    private void initializeDefaultGradeLevels() {
        if (gradeLevelRepository.countByActiveTrue() == 0) {
            log.info("Initializing default grade levels...");

            gradeLevelRepository.save(GradeLevel.createPreK());
            gradeLevelRepository.save(GradeLevel.createKindergarten());

            // Elementary grades (1-5)
            for (int i = 1; i <= 5; i++) {
                gradeLevelRepository.save(GradeLevel.createElementaryGrade(i));
            }

            // Middle school grades (6-8)
            for (int i = 6; i <= 8; i++) {
                gradeLevelRepository.save(GradeLevel.createElementaryGrade(i));
            }

            // High school grades (9-12)
            for (int i = 9; i <= 12; i++) {
                gradeLevelRepository.save(GradeLevel.createHighSchoolGrade(i));
            }

            log.info("Default grade levels initialized");
        }
    }

    private void initializeDefaultSections() {
        if (sectionRepository.countByActiveTrue() == 0) {
            log.info("Initializing default sections...");

            // Create sections A through E
            for (char c = 'A'; c <= 'E'; c++) {
                sectionRepository.save(Section.createAlphabetSection(c));
            }

            log.info("Default sections initialized");
        }
    }

    private void initializeDefaultDepartments() {
        if (departmentRepository.countByActiveTrue() == 0) {
            log.info("Initializing default departments...");

            departmentRepository.save(Department.createMathematics());
            departmentRepository.save(Department.createEnglish());
            departmentRepository.save(Department.createScience());
            departmentRepository.save(Department.createSocialStudies());
            departmentRepository.save(Department.createComputerScience());
            departmentRepository.save(Department.createPhysicalEducation());
            departmentRepository.save(Department.createArts());
            departmentRepository.save(Department.createLanguages());
            departmentRepository.save(Department.createLibrary());
            departmentRepository.save(Department.createAdministration());

            log.info("Default departments initialized");
        }
    }

    private void initializeDefaultEmploymentTypes() {
        if (employmentTypeRepository.countByActiveTrue() == 0) {
            log.info("Initializing default employment types...");

            employmentTypeRepository.save(EmploymentType.createFullTime());
            employmentTypeRepository.save(EmploymentType.createPartTime());
            employmentTypeRepository.save(EmploymentType.createContract());
            employmentTypeRepository.save(EmploymentType.createSubstitute());
            employmentTypeRepository.save(EmploymentType.createIntern());

            log.info("Default employment types initialized");
        }
    }

    private void initializeDefaultSemesters() {
        if (semesterRepository.countByActiveTrue() == 0) {
            log.info("Initializing default semesters...");

            int currentYear = java.time.Year.now().getValue();

            // Create current year semesters
            semesterRepository.save(Semester.createFallSemester(currentYear));
            semesterRepository.save(Semester.createSpringSemester(currentYear + 1));
            semesterRepository.save(Semester.createSummerSemester(currentYear + 1));

            log.info("Default semesters initialized");
        }
    }
}
