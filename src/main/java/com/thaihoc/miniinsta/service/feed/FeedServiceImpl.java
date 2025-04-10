package com.thaihoc.miniinsta.service.feed;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.FeedRepository;
import com.thaihoc.miniinsta.service.user.ProfileService;

@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private PostService postService;

    @Override
    public ResultPaginationDTO getFeedByProfileId(Pageable pageable, long profileId) throws IdInvalidException {
        Profile profile = profileService.getProfileById(profileId);

        // Get list of post IDs from pre-calculated feed
        List<Long> postIds = feedRepository.getFeed(profile.getId(),
                pageable.getPageSize(), pageable.getPageNumber() + 1);

        // Find posts from IDs
        List<Post> posts = postService.getPostsByIds(postIds);

        return createPaginationResult(posts, profile.getId(), pageable);
    }

    private ResultPaginationDTO createPaginationResult(List<Post> posts, long profileId, Pageable pageable)
            throws IdInvalidException {
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages((int) (this.feedRepository.getFeedSize(profileId) / pageable.getPageSize()) + 1);
        mt.setTotal(this.feedRepository.getFeedSize(profileId));
        rs.setMeta(mt);
        List<PostResponse> listPost = new ArrayList<>();
        for (Post post : posts) {
            listPost.add(postService.convertToPostResponse(post));
        }
        rs.setResult(listPost);
        return rs;
    }
}