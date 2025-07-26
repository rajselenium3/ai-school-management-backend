package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "parents")
public class Parent extends BaseEntity {
    @DBRef
    private User user;

    @NotBlank(message = "Parent ID is required")
    @Indexed(unique = true)
    private String parentId;

    @DBRef
    private List<Student> children = new ArrayList<>();

    private String relationship; // Father, Mother, Guardian, etc.
    private String occupation;
    private String workPhone;
    private String emergencyContact;
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private Boolean isPrimary = false; // Primary parent contact
    private Boolean isActive = true;

    // Financial settings
    private Boolean canViewFinancials = true;
    private Boolean canMakePayments = true;
    private Boolean receiveBillingNotifications = true;

    // Communication preferences
    private Boolean receiveEmailNotifications = true;
    private Boolean receiveSmsNotifications = true;
    private Boolean receiveProgressReports = true;
    private Boolean receiveAttendanceAlerts = true;

    // Add child to parent
    public void addChild(Student student) {
        if (children == null) {
            children = new ArrayList<>();
        }
        if (!children.contains(student)) {
            children.add(student);
        }
    }

    // Remove child from parent
    public void removeChild(Student student) {
        if (children != null) {
            children.remove(student);
        }
    }
}
