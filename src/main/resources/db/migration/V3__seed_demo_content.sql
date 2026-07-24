INSERT INTO products (
    category_id,
    name,
    slug,
    code,
    short_description,
    price_label,
    color_family,
    height_cm,
    inventory_status,
    thumbnail_url,
    featured
)
SELECT id,
       'Hoa giấy ngũ sắc tán bay',
       'hoa-giay-ngu-sac-tan-bay',
       'PDG-001',
       'Cây hoa giấy nhiều màu, tán rộng, phù hợp sân vườn và cổng nhà.',
       'Liên hệ',
       'Ngũ sắc',
       220,
       'AVAILABLE',
       '/assets/images/product-placeholder.svg',
       TRUE
FROM categories
WHERE slug = 'hoa-giay-ngu-sac';

INSERT INTO products (
    category_id,
    name,
    slug,
    code,
    short_description,
    price_label,
    color_family,
    height_cm,
    inventory_status,
    thumbnail_url,
    featured
)
SELECT id,
       'Hoa giấy hồng ban công',
       'hoa-giay-hong-ban-cong',
       'PDG-002',
       'Dáng gọn, hoa sai, dễ chăm sóc cho ban công hoặc sân thượng.',
       'Liên hệ',
       'Hồng',
       90,
       'AVAILABLE',
       '/assets/images/product-placeholder.svg',
       TRUE
FROM categories
WHERE slug = 'hoa-giay-ban-cong';

INSERT INTO care_articles (
    title,
    slug,
    summary,
    content,
    thumbnail_url,
    published,
    published_at
)
VALUES
    (
        'Cách chăm hoa giấy ra hoa đều',
        'cach-cham-hoa-giay-ra-hoa-deu',
        'Các nguyên tắc cơ bản về nắng, nước và cắt tỉa để cây hoa giấy khỏe và ra hoa ổn định.',
        'Hoa giấy cần nhiều nắng, đất thoát nước tốt và chế độ tưới hợp lý. Khi cây đã khỏe, có thể siết nước nhẹ và cắt tỉa để kích thích mầm hoa mới.',
        '/assets/images/article-placeholder.svg',
        TRUE,
        CURRENT_TIMESTAMP
    ),
    (
        'Chọn hoa giấy cho ban công nhỏ',
        'chon-hoa-giay-cho-ban-cong-nho',
        'Gợi ý chọn giống, kích thước chậu và cách bố trí hoa giấy cho không gian nhỏ.',
        'Với ban công nhỏ, nên chọn cây dáng gọn, chậu vừa phải và màu hoa hài hòa với mặt tiền. Cần ưu tiên vị trí có nắng trực tiếp ít nhất vài giờ mỗi ngày.',
        '/assets/images/article-placeholder.svg',
        TRUE,
        CURRENT_TIMESTAMP
    );
