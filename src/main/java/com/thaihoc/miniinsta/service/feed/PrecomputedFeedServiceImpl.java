package com.thaihoc.miniinsta.service.feed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.GetFeedResponse;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.FeedRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.profile.ProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("precomputedFeedService")
public class PrecomputedFeedServiceImpl implements FeedService {
  @Autowired
  private ProfileService profileService;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private FeedRepository feedRepository;

  @Override
  public GetFeedResponse getFeed(UserPrincipal userPrincipal, int limit, int page) {
    Profile profile = profileService.getUserProfile(userPrincipal);

    List<Long> postIds = feedRepository.getFeed(profile.getId(), limit, page);
    log.info("postIds={}", postIds);

    List<Post> posts = postRepository.findAllById(postIds.stream().map(Long::intValue).toList());

    Long totalPost = feedRepository.getFeedSize(profile.getId());
    log.info("totalPost={}", totalPost);
    int totalPage = (int) Math.ceil((double) totalPost / limit);

    return GetFeedResponse.builder()
        .posts(posts).totalPage(totalPage).build();
  }

  @Override
  public GetFeedResponse getFeed(UserPrincipal userPrincipal, Pageable pageable) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getFeed'");
  }

}
