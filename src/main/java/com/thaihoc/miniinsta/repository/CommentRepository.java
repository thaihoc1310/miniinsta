package com.thaihoc.miniinsta.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Comment;
import com.thaihoc.miniinsta.model.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    Page<Comment> findByPost(Post post, Pageable pageable);

    Page<Comment> findByParentComment(Comment parentComment, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c JOIN c.likes l WHERE l.id = :profileId AND c.id = :commentId")
    int isCommentLikedByProfile(@Param("commentId") Long commentId, @Param("profileId") Long profileId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.likeCount DESC")
    Page<Comment> findMostLikedComments(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentCommentId ORDER BY c.createdAt DESC")
    Page<Comment> findByParentCommentOrderByCreatedAtDesc(@Param("parentCommentId") Long parentCommentId,
            Pageable pageable);
}
