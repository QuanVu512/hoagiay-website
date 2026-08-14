package com.hoagiayphudong.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping({"/", "/index.html"})
    public ResponseEntity<Resource> homePage() {
        return okHtml("index.html");
    }

    @GetMapping("/products")
    public ResponseEntity<Resource> publicProductsPage() {
        return okHtml("Product.html");
    }

    @GetMapping("/articles")
    public ResponseEntity<Resource> publicArticlesPage() {
        return okHtml("Article.html");
    }

    @GetMapping("/admin/account")
    public ResponseEntity<Resource> employeeAccountsPage() {
        return okHtml("Admin/Account.html");
    }

    @GetMapping("/admin/department")
    public ResponseEntity<Resource> departmentsPage() {
        return okHtml("Admin/Department.html");
    }

    @GetMapping("/admin/manager")
    public ResponseEntity<Resource> managersPage() {
        return okHtml("Admin/Manager.html");
    }

    @GetMapping("/admin/article")
    public ResponseEntity<Resource> articlesPage() {
        return okHtml("Admin/Article.html");
    }

    @GetMapping("/admin/product")
    public ResponseEntity<Resource> productsPage() {
        return okHtml("Admin/Product.html");
    }

    @GetMapping("/admin/category")
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
