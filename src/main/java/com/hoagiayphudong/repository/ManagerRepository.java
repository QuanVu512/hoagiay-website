package com.hoagiayphudong.repository;

import com.hoagiayphudong.model.Manager;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @EntityGraph(attributePaths = {"user", "department"})
    List<Manager> findAllByOrderByIdAsc();

    @Query("select m from Manager m where m.user is null order by m.id")
    List<Manager> findManagersWithoutAccount();

    @Query("""
            select distinct m
            from Manager m
            join fetch m.user user
            left join fetch user.roleAssignments roleAssignment
            left join fetch roleAssignment.role
            left join fetch m.department
            where m.user is not null
            order by user.id
            """)
    List<Manager> findManagersWithAccount();

    @Query("""
            select distinct m
            from Manager m
            join fetch m.user user
            left join fetch user.roleAssignments roleAssignment
            left join fetch roleAssignment.role
            left join fetch m.department
            where user.id = :userId
            """)
    Optional<Manager> findByUserId(Long userId);

    boolean existsByDepartmentId(Long departmentId);
}
