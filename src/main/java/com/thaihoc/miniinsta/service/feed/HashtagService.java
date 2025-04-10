package com.thaihoc.miniinsta.service.feed;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Hashtag;

public interface HashtagService {

    Hashtag getHashtagById(long id) throws IdInvalidException;

    Hashtag createHashtag(String name) throws AlreadyExistsException;

    Hashtag getHashtagByName(String name) throws IdInvalidException;

    List<Hashtag> extractHashtagsFromText(String text) throws AlreadyExistsException, IdInvalidException;

    ResultPaginationDTO searchHashtags(String q, Pageable pageable);

    void updateHashtagPostCount(List<Hashtag> hashtags, int quantity);

    void deleteHashtag(long id) throws IdInvalidException;
}