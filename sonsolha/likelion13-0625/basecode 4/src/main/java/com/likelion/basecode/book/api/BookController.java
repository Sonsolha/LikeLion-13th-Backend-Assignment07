package com.likelion.basecode.book.api;

import com.likelion.basecode.book.api.dto.response.BookListResponseDto;
import com.likelion.basecode.book.application.BookService;
import com.likelion.basecode.common.error.SuccessCode;
import com.likelion.basecode.common.template.ApiResTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("/all")
    public ApiResTemplate<BookListResponseDto> getAllBooks() {
        BookListResponseDto bookListResponseDto = bookService.fetchAllRecommendedBooks();
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, bookListResponseDto);
    }

    @GetMapping("/recommendations")
    public ApiResTemplate<BookListResponseDto> recommendBooks(@RequestParam Long postId) {
        BookListResponseDto bookListResponseDto = bookService.recommendBooksByPostId(postId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, bookListResponseDto);
    }
}
