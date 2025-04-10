package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.IdInvalidException;

public interface FeedService {

  ResultPaginationDTO getFeedByProfileId(Pageable pageable, long profileId) throws IdInvalidException;

}
