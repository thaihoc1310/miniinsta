package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.LikePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.service.feed.PostService;
import com.thaihoc.miniinsta.service.user.ProfileService;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/")

public class PostController {

    private final PostService postService;
    private final ProfileService profileService;

    public PostController(PostService postService, ProfileService profileService) {
        this.postService = postService;
        this.profileService = profileService;
    }

    @PostMapping("profiles/{profileId}/posts")
    public ResponseEntity<Post> createPost(
            @PathVariable long profileId,
            @Valid @RequestBody CreatePostRequest request) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(profileId, request));
    }

    @PutMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable long profileId,
            @PathVariable long postId,
            @Valid @RequestBody UpdatePostRequest request) throws IdInvalidException {
        return ResponseEntity.ok(postService.updatePost(profileId, postId, request));
    }

    @GetMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable long profileId,
            @PathVariable long postId) throws IdInvalidException {
        return ResponseEntity.ok(postService.getPostById(postId, profileId));
    }

    @DeleteMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<Void> deletePostById(
            @PathVariable long profileId,
            @PathVariable long postId) throws IdInvalidException {
        postService.deletePostById(profileId, postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("posts/{postId}/likes")
    public ResponseEntity<Void> likePost(
            @PathVariable long postId,
            @Valid @RequestBody LikePostRequest request) throws IdInvalidException {
        postService.likePost(postId, request.getLikerId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("posts/{postId}/likes/{likerId}")
    public ResponseEntity<Void> unlikePost(
            @PathVariable long postId,
            @PathVariable long likerId) throws IdInvalidException {
        postService.unlikePost(postId, likerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("profiles/{profileId}/posts")
    public ResponseEntity<ResultPaginationDTO> getAllPostsByProfileId(
            @PathVariable long profileId,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(postService.getAllPostsByProfileId(profileId, pageable));
    }

    @GetMapping("posts")
    public ResponseEntity<ResultPaginationDTO> getAllPosts(
            @Filter Specification<Post> spec,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(postService.getAllPosts(spec, pageable));
    }

    @GetMapping("profiles/{profileId}/liked_posts")
    public ResponseEntity<ResultPaginationDTO> getLikedPosts(
            @PathVariable long profileId,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(postService.getLikedPostsByProfileId(profileId, pageable));
    }

    @GetMapping("hashtags/{hashtag}/posts")
    public ResponseEntity<ResultPaginationDTO> getAllPostsByHashtag(
            @PathVariable String hashtag,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(postService.getPostsByHashtag(hashtag, pageable));
    }

    // /**
    // * Get popular posts
    // */
    // @GetMapping("/popular")
    // public ResponseEntity<Page<PostResponse>> getPopularPosts(
    // Authentication authentication,
    // Pageable pageable) {
    // UserPrincipal userPrincipal = null;
    // if (authentication != null && authentication.isAuthenticated()) {
    // userPrincipal = (UserPrincipal) authentication.getPrincipal();
    // }
    // return ResponseEntity.ok(postService.getPopularPosts(userPrincipal,
    // pageable));
    // }

    @GetMapping("posts/{postId}/likes")
    public ResponseEntity<ResultPaginationDTO> getPostLikers(
            @PathVariable long postId,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(this.profileService.getPostLikers(postId, pageable));
    }
}
