package com.likelion.basecode.book.api.dto.response;

public class BookResponseDto {
    private String title;
    private String alternativeTitle;
    private String author;
    private String url;

    public BookResponseDto(String title, String alternativeTitle, String author, String url) {
        this.title = title != null && !title.isBlank() ? title : "제목 없음";
        this.alternativeTitle = alternativeTitle != null && !alternativeTitle.isBlank() ? alternativeTitle : "정보 없음";
        this.author = author != null && !author.isBlank() ? author : "정보 없음";
        this.url = url != null && !url.isBlank() ? url : "정보 없음";
    }

    public String getTitle() {
        return title;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }
}
