package com.storerecommender.apiserver.dtos;

import java.time.LocalDateTime;

public record ErrorDto(
        String message,
        LocalDateTime timestamp
) {}
