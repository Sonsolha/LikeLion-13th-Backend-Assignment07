package com.likelion.basecode.posttag.domain.repository;

import com.likelion.basecode.post.domain.Post;
import com.likelion.basecode.posttag.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    // @Modifying : 데이터 변경 쿼리임을 명시
    // clearAutomatically = true : 영속성 컨텍스트 자동 초기화하여 반영
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PostTag pt WHERE pt.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}