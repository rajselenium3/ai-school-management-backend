package com.eduai.schoolmanagement.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "families")
public class Family extends BaseEntity {

    @NotBlank(message = "Family name is required")
    private String familyName;

    private List<String> studentIds;

    @Valid
    private ContactInfo primaryContact;

    @Valid
    private ContactInfo secondaryContact;

    private String address;

    private List<EmergencyContact> emergencyContacts;

    @Data
    public static class ContactInfo {
        @NotBlank(message = "Contact name is required")
        private String name;

        @NotBlank(message = "Relationship is required")
        private String relationship;

        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Phone is required")
        private String phone;
    }

    @Data
    public static class EmergencyContact {
        @NotBlank(message = "Emergency contact name is required")
        private String name;

        @NotBlank(message = "Relationship is required")
        private String relationship;

        @NotBlank(message = "Phone is required")
        private String phone;
    }
}
