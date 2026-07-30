package com.hoagiayphudong.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Setter
    private String username;

    @Column(nullable = false, length = 120)
    @Setter
    private String password;

    @Column(nullable = false, unique = true, length = 160)
    @Setter
    private String email;

    @Column(nullable = false)
    @Setter
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoleAssignment> roleAssignments = new ArrayList<>();

    @PrePersist
    void beforeCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void beforeUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public List<Role> getRoles() {
        return roleAssignments.stream()
                .map(UserRoleAssignment::getRole)
                .sorted(Comparator.comparing(Role::getId))
                .toList();
    }

    public List<String> getRoleAuthorities() {
        return getRoles().stream()
                .map(Role::getAuthority)
                .toList();
    }

    public boolean hasAuthority(String authority) {
        return getRoleAuthorities().contains(authority);
    }

    public void replaceRoles(Collection<Role> roles) {
        roleAssignments.clear();
        roles.forEach(role -> roleAssignments.add(new UserRoleAssignment(this, role)));
    }
}
