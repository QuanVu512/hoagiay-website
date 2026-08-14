package com.hoagiayphudong.service;

import java.util.List;

import com.hoagiayphudong.dto.AuthLoginRequest;
import com.hoagiayphudong.dto.AuthLoginResponse;
import com.hoagiayphudong.dto.AuthAccountResponse;
import com.hoagiayphudong.dto.AuthRefreshRequest;
import com.hoagiayphudong.dto.AuthRegisterRequest;
import com.hoagiayphudong.dto.AuthTokenExchangeResponse;
import com.hoagiayphudong.helper.exception.ResourceAlreadyExistsException;
import com.hoagiayphudong.helper.security.SecurityUtil;
import com.hoagiayphudong.model.Customer;
import com.hoagiayphudong.model.Role;
import com.hoagiayphudong.model.User;
import com.hoagiayphudong.repository.CustomerRepository;
import com.hoagiayphudong.repository.RoleRepository;
import com.hoagiayphudong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthAccountResponse currentAccount() {
        List<String> roles = SecurityUtil.getCurrentRoles();
        return new AuthAccountResponse(
                SecurityUtil.getCurrentUserIdLogin().orElse(null),
                SecurityUtil.getCurrentUsernameLogin().orElse(null),
                roles,
                findManagementPath(roles)
        );
    }

    public AuthLoginResponse login(AuthLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userService.findAccountByUsernameOrEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Khong tim thay tai khoan da xac thuc."));
        RefreshTokenService.RefreshTokenResult refreshToken = refreshTokenService.issueToken(user);
        JwtService.AccessTokenResult accessToken = jwtService.createAccessToken(authentication, user.getId());

        return new AuthLoginResponse(
                accessToken.tokenType(),
                accessToken.accessToken(),
                accessToken.expiresIn(),
                accessToken.expiresAt(),
                refreshToken.tokenValue(),
                refreshToken.expiresAt(),
                user.getId(),
                accessToken.username(),
                accessToken.roles()
        );
    }

    @Transactional
    public AuthLoginResponse register(AuthRegisterRequest request) {
        String username = cleanText(request.username());
        String email = cleanText(request.email());
        String password = request.password() == null ? "" : request.password().trim();
        String confirmPassword = request.confirmPassword() == null ? "" : request.confirmPassword().trim();

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại chưa khớp.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new ResourceAlreadyExistsException("Tên đăng nhập đã tồn tại.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("Email đã tồn tại.");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("Role CUSTOMER chưa được khởi tạo."));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.replaceRoles(List.of(customerRole));

        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setFullName(cleanText(request.fullName()));
        customer.setPhone(cleanText(request.phone()));
        customerRepository.save(customer);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getUsername(),
                null,
                savedUser.getRoleAuthorities()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
        RefreshTokenService.RefreshTokenResult refreshToken = refreshTokenService.issueToken(savedUser);
        JwtService.AccessTokenResult accessToken = jwtService.createAccessToken(authentication, savedUser.getId());

        return new AuthLoginResponse(
                accessToken.tokenType(),
                accessToken.accessToken(),
                accessToken.expiresIn(),
                accessToken.expiresAt(),
                refreshToken.tokenValue(),
                refreshToken.expiresAt(),
                savedUser.getId(),
                accessToken.username(),
                accessToken.roles()
        );
    }

    public AuthTokenExchangeResponse refresh(AuthRefreshRequest request) {
        if (request == null || !StringUtils.hasText(request.refreshToken())) {
            throw new BadCredentialsException("Refresh token khong hop le.");
        }

        RefreshTokenService.RefreshTokenResult refreshToken = refreshTokenService.rotateToken(request.refreshToken());
        User user = refreshToken.user();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                user.getRoleAuthorities()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
        JwtService.AccessTokenResult accessToken = jwtService.createAccessToken(authentication, user.getId());

        return new AuthTokenExchangeResponse(
                accessToken.tokenType(),
                accessToken.accessToken(),
                accessToken.expiresIn(),
                accessToken.expiresAt(),
                refreshToken.tokenValue(),
                refreshToken.expiresAt()
        );
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    private String findManagementPath(List<String> roles) {
        if (roles.contains("ADMIN")) {
            return "/admin/account";
        }
        if (roles.contains("ARTICLE")) {
            return "/admin/article";
        }
        if (roles.contains("PRODUCT")) {
            return "/admin/product";
        }
        if (roles.contains("CATEGORY")) {
            return "/admin/category";
        }

        return null;
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
