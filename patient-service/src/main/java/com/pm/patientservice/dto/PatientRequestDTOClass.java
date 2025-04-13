package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PatientRequestDTOClass {

    @NotBlank(message = "first name is required")
    @Size(max = 100, message = "first name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "last name is required")
    @Size(max = 100, message = "last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "birthday is required")
    private String dateOfBirth;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "email is required")
    @Email(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "phone number is required")
    private String phone;

    @NotBlank(message =  "register date is required")
    private String registerDate;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }
}
