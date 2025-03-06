package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
  List<Post> findByIdIn(List<Integer> ids);

  List<Post> findByCreatedBy(Profile createdBy);

}
