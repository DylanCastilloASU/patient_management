package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientRequestDTORecord(

        @NotBlank(message = "first name is required")
        @Size(max = 100, message = "first name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "last name is required")
        @Size(max = 100, message = "last name must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "birthday is required")
        String dateOfBirth,

        @NotBlank(message = "address is required")
        String address,

        @NotBlank(message = "email is required")
        @Email(message = "please enter a valid email")
        String email,

        @NotBlank(message = "phone number is required")
        String phone,

        @NotBlank(message =  "register date is required")
        String registerDate
) {
}
