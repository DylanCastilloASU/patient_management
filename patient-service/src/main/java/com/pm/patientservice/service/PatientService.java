package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTOClass;
import com.pm.patientservice.dto.PatientResponseDTOClass;
import com.pm.patientservice.exception.*;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(
            PatientRepository patientRepository,
            PatientMapper patientMapper,
            BillingServiceGrpcClient billingServiceGrpcClient,
            KafkaProducer kafkaProducer
    ) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    /*
    CRUD - Create, Read, Update, Delete
    */

    //CREATE

    //Standard PatientRepository method
    public PatientResponseDTOClass createPatient(PatientRequestDTOClass patientRequestDTOClass) {
        //a user already exists with this email
        if (patientRepository.existsByEmail(patientRequestDTOClass.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists " + patientRequestDTOClass.getEmail()
            );
        }
        //making the given info into a patient object
        Patient newPatient = patientMapper.toPatientFromPatientRequestDTOClass(patientRequestDTOClass);
        //saving the patient object into the database
        patientRepository.save(newPatient);
        //calling the billing service microservice via a GRPC API connection and providing the info from patient object
        billingServiceGrpcClient.createBillingAccount(
                newPatient.getId().toString(),
                newPatient.getFirstName(),
                newPatient.getLastName(),
                newPatient.getEmail()
        );
        //sending info to a kafka topic so ...
        kafkaProducer.sendEvent(newPatient);

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
    public PatientResponseDTOClass getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new InvalidIdException("There is no account with the id: " + id)
        );
        return patientMapper.toPatientResponseDTOClassFromPatient(patient);
    }

    //This is a custom method that was created in PatientRepository - more info about method can be found there
    public PatientResponseDTOClass getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email).orElseThrow(
                () -> new InvalidEmailException("There is no account with the email: " + email)
        );
        return patientMapper.toPatientResponseDTOClassFromPatient(patient);
    }

    //This is a custom method that was created in PatientRepository - More info about method can be found there
    public List<PatientResponseDTOClass> getPatientByFirstnameLastnameDateOfBirth(
            String firstname,
            String lastname,
            LocalDate dateOfBirth
    ) {
        return patientRepository.findByFirstNameContainingAndLastNameContainingAndDateOfBirth(
                firstname,
                lastname,
                dateOfBirth
        ).orElseThrow(() -> new PatientNotFoundException(
                "There does not exist a patient with the provided details: \n\tFirstName: " + firstname +
                        "\n\tLastName: " + lastname + "\n\tDate of Birth: " + dateOfBirth))
                .stream()
                .map(patientMapper::toPatientResponseDTOClassFromPatient)
                .toList();
    }


    //UPDATE
    //I would like for this method to only update existing patients if they don't exist, I don't want it to create a new
    //patient. That is the task of createPatient.
    public PatientResponseDTOClass updatePatient(UUID id, PatientRequestDTOClass patientRequestDTOClass) {
        Patient existingPatient = patientRepository.findById(id).orElseThrow(
                () -> new InvalidIdException("There is no account with the id: " + id)
        );
        //checking to see if email is getting updated if so, check to see that the new email isn't associated to an
        //existing account
        if(!existingPatient.getEmail().equals(patientRequestDTOClass.getEmail())) {
            if(patientRepository.existsByEmail(patientRequestDTOClass.getEmail())) {
                throw new NewEmailAlreadyExistsException(
                        "There is already an account with the updated email: " + patientRequestDTOClass.getEmail() +
                                " old email: " + existingPatient.getEmail());
            }
        }
        existingPatient.setFirstName(patientRequestDTOClass.getFirstName());
        existingPatient.setLastName(patientRequestDTOClass.getLastName());
        existingPatient.setDateOfBirth(LocalDate.parse(patientRequestDTOClass.getDateOfBirth()));
        existingPatient.setAddress(patientRequestDTOClass.getAddress());
        existingPatient.setEmail(patientRequestDTOClass.getEmail());
        existingPatient.setPhone(patientRequestDTOClass.getPhone());
        patientRepository.save(existingPatient);
        return patientMapper.toPatientResponseDTOClassFromPatient(existingPatient);
    }

    //DELETE

    //Standard PatientRepository method
    public void deletePatient(UUID id) {
        if(!patientRepository.existsById(id)) {
            throw new InvalidIdException("There is no account with the id: " + id);
        }
        patientRepository.deleteById(id);
    }

    //This is a custom method that was created in PatientRepository
    @Transactional
    public void deletePatientByEmail(String email) {
        if(!patientRepository.existsByEmail(email)) {
            throw new InvalidEmailException("There is no account with the email: " + email);
        }
        patientRepository.deleteByEmail(email);
    }

}
