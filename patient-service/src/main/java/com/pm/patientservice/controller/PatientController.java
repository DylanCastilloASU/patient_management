package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTOClass;
import com.pm.patientservice.dto.PatientResponseDTOClass;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")// http://localhost:4000/patients
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
    public ResponseEntity<PatientResponseDTOClass> addPatient(@ModelAttribute("patient") PatientRequestDTOClass patient) {
        return ResponseEntity.ok(patientService.createPatient(patient));
    }

    //READ
    @GetMapping //GET patients
    public ResponseEntity<List<PatientResponseDTOClass>> getPatients(){
        return ResponseEntity.ok().body(patientService.getPatients());
    }

    @GetMapping("/id/{id}") //GET patients/id/{id}
    public ResponseEntity<PatientResponseDTOClass> getPatientsById(@PathVariable UUID id){
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDTOClass>> getPatientByFirstnameLastnameDateOfBirth(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam LocalDateTime dateOfBirth){
        return null;
    }

    @GetMapping("/email/{email}") //GET patients/email/{email}
    public ResponseEntity<PatientResponseDTOClass> getPatientsByEmail(@PathVariable String email){
        return null;
    }

    //UPDATE
    @PatchMapping //this is for only some things to get update such as email, phone number etc
    @PutMapping//this is for the whole patient to get updated.

    //DELETE
    @DeleteMapping("/id/{id}")
    public void deletePatientById(@PathVariable UUID id){}
    //CANT do this because spring sees /{id} & /{email} as the same thing
    //  2 Solutions:
    //      1. add more to the path such as DELETE /patients/id/{id}
    //      2. create 1 @DeleteMapping method and then use @RequestParam DELETE /patients?id={id} same with email
    // solution 2 could lead to an issue where both an id and email are provided (more logic required)
    // for this situation ill go with solution 1
    @DeleteMapping("/email/{email}")
    public void deletePatientByEmail(@PathVariable String email){}




}
