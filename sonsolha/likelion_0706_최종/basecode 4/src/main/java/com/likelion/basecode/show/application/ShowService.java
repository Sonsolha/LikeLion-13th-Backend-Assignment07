package com.likelion.basecode.show.application;

import com.likelion.basecode.common.client.ShowSearchClient;
import com.likelion.basecode.common.client.TagRecommendationClient;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import com.likelion.basecode.post.domain.Post;
import com.likelion.basecode.post.domain.repository.PostRepository;
import com.likelion.basecode.show.api.dto.response.ShowListResponseDto;
import com.likelion.basecode.show.api.dto.response.ShowResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final PostRepository postRepository;
    private final TagRecommendationClient tagClient;
    private final ShowSearchClient showSearchClient;

    // 전체 공연 목록 조회
    public ShowListResponseDto fetchAllRecommendedShows() {
        List<ShowResponseDto> shows = showSearchClient.fetchAllShows();
        return new ShowListResponseDto(shows);
    }

    // 특정 게시글의 특정 추천 태그를 기반으로 공연 추천
    public ShowListResponseDto recommendShowsByPostId(long postId) {
        // 1.게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION,
                        ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage()));

        // 2. AI 기반 태그 추천
        List<String> tags = tagClient.getRecommendedTags(post.getContents());

        // 3. 태그 추천 결과가 비어있는 경우 예외 처리
        if (tags.isEmpty()) {
            throw new BusinessException(ErrorCode.TAG_RECOMMENDATION_EMPTY,
                    ErrorCode.TAG_RECOMMENDATION_EMPTY.getMessage());
        }

        // 4. 전체 공연 목록 조회
        List<ShowResponseDto> allShows = showSearchClient.fetchAllShows();

        // 5. description에 태그가 포함된 공연만 필터링
        List<ShowResponseDto> filteredShows = filterShowsBydescription(allShows, tags);

        // 6. 필터링 결과가 비어있으면 예외 처리
        if (filteredShows.isEmpty()) {
            throw new BusinessException(ErrorCode.SHOW_API_NO_RESULT, ErrorCode.SHOW_API_NO_RESULT.getMessage());
        }

        // 7. 최종 결과 반환
        return new ShowListResponseDto(filteredShows);
    }

    // 공연 목록에서 description에 태그가 포함된 공연을 필터링
    private List<ShowResponseDto> filterShowsBydescription(
            List<ShowResponseDto> shows, List<String> tags) {
        return shows.stream()
                .filter(show ->
                        tags.stream().anyMatch(tag -> {
                            String description = Optional.ofNullable(show.description()).orElse("");
                            return description.contains(tag);
                        })
                )
                .limit(3)
                .toList();
    }
}

