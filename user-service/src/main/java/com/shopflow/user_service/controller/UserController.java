package com.shopflow.user_service.controller;

import com.shopflow.user_service.dto.UserResponse;
import com.shopflow.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour la gestion des profils utilisateurs.
 * Tous les endpoints nécessitent un JWT valide
 * (configuré dans SecurityConfig via .anyRequest().authenticated())
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me
     * Retourne le profil de l'utilisateur connecté.
     *
     * Authentication est injecté automatiquement par Spring Security
     * — c'est l'objet créé par JwtAuthFilter dans le SecurityContext.
     * authentication.getName() retourne l'email (le "subject" du JWT).
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserResponse user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/users/{id}
     * Retourne un utilisateur par son ID.
     *
     * @PreAuthorize("hasRole('ADMIN')") : Spring vérifie le rôle
     * AVANT d'entrer dans la méthode. Si l'utilisateur n'est pas ADMIN
     * → 403 Forbidden automatiquement.
     * Nécessite @EnableMethodSecurity dans SecurityConfig.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getById(id);
        return ResponseEntity.ok(user);
    }
}