package com.hoagiayphudong.controller;

import com.hoagiayphudong.security.SecurityPermission;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping({"/", "/index.html"})
    public ResponseEntity<Resource> homePage() {
        return okHtml("index.html");
    }

    @GetMapping("/admin/account")
    @PreAuthorize(SecurityPermission.ADMIN)
    public ResponseEntity<Resource> employeeAccountsPage() {
        return okHtml("Admin/Account.html");
    }

    @GetMapping("/admin/department")
    @PreAuthorize(SecurityPermission.ADMIN)
    public ResponseEntity<Resource> departmentsPage() {
        return okHtml("Admin/Department.html");
    }

    @GetMapping("/admin/manager")
    @PreAuthorize(SecurityPermission.ADMIN)
    public ResponseEntity<Resource> managersPage() {
        return okHtml("Admin/Manager.html");
    }

    @GetMapping("/admin/article")
    @PreAuthorize(SecurityPermission.ADMIN)
    public ResponseEntity<Resource> articlesPage() {
        return okHtml("Admin/Article.html");
    }

    @GetMapping("/admin/product")
    @PreAuthorize(SecurityPermission.ADMIN)
    public ResponseEntity<Resource> productsPage() {
        return okHtml("Admin/Product.html");
    }

    @GetMapping("/admin/category")
    @PreAuthorize(SecurityPermission.ADMIN)
    public ResponseEntity<Resource> categoriesPage() {
        return okHtml("Admin/Category.html");
    }

    @GetMapping("/login")
    public ResponseEntity<Resource> loginPage() {
        return okHtml("Security/Login.html");
    }

    @GetMapping("/register")
    public ResponseEntity<Resource> registerPage() {
        return okHtml("Security/Register.html");
    }

    @GetMapping("/notfound")
    public ResponseEntity<Resource> notFoundPage() {
        return statusHtml(HttpStatus.NOT_FOUND, "Security/Deny.html");
    }

    private ResponseEntity<Resource> okHtml(String path) {
        return statusHtml(HttpStatus.OK, path);
    }

    private ResponseEntity<Resource> statusHtml(HttpStatus status, String path) {
        return ResponseEntity.status(status)
                .contentType(MediaType.TEXT_HTML)
                .body(new ClassPathResource("static/" + path));
    }
}
