package com.pm.patientservice.dto;

public record PatientResponseDTORecord(
        String Id,

        String firstName,

        String lastName,

        String dateOfBirth,

        String address,

        String email,

        String phone
) {
}
