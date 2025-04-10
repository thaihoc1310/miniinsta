package com.thaihoc.miniinsta.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long>, JpaSpecificationExecutor<Hashtag> {

    Optional<Hashtag> findByName(String name);

    // @Query("SELECT h FROM Hashtag h ORDER BY h.postCount DESC")
    // Page<Hashtag> findTrendingHashtags(Pageable pageable);

    @Query("SELECT h FROM Hashtag h WHERE LOWER(h.name) LIKE LOWER(CONCAT(:q, '%')) ORDER BY h.postCount DESC")
    Page<Hashtag> searchHashtags(@Param("q") String q, Pageable pageable);

    boolean existsByName(String name);
}