package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTOClass;
import com.pm.patientservice.dto.PatientRequestDTORecord;
import com.pm.patientservice.dto.PatientResponseDTOClass;
import com.pm.patientservice.dto.PatientResponseDTORecord;
import com.pm.patientservice.model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PatientMapper {

    //PatientResponseDTORecord - Content (PRACTICE PURPOSES)

    public Patient toPatientFromPatientResponseDTORecord(PatientResponseDTORecord patientResponseDTO) {
        Patient patient = new Patient();
        patient.setFirstName(patientResponseDTO.firstName());
        patient.setLastName(patientResponseDTO.lastName());
        LocalDate dateOfBirth = LocalDate.parse(patientResponseDTO.dateOfBirth()); //ISO-8601 format yyyy-mm-dd
        patient.setDateOfBirth(dateOfBirth);
        patient.setAddress(patientResponseDTO.address());
        patient.setEmail(patientResponseDTO.email());
        patient.setPhone(patientResponseDTO.phone());
        return patient;
    }

    public PatientResponseDTORecord toPatientResponseDTORecordFromPatient(Patient patient) {
        return new PatientResponseDTORecord(
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth().toString(),//ISO-8601 format yyyy-mm-dd
                patient.getAddress(),
                patient.getEmail(),
                patient.getPhone()
        );
    }

    //PatientResponseDTOClass - Content

    public Patient toPatientFromPatientResponseDTOClass(PatientResponseDTOClass patientResponseDTO) {
        Patient patient = new Patient();
        patient.setFirstName(patientResponseDTO.getFirstName());
        patient.setLastName(patientResponseDTO.getLastName());
        LocalDate dateOfBirth = LocalDate.parse(patientResponseDTO.getDateOfBirth()); //ISO-8601 format yyyy-mm-dd
        patient.setDateOfBirth(dateOfBirth);
        patient.setAddress(patientResponseDTO.getAddress());
        patient.setEmail(patientResponseDTO.getEmail());
        patient.setPhone(patientResponseDTO.getPhone());
        return patient;
    }

    public PatientResponseDTOClass toPatientResponseDTOClassFromPatient(Patient patient) {
        PatientResponseDTOClass patientResponseDTOClass = new PatientResponseDTOClass();
        patientResponseDTOClass.setFirstName(patient.getFirstName());
        patientResponseDTOClass.setLastName(patient.getLastName());
        patientResponseDTOClass.setDateOfBirth(patient.getDateOfBirth().toString()); //ISO-8601 format yyyy-mm-dd
        patientResponseDTOClass.setAddress(patient.getAddress());
        patientResponseDTOClass.setEmail(patient.getEmail());
        patientResponseDTOClass.setPhone(patient.getPhone());
        return patientResponseDTOClass;
    }

    //PatientRequestDTORecord - Content (PRACTICE PURPOSES)
    public Patient toPatientFromPatientRequestDTORecord(PatientRequestDTORecord patientRequestDTORecord) {
        Patient patient = new Patient();
        patient.setFirstName(patientRequestDTORecord.firstName());
        patient.setLastName(patientRequestDTORecord.lastName());
        LocalDate dateOfBirth = LocalDate.parse(patientRequestDTORecord.dateOfBirth());
        patient.setDateOfBirth(dateOfBirth);
        patient.setAddress(patientRequestDTORecord.address());
        patient.setEmail(patientRequestDTORecord.email());
        patient.setPhone(patientRequestDTORecord.phone());
        LocalDate registeredDate = LocalDate.parse(patientRequestDTORecord.dateOfBirth());
        patient.setRegistrationDate(registeredDate);
        return patient;
    }

    public PatientRequestDTORecord toPatientRequestDTORecordFromPatient(Patient patient) {
        return new PatientRequestDTORecord(
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth().toString(),
                patient.getAddress(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getRegistrationDate().toString()
        );
    }

    //PatientRequestDTOClass - Content
    public Patient toPatientFromPatientRequestDTOClass(PatientRequestDTOClass patientRequestDTOClass) {
        Patient patient = new Patient();
        patient.setFirstName(patientRequestDTOClass.getFirstName());
        patient.setLastName(patientRequestDTOClass.getLastName());
        LocalDate dateOfBirth = LocalDate.parse(patientRequestDTOClass.getDateOfBirth());
        patient.setDateOfBirth(dateOfBirth);
        patient.setAddress(patientRequestDTOClass.getAddress());
        patient.setEmail(patientRequestDTOClass.getEmail());
        patient.setPhone(patientRequestDTOClass.getPhone());
        LocalDate registeredDate = LocalDate.parse(patientRequestDTOClass.getRegisterDate());
        patient.setRegistrationDate(registeredDate);
        return patient;
    }

    public PatientRequestDTOClass toPatientRequestDTOClassFromPatient(Patient patient) {
        PatientRequestDTOClass patientRequestDTOClass = new PatientRequestDTOClass();
        patientRequestDTOClass.setFirstName(patient.getFirstName());
        patientRequestDTOClass.setLastName(patient.getLastName());
        patientRequestDTOClass.setDateOfBirth(patient.getDateOfBirth().toString());
        patientRequestDTOClass.setAddress(patient.getAddress());
        patientRequestDTOClass.setEmail(patient.getEmail());
        patientRequestDTOClass.setPhone(patient.getPhone());
        patientRequestDTOClass.setRegisterDate(patient.getRegistrationDate().toString());
        return patientRequestDTOClass;
    }


}
