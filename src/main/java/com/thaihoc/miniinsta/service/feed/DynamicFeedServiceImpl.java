package com.thaihoc.miniinsta.service.feed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.GetFeedResponse;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.UserFollowing;
import com.thaihoc.miniinsta.repository.FollowerRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.profile.ProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("dynamicFeedService")
public class DynamicFeedServiceImpl implements FeedService {
  @Autowired
  private ProfileService profileService;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private FollowerRepository followerRepository;

  @Override
  public GetFeedResponse getFeed(UserPrincipal userPrincipal, Pageable pageable) {
    Profile profile = profileService.getUserProfile(userPrincipal);

    List<UserFollowing> followings = followerRepository.findByFollowerUserId(profile.getId());
    List<Integer> followingProfileIdList = followings.stream().map(following -> following.getFollowingUserId())
        .toList();
    log.info("followingProfileIdList={}", followingProfileIdList);
    // int totalPost = postRepository.countByCreatedByIn(followingProfileIdList);
    // log.info("totalPost={}", totalPost);
    // int totalPage = (int) Math.ceil((double) totalPost / limit);
    // int offset = (page - 1) * limit;

    // List<Post> posts = postRepository
    // .findByCreatedBy(followingProfileIdList, limit, offset);
    Page<Post> postsPage = postRepository.findByCreatedByIn(followingProfileIdList, pageable);
    List<Post> posts = postsPage.getContent();
    return GetFeedResponse.builder()
        .posts(posts).totalPage(postsPage.getTotalPages()).build();
  }

  @Override
  public GetFeedResponse getFeed(UserPrincipal userPrincipal, int limit, int page) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getFeed with limit and page'");
  }

}
