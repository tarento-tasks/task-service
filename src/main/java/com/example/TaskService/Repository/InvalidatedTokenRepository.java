package com.example.TaskService.Repository;

import com.example.TaskService.Model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;


public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, UUID> {
    Optional<InvalidatedToken> findByToken(String token);
}