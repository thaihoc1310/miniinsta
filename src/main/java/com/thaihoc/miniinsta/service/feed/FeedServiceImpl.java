package com.thaihoc.miniinsta.service.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.exception.HashtagNotFoundException;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.FeedRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.hashtag.HashtagService;
import com.thaihoc.miniinsta.service.profile.ProfileService;

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
    private HashtagService hashtagService;

    @Autowired
    private PostService postService;

    @Override
    public Page<PostResponse> getFeed(UserPrincipal userPrincipal, Pageable pageable) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);

        // Lấy danh sách ID của post từ feed đã tính toán sẵn
        List<Integer> postIds = feedRepository.getFeed(profile.getId(),
                pageable.getPageSize(), pageable.getPageNumber() + 1);

        if (postIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Tìm các bài post từ ID
        List<Post> posts = postRepository.findByIdIn(postIds);

        // Sắp xếp theo thứ tự trong danh sách postIds
        posts.sort((a, b) -> {
            int indexA = postIds.indexOf(a.getId());
            int indexB = postIds.indexOf(b.getId());
            return Integer.compare(indexA, indexB);
        });

        // Chuyển đổi thành PostResponse
        List<PostResponse> postResponses = posts.stream()
                .map(post -> postService.getPost(userPrincipal, post.getId()))
                .collect(Collectors.toList());

        // Tạo Page từ danh sách PostResponse
        long totalElements = feedRepository.getFeedSize(profile.getId());
        return new PageImpl<>(postResponses, pageable, totalElements);
    }

    @Override
    public Page<PostResponse> getExploreFeed(UserPrincipal userPrincipal, Pageable pageable) {
        // Lấy danh sách ID của post từ explore feed
        List<Integer> postIds = feedRepository.getExplore(
                pageable.getPageSize(), pageable.getPageNumber() + 1);

        if (postIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Tìm các bài post từ ID
        List<Post> posts = postRepository.findByIdIn(postIds);

        // Sắp xếp theo thứ tự trong danh sách postIds
        posts.sort((a, b) -> {
            int indexA = postIds.indexOf(a.getId());
            int indexB = postIds.indexOf(b.getId());
            return Integer.compare(indexA, indexB);
        });

        // Chuyển đổi thành PostResponse
        List<PostResponse> postResponses = posts.stream()
                .map(post -> postService.getPost(userPrincipal, post.getId()))
                .collect(Collectors.toList());

        // Sử dụng một số cố định cho tổng số phần tử trong explore feed
        long totalElements = 5000; // Giới hạn của explore feed
        return new PageImpl<>(postResponses, pageable, totalElements);
    }

    @Override
    public Page<PostResponse> getHashtagFeed(UserPrincipal userPrincipal, String hashtag, Pageable pageable) {
        try {
            // Kiểm tra hashtag có tồn tại không
            hashtagService.getHashtagByName(hashtag);

            // Sử dụng PostService để lấy bài đăng theo hashtag
            return postService.getPostsByHashtag(userPrincipal, hashtag, pageable);
        } catch (HashtagNotFoundException e) {
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<PostResponse> getLocationFeed(UserPrincipal userPrincipal, String location, Pageable pageable) {
        // Sử dụng PostService để lấy bài đăng theo vị trí
        return postService.getPostsByLocation(userPrincipal, location, pageable);
    }

    @Override
    @Transactional
    public void updateFeedsWithNewPost(int postId) {
        try {
            Post post = postRepository.findById(postId).orElse(null);
            if (post == null) {
                return;
            }

            // Thêm bài đăng vào feed của người tạo
            feedRepository.addPostToFeed(postId, post.getCreatedBy().getId());

            // Thêm bài đăng vào feed của người theo dõi
            List<Integer> followerIds = post.getCreatedBy().getFollowers().stream()
                    .map(Profile::getId)
                    .collect(Collectors.toList());
            feedRepository.addPostToMultipleFeeds(postId, followerIds);

            // Thêm vào explore nếu profile không private
            if (!post.getCreatedBy().isPrivate()) {
                feedRepository.addPostToExplore(postId);
            }

            // Thêm vào feed theo hashtag
            for (Hashtag hashtag : post.getHashtags()) {
                feedRepository.addPostToHashtagFeed(postId, hashtag.getName());
            }

            log.info("Updated feeds with new post: {}", postId);
        } catch (Exception e) {
            log.error("Error updating feeds with new post {}: {}", postId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removePostFromFeeds(int postId) {
        try {
            feedRepository.removePostFromFeeds(postId);
            log.info("Removed post from feeds: {}", postId);
        } catch (Exception e) {
            log.error("Error removing post {} from feeds: {}", postId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void rebuildUserFeed(int profileId) {
        try {
            Profile profile = profileService.getProfileById(profileId);

            // Lấy danh sách người theo dõi
            List<Profile> following = new ArrayList<>(profile.getFollowing());

            // Lấy danh sách ID của người theo dõi
            List<Integer> followingIds = following.stream()
                    .map(Profile::getId)
                    .collect(Collectors.toList());

            // Lấy tất cả bài đăng của những người mà người dùng theo dõi
            for (Integer followingId : followingIds) {
                List<Post> posts = postRepository.findByCreatedBy(
                        profileService.getProfileById(followingId),
                        Pageable.ofSize(100)).getContent();

                for (Post post : posts) {
                    feedRepository.addPostToFeed(post.getId(), profileId);
                }
            }

            log.info("Rebuilt feed for user: {}", profileId);
        } catch (Exception e) {
            log.error("Error rebuilding feed for user {}: {}", profileId, e.getMessage());
        }
    }
}