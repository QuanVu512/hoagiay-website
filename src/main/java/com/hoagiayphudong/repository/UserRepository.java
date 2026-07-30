package com.hoagiayphudong.repository;

import com.hoagiayphudong.model.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrderByIdAsc();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @EntityGraph(attributePaths = {"roleAssignments", "roleAssignments.role"})
    Optional<User> findByUsernameOrEmail(String username, String email);
}
