package com.likelion.basecode.book.api.dto.response;

public record BookResponseDto(
        String title,
        String alternativeTitle,
        String author,
        String url
) {}
