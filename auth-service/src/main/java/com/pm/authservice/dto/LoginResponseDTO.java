package com.pm.authservice.dto;

public class LoginResponseDTO {

    private final String token; // - JWT TOKEN

    //this is basically the setter (constructor injection)
    public LoginResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
