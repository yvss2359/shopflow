package com.shopflow.user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entité JPA — représente la table "users" en base PostgreSQL.
 * Hibernate lit cette classe au démarrage et crée/met à jour
 * la table automatiquement (ddl-auto: update dans application.yml).
 *
 * Implements UserDetails : Spring Security peut l'utiliser
 * directement pour l'authentification.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder                          // permet User.builder().email(...).build()
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY = auto-incrément géré par PostgreSQL (SERIAL)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    // unique = true → PostgreSQL crée un index unique sur cette colonne
    // Une exception sera levée si on tente d'insérer un email existant
    private String email;

    @Column(nullable = false)
    // Jamais stocker le mot de passe en clair
    // Ce champ contiendra le hash BCrypt : $2a$10$...
    private String password;

    @Enumerated(EnumType.STRING)
    // STRING = stocke "USER" ou "ADMIN" en base (pas 0 ou 1)
    // Plus lisible en BDD et résistant aux refactorings d'enum
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;  // valeur par défaut à la création

    @Column(nullable = false, updatable = false)
    // updatable = false → Hibernate ne touche pas à ce champ lors d'un UPDATE
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    // Appelé automatiquement par JPA avant chaque UPDATE en base
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}