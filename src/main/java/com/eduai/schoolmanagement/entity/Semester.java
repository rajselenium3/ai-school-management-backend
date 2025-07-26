package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "semesters")
public class Semester extends BaseEntity {

    @NotBlank(message = "Semester code is required")
    @Indexed(unique = true)
    private String semesterCode; // e.g., "FALL2024", "SPRING2025", "SUMMER2024"

    @NotBlank(message = "Semester name is required")
    private String semesterName; // e.g., "Fall 2024", "Spring 2025", "Summer 2024"

    @NotNull(message = "Academic year is required")
    private Integer academicYear; // e.g., 2024, 2025

    @NotNull(message = "Start date is required")
    private LocalDate startDate; // Semester start date

    @NotNull(message = "End date is required")
    private LocalDate endDate; // Semester end date

    private LocalDate registrationStartDate; // Registration start date
    private LocalDate registrationEndDate; // Registration end date

    private String description; // Semester description

    private String semesterType; // FALL, SPRING, SUMMER, WINTER

    private Boolean isCurrentSemester; // Whether this is the current active semester

    private Integer displayOrder; // Order for display

    private Integer maxCourses; // Maximum courses per semester
    private Integer maxCredits; // Maximum credits per semester

    // Static factory methods for common semesters
    public static Semester createFallSemester(int year) {
        Semester semester = new Semester();
        semester.setSemesterCode("FALL" + year);
        semester.setSemesterName("Fall " + year);
        semester.setAcademicYear(year);
        semester.setSemesterType("FALL");
        semester.setStartDate(LocalDate.of(year, 8, 15)); // August 15
        semester.setEndDate(LocalDate.of(year, 12, 15)); // December 15
        semester.setRegistrationStartDate(LocalDate.of(year, 6, 1)); // June 1
        semester.setRegistrationEndDate(LocalDate.of(year, 8, 10)); // August 10
        semester.setDescription("Fall semester " + year);
        semester.setMaxCourses(8);
        semester.setMaxCredits(18);
        semester.setDisplayOrder(1);
        semester.setActive(true);
        return semester;
    }

    public static Semester createSpringSemester(int year) {
        Semester semester = new Semester();
        semester.setSemesterCode("SPRING" + year);
        semester.setSemesterName("Spring " + year);
        semester.setAcademicYear(year);
        semester.setSemesterType("SPRING");
        semester.setStartDate(LocalDate.of(year, 1, 15)); // January 15
        semester.setEndDate(LocalDate.of(year, 5, 15)); // May 15
        semester.setRegistrationStartDate(LocalDate.of(year - 1, 11, 1)); // November 1 previous year
        semester.setRegistrationEndDate(LocalDate.of(year, 1, 10)); // January 10
        semester.setDescription("Spring semester " + year);
        semester.setMaxCourses(8);
        semester.setMaxCredits(18);
        semester.setDisplayOrder(2);
        semester.setActive(true);
        return semester;
    }

    public static Semester createSummerSemester(int year) {
        Semester semester = new Semester();
        semester.setSemesterCode("SUMMER" + year);
        semester.setSemesterName("Summer " + year);
        semester.setAcademicYear(year);
        semester.setSemesterType("SUMMER");
        semester.setStartDate(LocalDate.of(year, 6, 1)); // June 1
        semester.setEndDate(LocalDate.of(year, 8, 10)); // August 10
        semester.setRegistrationStartDate(LocalDate.of(year, 4, 1)); // April 1
        semester.setRegistrationEndDate(LocalDate.of(year, 5, 25)); // May 25
        semester.setDescription("Summer semester " + year);
        semester.setMaxCourses(4);
        semester.setMaxCredits(12);
        semester.setDisplayOrder(3);
        semester.setActive(true);
        return semester;
    }

    // Helper methods
    public boolean isRegistrationOpen() {
        LocalDate now = LocalDate.now();
        return registrationStartDate != null && registrationEndDate != null &&
               !now.isBefore(registrationStartDate) && !now.isAfter(registrationEndDate);
    }

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return startDate != null && endDate != null &&
               !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public boolean isUpcoming() {
        LocalDate now = LocalDate.now();
        return startDate != null && now.isBefore(startDate);
    }

    public boolean isCompleted() {
        LocalDate now = LocalDate.now();
        return endDate != null && now.isAfter(endDate);
    }

    public long getDaysUntilStart() {
        if (startDate == null) return -1;
        LocalDate now = LocalDate.now();
        return now.isBefore(startDate) ?
               java.time.temporal.ChronoUnit.DAYS.between(now, startDate) : 0;
    }

    public long getDaysUntilEnd() {
        if (endDate == null) return -1;
        LocalDate now = LocalDate.now();
        return now.isBefore(endDate) ?
               java.time.temporal.ChronoUnit.DAYS.between(now, endDate) : 0;
    }

    public long getDurationInDays() {
        if (startDate == null || endDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    public String getStatus() {
        if (isActive()) return "ACTIVE";
        if (isUpcoming()) return "UPCOMING";
        if (isCompleted()) return "COMPLETED";
        return "UNKNOWN";
    }
}
