package com.likelion.basecode.show.api.dto.response;

import java.util.List;

public record ShowListResponseDto(
        List<ShowResponseDto> shows
) {}
