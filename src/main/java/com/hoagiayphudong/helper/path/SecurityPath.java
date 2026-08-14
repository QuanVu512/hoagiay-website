package com.hoagiayphudong.helper.path;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

public final class SecurityPath {

    public static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    public static final String[] PAGE_WHITELIST = {
            "/",
            "/index.html",
            "/products",
            "/articles",
            "/assets/**",
            "/Asset/**",
            "/Security/**",
            "/favicon.ico",
            "/error/**",
            "/login",
            "/register",
            "/notfound",
            "/error"
    };

    public static final String[] AUTH_WHITELIST = {
            AuthPath.LOGIN,
            AuthPath.REGISTER,
            AuthPath.REFRESH,
            AuthPath.LOGOUT
    };

    public static final String[] PUBLIC_GET_API_WHITELIST = {
            "/api/categories",
            "/api/products",
            "/api/articles"
    };

    public static final String[] ADMIN_PAGE_WHITELIST = {
            "/Admin/**",
            "/admin/**"
    };

    private SecurityPath() {
    }

    public static boolean shouldSkipAccessTokenCookie(HttpServletRequest request) {
        String path = request.getRequestURI();

        return isPagePath(path)
                || isSwaggerPath(path)
                || isAuthPath(path)
                || isPublicGetApi(request, path);
    }

    private static boolean isPagePath(String path) {
        return path.equals("/")
                || path.equals("/index.html")
                || path.equals("/products")
                || path.equals("/articles")
                || path.equals("/favicon.ico")
                || path.equals("/login")
                || path.equals("/register")
                || path.equals("/notfound")
                || path.equals("/error")
                || path.startsWith("/assets/")
                || path.startsWith("/Asset/")
                || path.startsWith("/Security/")
                || path.startsWith("/error/");
    }

    private static boolean isSwaggerPath(String path) {
        return path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/")
                || path.equals("/v3/api-docs")
                || path.equals("/v3/api-docs.yaml")
                || path.startsWith("/v3/api-docs/");
    }

    private static boolean isAuthPath(String path) {
        return path.equals(AuthPath.LOGIN)
                || path.equals(AuthPath.REGISTER)
                || path.equals(AuthPath.REFRESH)
                || path.equals(AuthPath.LOGOUT);
    }

    private static boolean isPublicGetApi(HttpServletRequest request, String path) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return false;
        }

        return path.equals("/api/categories")
                || path.equals("/api/products")
                || path.equals("/api/articles");
    }
}
