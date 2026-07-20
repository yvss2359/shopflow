package com.shopflow.user_service.entity;

/**
 * Rôles disponibles dans l'application.
 * Stocké en base comme String ("USER" ou "ADMIN")
 * grâce à @Enumerated(EnumType.STRING) dans l'entité.
 *
 * Spring Security préfixe automatiquement avec "ROLE_"
 * donc hasRole("ADMIN") cherche "ROLE_ADMIN" en interne.
 */
public enum Role {
    USER,   // utilisateur standard
    ADMIN   // accès à tous les endpoints d'administration
}
