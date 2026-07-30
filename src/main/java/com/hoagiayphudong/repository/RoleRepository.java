package com.hoagiayphudong.repository;

import com.hoagiayphudong.model.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByOrderByIdAsc();

    List<Role> findByIdIn(Collection<Long> ids);
}
