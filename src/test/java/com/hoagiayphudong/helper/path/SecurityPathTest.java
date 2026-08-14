package com.hoagiayphudong.helper.path;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityPathTest {

    @Test
    void publicGetApiSkipsCookieAccessTokenResolution() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/products");

        assertThat(SecurityPath.shouldSkipAccessTokenCookie(request)).isTrue();
    }

    @Test
    void protectedAdminApiDoesNotSkipCookieAccessTokenResolution() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/product");

        assertThat(SecurityPath.shouldSkipAccessTokenCookie(request)).isFalse();
    }
}
