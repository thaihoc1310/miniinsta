package com.thaihoc.miniinsta.service.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.repository.HashtagRepository;

@Service
public class HashtagServiceImpl implements HashtagService {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

    private HashtagRepository hashtagRepository;

    public HashtagServiceImpl(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    @Override
    public Hashtag getHashtagById(long id) throws IdInvalidException {
        return hashtagRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Hashtag not found"));
    }

    @Override
    public Hashtag getHashtagByName(String name) throws IdInvalidException {
        return hashtagRepository.findByName(name)
                .orElseThrow(() -> new IdInvalidException("Hashtag not found"));
    }

    @Override
    @Transactional
    public Hashtag createHashtag(String name) throws AlreadyExistsException {
        // Normalize hashtag name
        String normalizedName = (name.startsWith("#") ? name.substring(1) : name).toLowerCase();

        if (isHashtagExists(normalizedName)) {
            throw new AlreadyExistsException("Hashtag already exists");
        }

        Hashtag hashtag = Hashtag.builder().name(normalizedName).postCount(0).build();
        return hashtagRepository.save(hashtag);
    }

    private boolean isHashtagExists(String name) {
        return hashtagRepository.existsByName(name);
    }

    @Override
    public List<Hashtag> extractHashtagsFromText(String text) throws AlreadyExistsException, IdInvalidException {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> hashtagNames = new ArrayList<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(text);

        while (matcher.find()) {
            hashtagNames.add(matcher.group(1).toLowerCase());
        }

        List<Hashtag> hashtags = new ArrayList<>();
        for (String name : hashtagNames) {
            Hashtag hashtag = null;
            try {
                hashtag = this.createHashtag(name);
            } catch (AlreadyExistsException e) {
                hashtag = this.getHashtagByName(name);
            }
            hashtags.add(hashtag);
        }
        return hashtags;
    }

    @Override
    public ResultPaginationDTO searchHashtags(String q, Pageable pageable) {
        Page<Hashtag> hashtags = hashtagRepository.searchHashtags(q, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(hashtags.getTotalPages());
        mt.setTotal(hashtags.getTotalElements());
        rs.setMeta(mt);
        List<Hashtag> listHashtag = hashtags.getContent();
        rs.setResult(listHashtag);
        return rs;
    }

    @Override
    @Transactional
    public void updateHashtagPostCount(List<Hashtag> hashtags, int quantity) {
        for (Hashtag hashtag : hashtags) {
            hashtag.setPostCount(hashtag.getPostCount() + quantity);
            hashtagRepository.save(hashtag);
        }
    }

    @Override
    public void deleteHashtag(long id) throws IdInvalidException {
        Hashtag hashtag = this.getHashtagById(id);
        hashtagRepository.delete(hashtag);
    }
}