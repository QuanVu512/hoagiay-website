package com.hoagiayphudong.helper.text;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugHelper {

    private SlugHelper() {
    }

    public static String normalizeSlug(String value) {
        String source = value == null ? "" : value.trim();
        String withoutVietnameseD = source.replace('đ', 'd').replace('Đ', 'D');
        String normalized = Normalizer.normalize(withoutVietnameseD, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Không thể tạo đường dẫn từ dữ liệu đã nhập.");
        }

        return normalized;
    }
}
