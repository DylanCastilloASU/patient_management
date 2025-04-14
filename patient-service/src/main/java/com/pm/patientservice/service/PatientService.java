package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTOClass;
import com.pm.patientservice.dto.PatientResponseDTOClass;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    /*
    CRUD - Create, Read, Update, Delete
    */

    //CREATE

    //Standard PatientRepository method
    public PatientResponseDTOClass createPatient(PatientRequestDTOClass patientRequestDTOClass) {
        Patient newPatient = patientMapper.toPatientFromPatientRequestDTOClass(patientRequestDTOClass);
        patientRepository.save(newPatient);
        return patientMapper.toPatientResponseDTOClassFromPatient(newPatient);

    }

    //READ

    //Standard PatientRepository method
    public List<PatientResponseDTOClass> getPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toPatientResponseDTOClassFromPatient)
                .toList();
    }

    //Standard PatientRepository method
    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id).orElse(null);
    }

    //This is a custom method that was created in PatientRepository - More info about method can be found there
    public List<Patient> getPatientByFirstnameLastnameDateOfBirth(
            String firstname,
            String lastname,
            LocalDate dateOfBirth
    ) {
        return patientRepository.findByFirstNameContainingAndLastNameContainingAndDateOfBirth(
                firstname,
                lastname,
                dateOfBirth
        ).orElse(null);
    }

    //This is a custom method that was created in PatientRepository - more info about method can be found there
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email).orElse(null);
    }


    //UPDATE
    //I would like for this method to only update existing patients if they don't exist, I don't want it to create a new
    //patient. That is the task of createPatient.
    public Patient updatePatient(Patient patient) {
        Patient existingPatient = patientRepository.findByEmail(patient.getEmail()).orElseThrow();
        return patientRepository.save(patient);
    }

    //DELETE

    //Standard PatientRepository method
    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }

    //This is a custom method that was created in PatientRepository
    public void deletePatientByEmail(String email) {
        patientRepository.deleteByEmail(email);
    }

}
