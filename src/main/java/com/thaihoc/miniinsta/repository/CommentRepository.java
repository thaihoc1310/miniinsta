package com.thaihoc.miniinsta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
