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

        // Tối ưu: giữ thứ tự đúng như trong Redis
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

        // Tối ưu: giữ thứ tự đúng như trong Redis
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
    @Transactional
    public void removePostFromFeeds(int postId) {
        try {
            // Xóa post khỏi tất cả các feed
            feedRepository.removePostFromFeeds(postId);
            log.info("Đã xóa bài đăng khỏi tất cả feed: {}", postId);
        } catch (Exception e) {
            log.error("Lỗi khi xóa bài đăng {} khỏi feed: {}", postId, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void rebuildUserFeed(int profileId) {
        try {
            Profile profile = profileService.getProfileById(profileId);
            log.info("Bắt đầu xây dựng lại feed cho người dùng ID: {}", profileId);

            // Lấy danh sách người mà user đang theo dõi
            Set<Profile> following = profile.getFollowing();

            if (following == null || following.isEmpty()) {
                log.info("Người dùng {} không theo dõi ai, không cần xây dựng lại feed", profileId);
                return;
            }

            // Lấy danh sách ID của người đang theo dõi
            List<Integer> followingIds = following.stream()
                    .map(Profile::getId)
                    .collect(Collectors.toList());

            log.info("Người dùng {} đang theo dõi {} người dùng khác", profileId, followingIds.size());

            // Xóa feed hiện tại trước khi xây dựng lại
            feedRepository.clearUserFeed(profileId);
            log.info("Đã xóa feed cũ của người dùng ID: {}", profileId);

            // Lấy các bài post từ những người mà user đang theo dõi
            for (Integer followingId : followingIds) {
                List<Post> posts = postRepository.findByCreatedByIdOrderByCreatedAtDesc(followingId,
                        Pageable.ofSize(100))
                        .getContent();

                log.info("Tìm thấy {} bài đăng từ người dùng ID {}", posts.size(), followingId);

                // Thêm tất cả post vào feed của user theo thứ tự thời gian
                for (Post post : posts) {
                    feedRepository.addPostToFeed(post.getId(), profileId);
                }
            }

            log.info("Đã xây dựng lại feed thành công cho người dùng: {}", profileId);
        } catch (Exception e) {
            log.error("Lỗi khi xây dựng lại feed cho người dùng {}: {}", profileId, e.getMessage(), e);
        }
    }
}