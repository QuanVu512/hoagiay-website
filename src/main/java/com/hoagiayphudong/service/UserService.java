package com.hoagiayphudong.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.hoagiayphudong.helper.exception.ResourceNotFoundException;
import com.hoagiayphudong.dto.EmployeeAccountCreateRequest;
import com.hoagiayphudong.dto.EmployeeAccountResponse;
import com.hoagiayphudong.dto.EmployeeAccountUpdateRequest;
import com.hoagiayphudong.dto.RoleOptionResponse;
import com.hoagiayphudong.model.Manager;
import com.hoagiayphudong.model.Role;
import com.hoagiayphudong.model.User;
import com.hoagiayphudong.repository.ManagerRepository;
import com.hoagiayphudong.repository.RoleRepository;
import com.hoagiayphudong.repository.UserRepository;
import com.hoagiayphudong.security.SecurityPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public List<EmployeeAccountResponse> findAllAccounts() {
        return managerRepository.findManagersWithAccount()
                .stream()
                .map(EmployeeAccountResponse::from)
                .toList();
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public EmployeeAccountResponse findAccountById(Long id) {
        return EmployeeAccountResponse.from(findAccountOwner(id));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public List<RoleOptionResponse> findRoleOptions() {
        return roleRepository.findAllByOrderByIdAsc()
                .stream()
                .map(RoleOptionResponse::from)
                .toList();
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public EmployeeAccountResponse createAccount(EmployeeAccountCreateRequest request) {
        Manager manager = managerRepository.findById(request.managerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ nhân viên id " + request.managerId()));

        if (manager.getUser() != null) {
            throw new IllegalStateException("Hồ sơ nhân viên này đã có tài khoản.");
        }

        validateUniqueUsername(cleanText(request.username()), null);
        validateUniqueEmail(cleanText(request.email()), null);

        User user = new User();
        user.setUsername(cleanText(request.username()));
        user.setPassword(passwordEncoder.encode(request.password().trim()));
        user.setEmail(cleanText(request.email()));
        user.replaceRoles(findRoles(request.roleIds()));
        user.setActive(request.active() == null || request.active());

        User savedUser = userRepository.save(user);
        manager.setUser(savedUser);

        return EmployeeAccountResponse.from(managerRepository.save(manager));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public EmployeeAccountResponse updateAccount(Long id, EmployeeAccountUpdateRequest request) {
        Manager manager = findAccountOwner(id);
        User user = manager.getUser();

        String username = cleanText(request.username());
        String email = cleanText(request.email());

        validateUniqueUsername(username, id);
        validateUniqueEmail(email, id);

        user.setUsername(username);
        user.setEmail(email);
        user.replaceRoles(findRoles(request.roleIds()));
        if (request.active() != null) {
            user.setActive(request.active());
        }

        if (hasText(request.password())) {
            String password = request.password().trim();
            if (password.length() < 6 || password.length() > 120) {
                throw new IllegalArgumentException("Mật khẩu phải từ 6 đến 120 ký tự.");
            }
            user.setPassword(passwordEncoder.encode(password));
        }

        return EmployeeAccountResponse.from(managerRepository.save(manager));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public void deleteAccount(Long id) {
        Manager manager = findAccountOwner(id);
        User user = manager.getUser();

        manager.setUser(null);
        managerRepository.save(manager);
        userRepository.delete(user);
    }

    private Manager findAccountOwner(Long id) {
        return managerRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản nhân viên id " + id));
    }

    private void validateUniqueUsername(String username, Long ignoredUserId) {
        boolean exists = ignoredUserId == null
                ? userRepository.existsByUsername(username)
                : userRepository.existsByUsernameAndIdNot(username, ignoredUserId);

        if (exists) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
    }

    private void validateUniqueEmail(String email, Long ignoredUserId) {
        boolean exists = ignoredUserId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, ignoredUserId);

        if (exists) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }
    }

    private List<Role> findRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một vai trò.");
        }

        List<Long> cleanRoleIds = roleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (cleanRoleIds.isEmpty()) {
            throw new IllegalArgumentException("Vai trò không hợp lệ.");
        }

        List<Role> roles = roleRepository.findByIdIn(cleanRoleIds);
        if (roles.size() != cleanRoleIds.size()) {
            throw new ResourceNotFoundException("Không tìm thấy một hoặc nhiều vai trò đã chọn.");
        }

        return roles;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    @Transactional(readOnly = true)
    public Optional<User> findAccountByUsernameOrEmail(String loginName) {
        return userRepository.findByUsernameOrEmail(loginName, loginName);
    }
}
