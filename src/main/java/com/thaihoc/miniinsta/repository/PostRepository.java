package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
  List<Post> findByIdIn(List<Integer> ids);

  List<Post> findByCreatedBy(Profile createdBy);

  // @Query(value = "select * from post where created_by_id in :ids order by
  // created_at desc LIMIT :limit OFFSET :offset", nativeQuery = true)
  // List<Post> findByCreatedBy(@Param("ids") List<Integer> createdByIdList,
  // @Param(value = "limit") int limit,
  // @Param(value = "offset") int offset);

  Page<Post> findByCreatedByIn(List<Integer> createdByIdList, Pageable pageable);

  // @Query(value = "select count(*) from post where created_by_id in :ids",
  // nativeQuery = true)
  // int countByCreatedByIn(@Param("ids") List<Integer> createdByIdList);
}