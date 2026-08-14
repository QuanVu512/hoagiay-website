package com.hoagiayphudong.config;

import com.hoagiayphudong.model.User;
import com.hoagiayphudong.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản");
        }

        User user = this.userService.findAccountByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Boolean.TRUE.equals(user.getActive()),
                true,
                true,
                true,
                user.getRoleAuthorities()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }
}
