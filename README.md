# Hoa Giấy Phù Đổng

Website portfolio về hoa giấy, xây bằng Spring Boot cho backend và HTML/CSS/JavaScript cho frontend tĩnh.

## Tech stack

- Backend: Spring Boot, Spring Web, Spring Data JPA, Bean Validation.
- Database: PostgreSQL cho deploy; H2 cho local/test.
- Migration: Flyway.
- Frontend: HTML/CSS/JavaScript trong `src/main/resources/static`.

## Cấu trúc chính

- `src/main/java/com/hoagiayphudong/catalog`: danh mục và sản phẩm.
- `src/main/java/com/hoagiayphudong/blog`: bài viết chăm sóc hoa giấy.
- `src/main/java/com/hoagiayphudong/contact`: form liên hệ/tư vấn.
- `src/main/java/com/hoagiayphudong/common`: thành phần dùng chung.
- `src/main/resources/db/migration`: schema và dữ liệu nền.
- `src/main/resources/static`: frontend tĩnh.
- `docs/project-foundation.md`: ghi chú định hướng dự án.
- `docs/mvc-flow.md`: giải thích luồng frontend, controller, service, repository và database.

## Luồng MVC

Frontend gửi dữ liệu bằng JavaScript `fetch`, Controller nhận request, Service xử lý nghiệp vụ, Repository làm việc với database, sau đó dữ liệu trả ngược lại frontend dưới dạng JSON.

Ví dụ các API nền:

- `GET /api/categories`
- `GET /api/products?featured=true`
- `GET /api/articles`
- `POST /api/contact-messages`

## Chạy local

```powershell
.\gradlew.bat bootRun
```

Ứng dụng chạy mặc định ở `http://localhost:8080`.

## Cấu hình deploy PostgreSQL

Khi deploy, đặt profile `prod` và khai báo:

```text
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://<host>/<database>?sslmode=require
DATABASE_USERNAME=<username>
DATABASE_PASSWORD=<password>
```
