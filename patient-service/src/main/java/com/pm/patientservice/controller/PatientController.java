package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTOClass;
import com.pm.patientservice.dto.PatientResponseDTOClass;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")// http://localhost:4000/patients
@Tag(name = "Patient", description = "API for managing Patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /*
        CRUD - Create, Read, Update, Delete
     */

    //CREATE
    @PostMapping //POST patients
    @Operation(summary = "Create a new Patient")
    public ResponseEntity<PatientResponseDTOClass> createPatients(
            @Validated({Default.class, CreatePatientValidationGroup.class})
            @RequestBody PatientRequestDTOClass patientRequestDTOClass
    ) {
        return ResponseEntity.ok().body(patientService.createPatient(patientRequestDTOClass));
    }

    //READ
    @GetMapping //GET patients
    @Operation(summary = "Get Patients Info")
    public ResponseEntity<List<PatientResponseDTOClass>> getPatients(){
        return ResponseEntity.ok().body(patientService.getPatients());
    }

    @GetMapping("/id/{id}") //GET patients/id/{id}
    @Operation(summary = "Get Patient Info by their ID")
    public ResponseEntity<PatientResponseDTOClass> getPatientsById(@PathVariable UUID id){
        return ResponseEntity.ok().body(patientService.getPatientById(id));
    }

    @GetMapping("/email/{email}") //GET patients/email/{email}
    @Operation(summary = "Get Patient Info by their Email")
    public ResponseEntity<PatientResponseDTOClass> getPatientsByEmail(@PathVariable String email){
        return ResponseEntity.ok().body(patientService.getPatientByEmail(email));
    }

    @GetMapping("/search")
    @Operation(summary = "Get Patients based on their firstname, lastname, and/or date of birth")
    public ResponseEntity<List<PatientResponseDTOClass>> getPatientByFirstnameLastnameDateOfBirth(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam LocalDate dateOfBirth){
        return ResponseEntity.ok().body(patientService.getPatientByFirstnameLastnameDateOfBirth(firstname, lastname, dateOfBirth));
    }

    //UPDATE
    @PutMapping("/{id}")//this is for the whole patient to get updated.
    @Operation(summary = "Update Patient")
    public ResponseEntity<PatientResponseDTOClass> updatePatient(
            @PathVariable UUID id, @Validated(Default.class) @RequestBody PatientRequestDTOClass patientRequestDTOClass
    ){
        PatientResponseDTOClass patientResponseDTOClass = patientService.updatePatient(id, patientRequestDTOClass);
        return ResponseEntity.ok().body(patientResponseDTOClass);
    }

    //DELETE
    @DeleteMapping("/id/{id}")
    @Operation(summary = "Delete Patient by ID")
    public ResponseEntity<Void> deletePatientById(@PathVariable UUID id){
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    //CANT do this because spring sees /{id} & /{email} as the same thing
    //  2 Solutions:
    //      1. add more to the path such as DELETE /patients/id/{id}
    //      2. create 1 @DeleteMapping method and then use @RequestParam DELETE /patients?id={id} same with email
    // solution 2 could lead to an issue where both an id and email are provided (more logic required)
    // for this situation ill go with solution 1
    @DeleteMapping("/email/{email}")
    @Operation(summary = "Delete Patient by Email")
    public ResponseEntity<Void> deletePatientByEmail(@PathVariable String email){
        patientService.deletePatientByEmail(email);
        return ResponseEntity.noContent().build();
    }




}
