package com.renatobonfim.aemblogbackend.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Contact {


    @NotBlank( message="'name' should should not be blank")
    @NotEmpty( message="'name' should should not be empty")
    @Size( min=2, message ="'name' content is too short'")
    String name;

    @Email(message = "'email' is not valid")
    String email;

    @NotBlank( message="'message' should should not be blank")
    @NotEmpty( message="'message' should should not be empty")
    @Size( min=5, message ="'message' content is too short'")
    String message;
}
