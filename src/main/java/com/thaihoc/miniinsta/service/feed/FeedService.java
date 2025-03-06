package com.thaihoc.miniinsta.service.feed;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.GetFeedResponse;

public interface FeedService {
  GetFeedResponse getFeed(UserPrincipal userPrincipal, int limit, int page);
}
