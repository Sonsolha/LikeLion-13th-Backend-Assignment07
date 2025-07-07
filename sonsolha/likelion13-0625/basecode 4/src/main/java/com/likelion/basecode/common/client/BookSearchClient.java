package com.likelion.basecode.common.client;

import com.likelion.basecode.book.api.dto.response.BookResponseDto;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookSearchClient {

    private final RestTemplate restTemplate;

    @Value("${book-api.base-url}")
    private String baseUrl;

    @Value("${book-api.service-key}")
    private String serviceKey;

    public List<BookResponseDto> fetchAllBooks() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("resultType", "json")
                .queryParam("numOfRows", 50)
                .queryParam("pageNo", 1)
                .queryParam("keyword", "")  // 또는 null
                .build()
                .toUri();

        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

        Map<String, Object> body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessException(
                ErrorCode.BOOK_API_ITEM_MALFORMED,
                ErrorCode.BOOK_API_ITEM_MALFORMED.getMessage()
        )
);

        List<Map<String, Object>> items = extractItemList(body);

        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractItemList(Map<String, Object> responseMap) {
        Map<String, Object> response = castToMap(responseMap.get("response"), ErrorCode.BOOK_API_BODY_MALFORMED);
        Map<String, Object> body = castToMap(response.get("body"), ErrorCode.BOOK_API_BODY_MALFORMED);
        Map<String, Object> items = castToMap(body.get("items"), ErrorCode.BOOK_API_ITEMS_MALFORMED);
        Object itemObj = items.get("item");

        if (itemObj instanceof List<?> itemList) {
            return (List<Map<String, Object>>) itemList;
        } else if (itemObj instanceof Map<?, ?> itemMap) {
            return List.of((Map<String, Object>) itemMap);
        } else {
            throw new BusinessException(
                    ErrorCode.BOOK_API_ITEM_MALFORMED,
                    ErrorCode.BOOK_API_ITEM_MALFORMED.getMessage()
            );

        }
    }

    private BookResponseDto toDto(Map<String, Object> item) {
        return new BookResponseDto(
                (String) item.getOrDefault("title", ""),
                (String) item.getOrDefault("alternativeTitle", ""),
                (String) item.getOrDefault("entps", ""), // 저자명 or 기관명
                (String) item.getOrDefault("url", "")    // 상세정보 URL
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj, ErrorCode errorCode) {
        if (!(obj instanceof Map)) {
            throw new BusinessException(errorCode, errorCode.getMessage());
        }
        return (Map<String, Object>) obj;
    }
}
