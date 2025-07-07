package com.likelion.basecode.show.api.dto.response;

public record ShowResponseDto(
        String title,
        String viewCount,
        String description,
        String url
) {}
