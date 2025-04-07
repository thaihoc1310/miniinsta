package com.thaihoc.miniinsta.service.feed;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.FeedRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.user.ProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private PostService postService;

    @Override
    public Page<PostResponse> getFeed(UserPrincipal userPrincipal, Pageable pageable) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);

        // Get list of post IDs from pre-calculated feed
        List<Integer> postIds = feedRepository.getFeed(profile.getId(),
                pageable.getPageSize(), pageable.getPageNumber() + 1);

        if (postIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Find posts from IDs
        List<Post> posts = postRepository.findByIdIn(postIds);

        // Optimize: maintain correct order as in Redis
        posts.sort((a, b) -> {
            int indexA = postIds.indexOf(a.getId());
            int indexB = postIds.indexOf(b.getId());
            return Integer.compare(indexA, indexB);
        });

        // Convert to PostResponse
        List<PostResponse> postResponses = posts.stream()
                .map(post -> postService.getPost(userPrincipal, post.getId()))
                .collect(Collectors.toList());

        // Create Page from PostResponse list
        long totalElements = feedRepository.getFeedSize(profile.getId());
        return new PageImpl<>(postResponses, pageable, totalElements);
    }

    @Override
    public Page<PostResponse> getExploreFeed(UserPrincipal userPrincipal, Pageable pageable) {
        // Get list of post IDs from explore feed
        List<Integer> postIds = feedRepository.getExplore(
                pageable.getPageSize(), pageable.getPageNumber() + 1);

        if (postIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Find posts from IDs
        List<Post> posts = postRepository.findByIdIn(postIds);

        // Optimize: maintain correct order as in Redis
        posts.sort((a, b) -> {
            int indexA = postIds.indexOf(a.getId());
            int indexB = postIds.indexOf(b.getId());
            return Integer.compare(indexA, indexB);
        });

        // Convert to PostResponse
        List<PostResponse> postResponses = posts.stream()
                .map(post -> postService.getPost(userPrincipal, post.getId()))
                .collect(Collectors.toList());

        // Use a fixed number for total elements in explore feed
        long totalElements = 5000; // Limit of explore feed
        return new PageImpl<>(postResponses, pageable, totalElements);
    }

    @Override
    @Transactional
    public void removePostFromFeeds(int postId) {
        try {
            // Remove post from all feeds
            feedRepository.removePostFromFeeds(postId);
            log.info("Post removed from all feeds: {}", postId);
        } catch (Exception e) {
            log.error("Error removing post {} from feeds: {}", postId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void rebuildUserFeed(int profileId) {
        try {
            Profile profile = profileService.getProfileById(profileId);
            log.info("Starting to rebuild feed for user ID: {}", profileId);

            // Get list of users that the current user is following
            Set<Profile> following = profile.getFollowing();

            if (following == null || following.isEmpty()) {
                log.info("User {} is not following anyone, no need to rebuild feed", profileId);
                return;
            }

            // Get list of IDs of users being followed
            List<Integer> followingIds = following.stream()
                    .map(Profile::getId)
                    .collect(Collectors.toList());

            log.info("User {} is following {} other users", profileId, followingIds.size());

            // Clear current feed before rebuilding
            feedRepository.clearUserFeed(profileId);
            log.info("Cleared old feed for user ID: {}", profileId);

            // Get posts from users that the current user is following
            for (Integer followingId : followingIds) {
                List<Post> posts = postRepository.findByCreatedByIdOrderByCreatedAtDesc(followingId,
                        Pageable.ofSize(100))
                        .getContent();

                log.info("Found {} posts from user ID {}", posts.size(), followingId);

                // Add all posts to user's feed in chronological order
                for (Post post : posts) {
                    feedRepository.addPostToFeed(post.getId(), profileId);
                }
            }

            log.info("Successfully rebuilt feed for user: {}", profileId);
        } catch (Exception e) {
            log.error("Error rebuilding feed for user {}: {}", profileId, e.getMessage(), e);
        }
    }
}