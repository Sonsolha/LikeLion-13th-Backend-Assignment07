package com.likelion.basecode.common.client;

import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.show.api.dto.response.ShowResponseDto;
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
public class ShowSearchClient {

    private final RestTemplate restTemplate;

    @Value("${show-api.base-url}")
    private String baseUrl;

    @Value("${show-api.service-key}")
    private String serviceKey;

    // 외부 공연 API로부터 전체 공연 목록을 조회
    public List<ShowResponseDto> fetchAllShows() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 20) // 한 페이지에 20개 조회
                .queryParam("pageNo", 1)  // 10번째 페이지
                .build()
                .toUri();

        // 외부 API 호출
        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

        // 응답 body가 null인 경우 예외 발생
        Map<String, Object> body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOW_API_RESPONSE_NULL, ErrorCode.SHOW_API_RESPONSE_NULL.getMessage()));

        // 'item' 리스트를 추출한 후 ShowResponseDto 형태로 변환
        return extractItemList(body).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 응답 맵에서 공연 항목 리스트 추출
    @SuppressWarnings("unchecked") // Java 컴파일러 경고를 무시하도록 하는 어노테이션-!
    private List<Map<String, Object>> extractItemList(Map<String, Object> responseMap) {
        // 'item' 리스트까지 접근
        Map<String, Object> response = castToMap(responseMap.get("response"), ErrorCode.SHOW_API_BODY_MALFORMED);
        Map<String, Object> body = castToMap(response.get("body"), ErrorCode.SHOW_API_BODY_MALFORMED);
        Map<String, Object> items = castToMap(body.get("items"), ErrorCode.SHOW_API_ITEMS_MALFORMED);
        Object itemObj = items.get("item");

        // 'itemObj'가 실제로 List<Map<String, Object>> 형태라고 가정하고 형변환
        // 컴파일러는 타입 안정성을 보장할 수 없기 때문에 unchecked cast 경고가 발생-!
        // -> 이 경고를 무시하기 위해 @SuppressWarnings("unchecked")를 선언했다고 생각하면 됨
        if (itemObj instanceof List<?> itemList) {
            return (List<Map<String, Object>>) itemList;
        }

        // 그 외는 형식 오류로 예외 처리
        throw new BusinessException(ErrorCode.SHOW_API_ITEM_MALFORMED, ErrorCode.SHOW_API_ITEM_MALFORMED.getMessage());
    }

    // 개별 공연 항목 Map을 ShowResponseDto로 변환
    private ShowResponseDto toDto(Map<String, Object> item) {
        return new ShowResponseDto(
                (String) item.getOrDefault("title", ""),              // 공연 제목
                (String) item.getOrDefault("viewCount", ""),               // 공연 분류명
                (String) item.getOrDefault("description", ""),        // 공연 설명
                (String) item.getOrDefault("url", "")                 // 상세 페이지 링크
        );
    }

    // 위에서 설명한 상황과 유사하다고 생각하면 됨. (정확히는 Map<String, Object> 캐스팅은 컴파일러가 타입 안정성을 확인할 수 없기 때문)
    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj, ErrorCode errorCode) {
        // obj가 Map 타입이 아닌 경우 예외를 발생
        if (!(obj instanceof Map)) {
            // 비즈니스 로직에 정의된 에러 코드와 메시지를 포함한 예외를 던짐
            throw new BusinessException(errorCode, errorCode.getMessage());
        }

        // Map 타입이 확인 -> 따라서 안전하게 형변환하여 반환-!
        return (Map<String, Object>) obj;
    }
}
