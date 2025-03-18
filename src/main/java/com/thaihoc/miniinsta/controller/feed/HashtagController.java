package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.hashtag.CreateHashtagRequest;
import com.thaihoc.miniinsta.dto.hashtag.HashtagResponse;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.service.feed.HashtagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "api/v1/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("/{name}")
    public ResponseEntity<HashtagResponse> getHashtagByName(@PathVariable String name) {
        Hashtag hashtag = hashtagService.getHashtagByName(name);
        HashtagResponse response = HashtagResponse.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .postCount(hashtag.getPostCount())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<HashtagResponse> createHashtag(@Valid @RequestBody CreateHashtagRequest request) {
        Hashtag hashtag = hashtagService.createHashtagIfNotExists(request.getName());
        HashtagResponse response = HashtagResponse.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .postCount(hashtag.getPostCount())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HashtagResponse>> searchHashtags(
            @RequestParam String searchTerm,
            Pageable pageable) {
        return ResponseEntity.ok(hashtagService.searchHashtags(searchTerm, pageable));
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<HashtagResponse>> getTrendingHashtags(Pageable pageable) {
        return ResponseEntity.ok(hashtagService.getTrendingHashtags(pageable));
    }

    // @GetMapping("/{name}/posts")
    // public ResponseEntity<List<Post>> getPostsByHashtag(
    // @PathVariable String name,
    // @RequestParam(defaultValue = "10") int limit) {
    // return ResponseEntity.ok(hashtagService.getPostsByHashtag(name, limit));
    // }

    // @DeleteMapping("/posts/{postId}/hashtags/{hashtagName}")
    // public ResponseEntity<Void> removeHashtagFromPost(
    // Authentication authentication,
    // @PathVariable int postId,
    // @PathVariable String hashtagName) {
    // // Check if user has permission to modify the post (owner check)
    // // This validation is simplified - in a real app, check if user owns the post
    // hashtagService.removeHashtagFromPost(postId, hashtagName);
    // return ResponseEntity.ok().build();
    // }

    @PostMapping("/update-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateHashtagPostCount() {
        hashtagService.updateHashtagPostCount();
        return ResponseEntity.ok().build();
    }
}