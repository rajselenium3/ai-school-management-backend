package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "departments")
public class Department extends BaseEntity {

    @NotBlank(message = "Department code is required")
    @Indexed(unique = true)
    private String departmentCode; // e.g., "MATH", "ENG", "SCI", "ADMIN"

    @NotBlank(message = "Department name is required")
    private String departmentName; // e.g., "Mathematics", "English", "Science"

    private String description; // Department description

    private String headOfDepartment; // Name of department head

    @Email(message = "Invalid email format")
    private String contactEmail; // Department contact email

    private String contactPhone; // Department contact phone

    private String location; // Physical location/building

    private String color; // Color code for UI display

    private Integer displayOrder; // Order for display

    private Integer maxTeachers; // Maximum number of teachers

    private Integer currentTeachers; // Current number of teachers

    // Static factory methods for common departments
    public static Department createAcademicDepartment(String code, String name) {
        Department dept = new Department();
        dept.setDepartmentCode(code);
        dept.setDepartmentName(name);
        dept.setDescription("Academic department for " + name);
        dept.setMaxTeachers(20);
        dept.setCurrentTeachers(0);
        dept.setActive(true);
        return dept;
    }

    public static Department createMathematics() {
        Department dept = createAcademicDepartment("MATH", "Mathematics");
        dept.setDescription("Mathematics and related subjects");
        dept.setColor("#2196F3");
        dept.setDisplayOrder(1);
        return dept;
    }

    public static Department createEnglish() {
        Department dept = createAcademicDepartment("ENG", "English");
        dept.setDescription("English Language and Literature");
        dept.setColor("#4CAF50");
        dept.setDisplayOrder(2);
        return dept;
    }

    public static Department createScience() {
        Department dept = createAcademicDepartment("SCI", "Science");
        dept.setDescription("General Science, Physics, Chemistry, Biology");
        dept.setColor("#FF9800");
        dept.setDisplayOrder(3);
        return dept;
    }

    public static Department createSocialStudies() {
        Department dept = createAcademicDepartment("SS", "Social Studies");
        dept.setDescription("History, Geography, Civics");
        dept.setColor("#9C27B0");
        dept.setDisplayOrder(4);
        return dept;
    }

    public static Department createComputerScience() {
        Department dept = createAcademicDepartment("CS", "Computer Science");
        dept.setDescription("Computer Science and Information Technology");
        dept.setColor("#607D8B");
        dept.setDisplayOrder(5);
        return dept;
    }

    public static Department createPhysicalEducation() {
        Department dept = createAcademicDepartment("PE", "Physical Education");
        dept.setDescription("Physical Education and Sports");
        dept.setColor("#795548");
        dept.setDisplayOrder(6);
        return dept;
    }

    public static Department createArts() {
        Department dept = createAcademicDepartment("ART", "Arts");
        dept.setDescription("Visual Arts, Music, Drama");
        dept.setColor("#E91E63");
        dept.setDisplayOrder(7);
        return dept;
    }

    public static Department createLanguages() {
        Department dept = createAcademicDepartment("LANG", "Languages");
        dept.setDescription("Foreign Languages");
        dept.setColor("#009688");
        dept.setDisplayOrder(8);
        return dept;
    }

    public static Department createLibrary() {
        Department dept = createAcademicDepartment("LIB", "Library");
        dept.setDescription("Library Services");
        dept.setColor("#8BC34A");
        dept.setDisplayOrder(9);
        return dept;
    }

    public static Department createAdministration() {
        Department dept = createAcademicDepartment("ADMIN", "Administration");
        dept.setDescription("Administrative Staff");
        dept.setColor("#F44336");
        dept.setDisplayOrder(10);
        return dept;
    }

    // Helper methods
    public boolean isFull() {
        return currentTeachers != null && maxTeachers != null &&
               currentTeachers >= maxTeachers;
    }

    public int getAvailablePositions() {
        if (currentTeachers == null) currentTeachers = 0;
        if (maxTeachers == null) return 0;
        return Math.max(0, maxTeachers - currentTeachers);
    }

    public double getOccupancyPercentage() {
        if (currentTeachers == null || maxTeachers == null || maxTeachers == 0) {
            return 0.0;
        }
        return (double) currentTeachers / maxTeachers * 100;
    }
}
