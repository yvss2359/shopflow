package com.shopflow.user_service.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO de sortie représentant un utilisateur.
 * Ne contient PAS le mot de passe — jamais exposé dans une API.
 *
 * Utilisé pour GET /api/users/me et GET /api/users/{id}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
