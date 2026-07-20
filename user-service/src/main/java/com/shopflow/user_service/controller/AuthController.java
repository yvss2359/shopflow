package com.shopflow.user_service.controller;

import com.shopflow.user_service.dto.*;
import com.shopflow.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour l'authentification.
 * Ces endpoints sont PUBLICS — configurés dans SecurityConfig.
 *
 * @RestController = @Controller + @ResponseBody
 * → chaque méthode retourne directement du JSON, pas une vue HTML
 *
 * @RequestMapping("/api/auth") = préfixe commun à tous les endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     *
     * Corps attendu :
     * {
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "email": "john@example.com",
     *   "password": "motdepasse123"
     * }
     *
     * @Valid déclenche la validation des @NotBlank, @Email, etc.
     * Si invalide → 400 Bad Request avec les messages d'erreur
     * Si valide → 201 Created avec le token JWT
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // 201 Created = ressource créée avec succès (plus précis que 200 OK)
    }

    /**
     * POST /api/auth/login
     *
     * Corps attendu :
     * {
     *   "email": "john@example.com",
     *   "password": "motdepasse123"
     * }
     *
     * Réponse : 200 OK avec le token JWT
     * ou 401 Unauthorized si credentials incorrects
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}