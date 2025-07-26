package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "sections")
public class Section extends BaseEntity {

    @NotBlank(message = "Section code is required")
    @Indexed(unique = true)
    private String sectionCode; // e.g., "A", "B", "C", "ALPHA", "BETA"

    @NotBlank(message = "Section name is required")
    private String sectionName; // e.g., "Section A", "Alpha Section"

    private String description; // Optional description

    @NotNull(message = "Max capacity is required")
    @Min(value = 1, message = "Max capacity must be at least 1")
    @Max(value = 100, message = "Max capacity must be 100 or less")
    private Integer maxCapacity; // Maximum number of students

    private Integer currentEnrollment; // Current number of students

    private Integer displayOrder; // Order for display in dropdowns

    private String color; // Optional color code for UI display

    // Static factory methods for common sections
    public static Section createAlphabetSection(char letter) {
        Section section = new Section();
        section.setSectionCode(String.valueOf(letter));
        section.setSectionName("Section " + letter);
        section.setMaxCapacity(30);
        section.setCurrentEnrollment(0);
        section.setDisplayOrder((int) letter - 64); // A=1, B=2, etc.
        section.setDescription("Standard section " + letter);
        section.setActive(true);
        return section;
    }

    public static Section createNamedSection(String code, String name, int capacity) {
        Section section = new Section();
        section.setSectionCode(code);
        section.setSectionName(name);
        section.setMaxCapacity(capacity);
        section.setCurrentEnrollment(0);
        section.setDescription("Custom section: " + name);
        section.setActive(true);
        return section;
    }

    // Helper method to check if section is full
    public boolean isFull() {
        return currentEnrollment != null && maxCapacity != null &&
               currentEnrollment >= maxCapacity;
    }

    // Helper method to get available spots
    public int getAvailableSpots() {
        if (currentEnrollment == null) currentEnrollment = 0;
        if (maxCapacity == null) return 0;
        return Math.max(0, maxCapacity - currentEnrollment);
    }

    // Helper method to calculate occupancy percentage
    public double getOccupancyPercentage() {
        if (currentEnrollment == null || maxCapacity == null || maxCapacity == 0) {
            return 0.0;
        }
        return (double) currentEnrollment / maxCapacity * 100;
    }
}
