package com.thaihoc.miniinsta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

    /**
     * Tìm hashtag theo tên chính xác
     */
    Optional<Hashtag> findByName(String name);

    /**
     * Tìm hashtag theo tên một phần
     */
    List<Hashtag> findByNameContaining(String partialName);

    /**
     * Lấy danh sách hashtag thịnh hành theo số lượng bài viết
     */
    @Query("SELECT h FROM Hashtag h ORDER BY h.postCount DESC")
    Page<Hashtag> findTrendingHashtags(Pageable pageable);

    /**
     * Tìm kiếm hashtag theo từ khóa
     */
    @Query("SELECT h FROM Hashtag h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Hashtag> searchHashtags(@Param("q") String q, Pageable pageable);

    /**
     * Lấy danh sách hashtag của một bài đăng
     */
    @Query(value = "SELECT h.* FROM hashtag h " +
            "JOIN post_hashtags ph ON h.id = ph.hashtag_id " +
            "WHERE ph.post_id = :postId", nativeQuery = true)
    List<Hashtag> findHashtagsByPostId(@Param("postId") Integer postId);
}