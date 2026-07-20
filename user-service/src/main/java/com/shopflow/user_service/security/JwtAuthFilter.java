package com.shopflow.user_service.security;

import com.shopflow.user_service.repository.UserRepository;
import com.shopflow.user_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre JWT — s'exécute UNE FOIS par requête HTTP (OncePerRequestFilter).
 *
 * Rôle : intercepter chaque requête, lire le token JWT dans le header
 * Authorization, le valider, et si valide — authentifier l'utilisateur
 * dans le SecurityContext pour que Spring Security le reconnaisse.
 *
 * Flux :
 * Requête HTTP
 *   → JwtAuthFilter (ce filtre)
 *     → lit "Authorization: Bearer eyJ..."
 *     → extrait et valide le JWT
 *     → charge l'utilisateur depuis la BDD
 *     → injecte l'authentification dans SecurityContext
 *   → Controller (la requête continue)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain  // chaîne des filtres suivants
    ) throws ServletException, IOException {

        // 1. Récupère le header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si pas de token ou format incorrect → passe au filtre suivant
        // Spring Security refusera la requête si l'endpoint est protégé
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrait le token (retire "Bearer " — 7 caractères)
        final String jwt = authHeader.substring(7);

        // 4. Extrait l'email du token
        final String userEmail;
        try {
            userEmail = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            log.warn("Impossible d'extraire l'email du token : {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Si email extrait et pas encore authentifié dans ce contexte
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Charge l'utilisateur depuis la BDD pour vérification
            var userOpt = userRepository.findByEmail(userEmail);

            if (userOpt.isPresent() && jwtService.isTokenValid(jwt, userEmail)) {
                var user = userOpt.get();

                // 7. Crée l'objet d'authentification Spring Security
                // SimpleGrantedAuthority = le rôle de l'utilisateur
                var authToken = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,  // pas besoin du password ici, le JWT suffit
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Enregistre l'authentification dans le SecurityContext
                // Spring Security sait maintenant QUI fait la requête
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Utilisateur authentifié via JWT : {}", userEmail);
            }
        }

        // 9. Passe la requête au filtre suivant (puis au Controller)
        filterChain.doFilter(request, response);
    }
}