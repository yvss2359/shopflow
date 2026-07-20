package com.shopflow.user_service.service;

import com.shopflow.user_service.dto.*;
import com.shopflow.user_service.entity.User;
import com.shopflow.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'authentification.
 * Contient toute la logique métier pour register() et login().
 *
 * @Transactional sur register() : si la sauvegarde en BDD échoue,
 * toute l'opération est annulée (rollback automatique).
 */
@Service
@RequiredArgsConstructor  // génère un constructeur avec tous les champs final
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // BCryptPasswordEncoder
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription d'un nouvel utilisateur.
     *
     * Étapes :
     * 1. Vérifie que l'email n'est pas déjà utilisé
     * 2. Hash le mot de passe avec BCrypt
     * 3. Crée et sauvegarde l'utilisateur en BDD
     * 4. Génère et retourne un JWT
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Étape 1 — email unique
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Un compte existe déjà avec l'email : " + request.getEmail()
            );
        }

        // Étape 2 & 3 — création de l'utilisateur
        // Le @Builder de Lombok permet cette syntaxe fluide
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // BCrypt génère : $2a$10$randomSalt...hashedPassword
                .build();

        userRepository.save(user);
        log.info("Nouvel utilisateur inscrit : {}", user.getEmail());

        // Étape 4 — génération du JWT
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Connexion d'un utilisateur existant.
     *
     * Étapes :
     * 1. Spring Security vérifie email + password via AuthenticationManager
     *    → lève BadCredentialsException si incorrect (→ 401)
     * 2. Charge l'utilisateur depuis la BDD
     * 3. Génère et retourne un JWT
     */
    public AuthResponse login(LoginRequest request) {
        // Étape 1 — délègue la vérification à Spring Security
        // AuthenticationManager appelle UserDetailsService.loadUserByUsername()
        // puis compare les mots de passe avec BCrypt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si on arrive ici, les credentials sont valides
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        log.info("Connexion réussie pour : {}", user.getEmail());

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}