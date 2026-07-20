package com.shopflow.user_service.dto;

import lombok.*;

/**
 * DTO de sortie après une authentification réussie.
 * Retourne le token JWT que le client devra stocker
 * et envoyer dans chaque requête suivante.
 *
 * Exemple de réponse JSON :
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "type": "Bearer",
 *   "email": "john@example.com",
 *   "role": "USER"
 * }
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";  // type standard du token JWT

    private String email;
    private String role;
}
