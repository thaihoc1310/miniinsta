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
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    /**
     * Lấy feed chính của người dùng hiện tại (Home feed)
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getFeed(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(feedService.getFeed(userPrincipal, pageable));
    }

    /**
     * Lấy feed khám phá (Explore feed)
     */
    @GetMapping("/explore")
    public ResponseEntity<Page<PostResponse>> getExploreFeed(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(feedService.getExploreFeed(userPrincipal, pageable));
    }

    /**
     * Xây dựng lại feed cho người dùng (API dành cho Admin)
     */
    @PostMapping("/users/{profileId}/rebuild")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rebuildUserFeed(@PathVariable int profileId) {
        feedService.rebuildUserFeed(profileId);
        return ResponseEntity.ok().build();
    }
}
