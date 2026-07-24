# Luồng MVC backend

Với frontend HTML/CSS/JavaScript, dự án nên dùng Spring Boot theo kiểu REST MVC:

1. Frontend gọi API bằng `fetch`.
2. Controller nhận request, kiểm tra đầu vào cơ bản và gọi Service.
3. Service xử lý nghiệp vụ, điều phối dữ liệu và gọi Repository.
4. Repository làm việc với database thông qua Spring Data JPA.
5. Database trả dữ liệu về Repository.
6. Repository trả entity về Service.
7. Service đổi entity thành DTO response.
8. Controller trả JSON cho frontend.

Repository không gửi tới một backend khác. Repository là một lớp nằm trong backend và nhiệm vụ chính của nó là giao tiếp với database.

## API nền đã có

- `GET /api/categories`: lấy danh mục đang hoạt động.
- `GET /api/products`: lấy danh sách sản phẩm.
- `GET /api/products?featured=true`: lấy sản phẩm nổi bật.
- `GET /api/products?categorySlug=hoa-giay-ngu-sac`: lọc sản phẩm theo danh mục.
- `GET /api/products?q=hồng`: tìm sản phẩm theo tên.
- `GET /api/products/{slug}`: lấy chi tiết sản phẩm.
- `GET /api/articles`: lấy bài viết đã xuất bản.
- `GET /api/articles/{slug}`: lấy chi tiết bài viết.
- `POST /api/contact-messages`: frontend gửi form tư vấn.

## Ví dụ frontend gửi form

```javascript
fetch("/api/contact-messages", {
    method: "POST",
    headers: {
        "Content-Type": "application/json"
    },
    body: JSON.stringify({
        fullName: "Nguyễn Văn A",
        phone: "0900000000",
        email: "a@example.com",
        message: "Tôi muốn tư vấn hoa giấy ban công"
    })
});
```
