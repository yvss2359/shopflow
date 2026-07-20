package com.shopflow.user_service.service;

import com.shopflow.user_service.dto.UserResponse;
import com.shopflow.user_service.entity.User;
import com.shopflow.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service pour les opérations sur les utilisateurs.
 * Séparé de AuthService pour respecter le principe
 * de responsabilité unique (Single Responsibility).
 *
 * AuthService → authentification
 * UserService → gestion des profils
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retourne le profil de l'utilisateur connecté.
     * L'email vient du JWT décodé par JwtAuthFilter
     * et est injecté via le SecurityContext.
     */
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + email));

        return toResponse(user);
    }

    /**
     * Retourne un utilisateur par son ID.
     * Accessible uniquement par les ADMIN.
     */
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + id));

        return toResponse(user);
    }

    /**
     * Convertit une entité User en DTO UserResponse.
     * Cette conversion est faite ICI, jamais dans le Controller.
     * Le Controller ne connaît pas l'entité User — seulement les DTOs.
     */
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}