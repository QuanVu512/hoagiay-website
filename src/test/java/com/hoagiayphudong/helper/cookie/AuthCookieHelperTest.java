package com.hoagiayphudong.helper.cookie;

import java.time.Instant;
import java.util.List;

import com.hoagiayphudong.config.CookieProperties;
import com.hoagiayphudong.dto.AuthLoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class AuthCookieHelperTest {

    @Test
    void addAuthCookiesUsesHttpOnlyAndConfiguredSecureFlag() {
        AuthCookieHelper helper = new AuthCookieHelper(new CookieProperties(true, "Lax"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        helper.addAuthCookies(response, new AuthLoginResponse(
                "Bearer",
                "access-token-value",
                3600,
                Instant.now().plusSeconds(3600),
                "refresh-token-value",
                Instant.now().plusSeconds(604800),
                1L,
                "admin",
                List.of("ADMIN")
        ));

        List<String> cookies = response.getHeaders("Set-Cookie");

        assertThat(cookies).hasSize(2);
        assertThat(cookies).allSatisfy(cookie -> assertThat(cookie)
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Lax"));
        assertThat(cookies.get(0)).contains("af=access-token-value", "Path=/");
        assertThat(cookies.get(1)).contains("rf=refresh-token-value", "Path=/api/auth");
    }
}
