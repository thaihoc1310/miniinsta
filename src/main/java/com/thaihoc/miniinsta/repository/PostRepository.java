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
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

  Optional<Post> findByIdAndAuthor(Long id, Profile author);

  @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h = :hashtag")
  Page<Post> findByHashtag(@Param("hashtag") Hashtag hashtag, Pageable pageable);

  Page<Post> findByAuthor(Profile author, Pageable pageable);

  @Query("SELECT p FROM Post p JOIN p.userLikes u WHERE u.id = :profileId")
  Page<Post> findLikedPosts(@Param("profileId") Long profileId, Pageable pageable);

  @Query("SELECT COUNT(p) FROM Post p JOIN p.userLikes u WHERE u.id = :profileId AND p.id = :postId")
  int isPostLikedByProfile(@Param("postId") Long postId, @Param("profileId") Long profileId);

  @Query("SELECT p FROM Post p WHERE SIZE(p.userLikes) > 0 ORDER BY SIZE(p.userLikes) DESC")
  Page<Post> findPopularPosts(Pageable pageable);

}