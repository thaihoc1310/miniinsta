package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Comment;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findByPost(Post post, Pageable pageable);

    Page<Comment> findByPostAndParentCommentIsNull(Post post, Pageable pageable);

    Page<Comment> findByParentComment(Comment parentComment, Pageable pageable);

    Page<Comment> findByCreatedBy(Profile profile, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c JOIN c.likes l WHERE l.id = :profileId AND c.id = :commentId")
    int isCommentLikedByProfile(@Param("commentId") Integer commentId, @Param("profileId") Integer profileId);

    List<Comment> findTop5ByPostOrderByLikeCountDesc(Post post);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY SIZE(c.likes) DESC")
    Page<Comment> findMostLikedComments(@Param("postId") Integer postId, Pageable pageable);
}
