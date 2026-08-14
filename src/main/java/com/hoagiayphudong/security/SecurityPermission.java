package com.hoagiayphudong.security;

public final class SecurityPermission {

    public static final String ADMIN = "hasAuthority('ADMIN')";
    public static final String ARTICLE = "hasAnyAuthority('ADMIN', 'ARTICLE')";
    public static final String PRODUCT = "hasAnyAuthority('ADMIN', 'PRODUCT')";
    public static final String CATEGORY = "hasAnyAuthority('ADMIN', 'CATEGORY')";

    private SecurityPermission() {
    }
}
