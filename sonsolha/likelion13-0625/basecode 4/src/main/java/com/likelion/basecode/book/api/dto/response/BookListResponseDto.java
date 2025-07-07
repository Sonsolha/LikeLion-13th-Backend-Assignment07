package com.likelion.basecode.book.api.dto.response;

import java.util.List;

public record BookListResponseDto(
        List<BookResponseDto> books
) {}
