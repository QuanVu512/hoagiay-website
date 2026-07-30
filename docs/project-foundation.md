# Nền móng dự án Hoa Giấy Phù Đổng

## Hướng sản phẩm

Dự án đi theo hướng website catalog cho nhà vườn hoa giấy: giới thiệu dòng cây, danh sách sản phẩm, bài viết chăm sóc và form liên hệ. Đây là phạm vi vừa đủ để làm sản phẩm phỏng vấn vì có frontend, API, database, nghiệp vụ quản trị nội dung và deploy.

## Module backend

- `catalog`: danh mục và sản phẩm hoa giấy.
- `blog`: bài viết chăm sóc, tin tức, kinh nghiệm trồng cây.
- `contact`: tin nhắn tư vấn/liên hệ của khách.
- `helper`: lỗi chung, response chung và tiện ích dùng lại.
- `config`: cấu hình ứng dụng.
- `security`: chừa sẵn cho đăng nhập quản trị sau này.

## Frontend tĩnh

- `src/main/resources/static/assets/css`: style.
- `src/main/resources/static/assets/js`: JavaScript.
- `src/main/resources/static/assets/images`: ảnh dùng chung.
- `src/main/resources/static/uploads`: ảnh upload/demo khi chạy local.

## Database

Database chính dùng PostgreSQL. Khi chạy local, test hoặc deploy, dự án đều đọc kết nối PostgreSQL/Neon qua biến môi trường để tránh lệch dữ liệu giữa máy cá nhân và database thật.

Gợi ý dịch vụ free cho portfolio: Neon PostgreSQL. Lý do: có free tier, không cần thẻ tín dụng theo thông tin giá hiện tại, là PostgreSQL chuẩn nên tích hợp tốt với Spring Boot/JPA/Flyway và sau này nâng cấp ít phải đổi công nghệ.

## Profile cấu hình

- Local/test/deploy đều dùng PostgreSQL, nhận kết nối qua `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`.
- Có thể khai báo các biến này trong `.env` khi chạy trên máy cá nhân hoặc trong Environment Variables khi deploy.
