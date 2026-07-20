package com.shopflow.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO d'entrée pour la connexion.
 * Simple — juste email + password.
 */
@Data
public class LoginRequest {

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
