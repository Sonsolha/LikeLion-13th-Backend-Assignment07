package com.likelion.basecode.show.api.dto;

import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.common.template.ApiResTemplate;
import com.likelion.basecode.show.api.dto.response.ShowListResponseDto;
import com.likelion.basecode.show.application.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;

    @GetMapping("/all")
    public ApiResTemplate<ShowListResponseDto> getAllshows() {
        ShowListResponseDto showListResponseDto = showService.fetchAllRecommendedShows();
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, showListResponseDto);
    }

    @GetMapping("/recommedations")
    public ApiResTemplate<ShowListResponseDto> recommedShows(@RequestParam Long postId) {
        ShowListResponseDto showListResponseDto = showService.recommendShowsByPostId(postId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, showListResponseDto);
    }

}
