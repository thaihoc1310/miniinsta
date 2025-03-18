package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.service.feed.FeedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getFeed(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(feedService.getFeed(userPrincipal, pageable));
    }

    @GetMapping("/explore")
    public ResponseEntity<Page<PostResponse>> getExploreFeed(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(feedService.getExploreFeed(userPrincipal, pageable));
    }

    @GetMapping("/hashtag/{hashtag}")
    public ResponseEntity<Page<PostResponse>> getHashtagFeed(
            Authentication authentication,
            @PathVariable String hashtag,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(feedService.getHashtagFeed(userPrincipal, hashtag, pageable));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<Page<PostResponse>> getLocationFeed(
            Authentication authentication,
            @PathVariable String location,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(feedService.getLocationFeed(userPrincipal, location, pageable));
    }

    @PostMapping("/rebuild/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rebuildUserFeed(@PathVariable int profileId) {
        feedService.rebuildUserFeed(profileId);
        return ResponseEntity.ok().build();
    }
}
