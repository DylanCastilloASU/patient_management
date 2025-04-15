package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    /*
        This doesn't work because in PatientService I have a .orElse(null) on the function call just in case the email
        provided doesn't exist to basically give a null but that requires the usage of Optional because this statement
        below it requiring the return of a Patient object while have Optional is basically an option
    //Patient findByEmail(@NotNull String email);
    Method: This method is basically a SQL search query that finds only one patient (if they exist) due to email being
        unique
    */
    Optional<Patient> findByEmail(@NotNull String email);

    /*
        Method: This method is a SQL search query that allows three parameters: firstname, lastname, and date of birth
            •Allows subsets of first name and last name meaning if EX: john doe, Fname = jo, Lname = d, DOB = ""
            john doe would appear.
            •It also allows for various ways to search a patient:
                Firstname
                Lastname
                DOB
                First, Lastname
                ...
     */
    Optional<List<Patient>> findByFirstNameContainingAndLastNameContainingAndDateOfBirth(
            String firstName,
            String lastName,
            LocalDate dateOfBirth
    );

    void deleteByEmail(@NotNull String email);

    boolean existsByEmail(@NotNull String email);
}
