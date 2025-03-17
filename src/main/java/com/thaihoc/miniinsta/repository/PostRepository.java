package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
  List<Post> findByIdIn(List<Integer> ids);

  Page<Post> findByCreatedBy(Profile createdBy, Pageable pageable);

  Page<Post> findByCreatedByIn(List<Profile> createdByList, Pageable pageable);

  Page<Post> findByHashtags(Hashtag hashtag, Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.caption LIKE %:searchTerm%")
  Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);

  @Query("SELECT p FROM Post p JOIN p.userLikes u WHERE u.id = :profileId")
  Page<Post> findLikedPosts(@Param("profileId") Integer profileId, Pageable pageable);

  @Query("SELECT COUNT(p) FROM Post p JOIN p.userLikes u WHERE u.id = :profileId AND p.id = :postId")
  int isPostLikedByProfile(@Param("postId") Integer postId, @Param("profileId") Integer profileId);

  @Query("SELECT p FROM Post p WHERE SIZE(p.userLikes) > 0 ORDER BY SIZE(p.userLikes) DESC")
  Page<Post> findPopularPosts(Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.location LIKE %:location%")
  Page<Post> findPostsByLocation(@Param("location") String location, Pageable pageable);
}