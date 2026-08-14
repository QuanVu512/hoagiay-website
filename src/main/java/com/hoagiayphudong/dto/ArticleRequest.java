package com.hoagiayphudong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArticleRequest(
        @NotBlank(message = "Tiêu đề bài viết không được để trống")
        @Size(max = 220, message = "Tiêu đề tối đa 220 ký tự")
        String title,

        @Size(max = 260, message = "Slug tối đa 260 ký tự")
        String slug,

        String summary,

        String content,

        @Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
        String thumbnailUrl,

        Boolean published
) {
}
