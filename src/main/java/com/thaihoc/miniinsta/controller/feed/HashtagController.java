package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.feed.CreateHashtagRequest;
import com.thaihoc.miniinsta.dto.feed.HashtagResponse;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.service.feed.HashtagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/hashtags")
public class HashtagController {

    private final HashtagService hashtagService;

    public HashtagController(HashtagService hashtagService) {
        this.hashtagService = hashtagService;
    }

    /**
     * Get hashtag information by name
     */
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

    /**
     * Create new hashtag
     */
    @PostMapping
    public ResponseEntity<HashtagResponse> createHashtag(@Valid @RequestBody CreateHashtagRequest request) {
        Hashtag hashtag = hashtagService.createHashtagIfNotExists(request.getName());
        HashtagResponse response = HashtagResponse.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .postCount(hashtag.getPostCount())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Search hashtags by keyword
     */
    @GetMapping
    public ResponseEntity<Page<HashtagResponse>> searchHashtags(
            @RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(hashtagService.searchHashtags(q, pageable));
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
}