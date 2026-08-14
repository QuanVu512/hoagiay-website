package com.hoagiayphudong.repository;

import java.util.Optional;

import com.hoagiayphudong.model.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @EntityGraph(attributePaths = {"user", "user.roleAssignments", "user.roleAssignments.role"})
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
