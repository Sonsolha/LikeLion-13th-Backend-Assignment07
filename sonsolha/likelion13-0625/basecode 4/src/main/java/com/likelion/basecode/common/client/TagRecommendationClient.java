package com.likelion.basecode.common.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TagRecommendationClient {

    // 외부 API 호출을 위한 HTTP 클라이언트
    private final RestTemplate restTemplate;
    // 태그 추천 API 엔드포인트 URL
    private final String apiUrl;

    public TagRecommendationClient(
            RestTemplate restTemplate,
            @Value("${tag.recommendation.api-url}") String apiUrl
    ) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    // 게시물 contents를 기반으로 추천 태그 목록 요청
    public List<String> getRecommendedTags(String contents) {
        // // HTTP 요청 헤더 설정 : Content-Type을 JSON으로 지정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 : contents 필드를 JSON 형태로 전달
        Map<String, String> body = Map.of("contents", contents);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // POST 요청 전송 후 응답 받기 (Map으로 변환)
        Map<String, List<String>> response = restTemplate.postForObject(apiUrl, request, Map.class);

        // 응답에서 "tags" 필드를 추출하여 반환 (null일 경우 빈 리스트 반환)
        return Optional.ofNullable(response)
                .map(r -> r.getOrDefault("tags", List.of()))
                .orElse(List.of());
    }
}
