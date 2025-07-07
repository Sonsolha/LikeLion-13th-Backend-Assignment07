package com.likelion.basecode.post.api;

import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.common.template.ApiResTemplate;
import com.likelion.basecode.post.api.dto.response.PostInfoResponseDto;
import com.likelion.basecode.post.api.dto.response.PostListResponseDto;
import com.likelion.basecode.post.api.dto.request.PostSaveRequestDto;
import com.likelion.basecode.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.basecode.post.application.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    // 게시물 저장
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResTemplate<PostInfoResponseDto> postSave(
            @RequestPart("post") @Valid PostSaveRequestDto postSaveRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        PostInfoResponseDto postInfoResponseDto = postService.postSave(postSaveRequestDto, imageFile);
        return ApiResTemplate.successResponse(SuccessCode.POST_SAVE_SUCCESS, postInfoResponseDto);
    }

    // 게시글 목록 조회
    @GetMapping("/{memberId}")
    public ApiResTemplate<PostListResponseDto> myPostFindAll(@PathVariable("memberId") Long memberId) {
        PostListResponseDto postListResponseDto = postService.postFindMember(memberId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, postListResponseDto);
    }

    // 게시물 수정
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResTemplate<PostInfoResponseDto> postUpdate(
            @PathVariable("postId") Long postId,
            @RequestPart("post") @Valid PostUpdateRequestDto postUpdateRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        PostInfoResponseDto postInfoResponseDto = postService.postUpdate(postId, postUpdateRequestDto, imageFile);
        return ApiResTemplate.successResponse(SuccessCode.POST_UPDATE_SUCCESS, postInfoResponseDto);
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ApiResTemplate<String> postDelete(@PathVariable("postId") Long postId) {
        postService.postDelete(postId);
        return ApiResTemplate.successWithNoContent(SuccessCode.POST_DELETE_SUCCESS);
    }

    // 게시물 이미지 삭제
    @DeleteMapping("/{postId}/image")
    public ApiResTemplate<String> postImageDelete(@PathVariable("postId") Long postId) {
        postService.postImageDelete(postId);
        return ApiResTemplate.successWithNoContent(SuccessCode.POST_DELETE_SUCCESS);
    }
}
