package com.lohith.gymtracker.repository;

import com.lohith.gymtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    default User getUserOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new com.lohith.gymtracker.exception.ResourceNotFoundException("User not found"));
    }
}
