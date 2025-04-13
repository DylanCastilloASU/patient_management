package com.pm.patientservice.dto;

public record PatientResponseDTORecord(
        String firstName,

        String lastName,

        String dateOfBirth,

        String address,

        String email,

        String phone
) {
}
