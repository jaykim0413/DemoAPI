package com.demo.api.payload.request;

import java.util.Set;

import com.demo.api.validation.annotations.PasswordMatching;
import com.demo.api.validation.annotations.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatching(password = "password", confirmPassword = "confirmPassword", message = "Password must match!")
public class SignUpRequest {

    @NotBlank
    @Size(min = 8, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @StrongPassword
    private String password;

    private String confirmPassword;

    private Set<String> role;
}
