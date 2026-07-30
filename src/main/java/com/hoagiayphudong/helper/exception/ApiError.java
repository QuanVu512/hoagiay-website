package com.hoagiayphudong.helper.exception;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        String message,
        int status,
        List<String> details,
        OffsetDateTime timestamp
) {
}
