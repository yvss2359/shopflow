package com.shopflow.user_service.repository;

import com.shopflow.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité User.
 *
 * En héritant de JpaRepository<User, Long>, on obtient
 * GRATUITEMENT sans écrire une ligne de SQL :
 *   - save(user)          → INSERT ou UPDATE
 *   - findById(id)        → SELECT WHERE id = ?
 *   - findAll()           → SELECT *
 *   - deleteById(id)      → DELETE WHERE id = ?
 *   - count()             → SELECT COUNT(*)
 *   - existsById(id)      → SELECT COUNT(*) > 0
 *
 * Les méthodes déclarées ici sont générées automatiquement
 * par Spring Data à partir de leur nom (Query Derivation).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Spring génère automatiquement :
     * SELECT * FROM users WHERE email = ? LIMIT 1
     *
     * Optional<> évite les NullPointerException —
     * au lieu de retourner null, retourne Optional.empty()
     * si aucun utilisateur n'est trouvé.
     */
    Optional<User> findByEmail(String email);

    /**
     * SELECT COUNT(*) > 0 FROM users WHERE email = ?
     * Utilisé pour vérifier si un email est déjà pris
     * avant l'inscription, sans charger l'entité entière.
     */
    boolean existsByEmail(String email);
}