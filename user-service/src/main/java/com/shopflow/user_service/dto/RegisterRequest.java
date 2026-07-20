package com.shopflow.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO d'entrée pour l'inscription d'un nouvel utilisateur.
 *
 * Les annotations de validation (@NotBlank, @Email, @Size)
 * sont vérifiées automatiquement par Spring quand on met
 * @Valid sur le paramètre du Controller.
 * Si une contrainte échoue → réponse 400 Bad Request automatique.
 */
@Data   // @Getter + @Setter + @ToString + @EqualsAndHashCode
public class RegisterRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;
}