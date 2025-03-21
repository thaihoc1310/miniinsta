package com.thaihoc.miniinsta.service.feed;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.feed.HashtagResponse;
import com.thaihoc.miniinsta.exception.HashtagNotFoundException;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.repository.HashtagRepository;
import com.thaihoc.miniinsta.repository.PostRepository;

@Service
public class HashtagServiceImpl implements HashtagService {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public Hashtag getHashtagByName(String name) {
        // Normalize hashtag name (remove # if present, lowercase)
        String normalizedName = name.startsWith("#") ? name.substring(1) : name;
        normalizedName = normalizedName.toLowerCase();

        return hashtagRepository.findByName(normalizedName)
                .orElseThrow(() -> new HashtagNotFoundException("Hashtag not found: " + name));
    }

    @Override
    @Transactional
    public Hashtag createHashtagIfNotExists(String name) {
        // Normalize hashtag name
        String normalizedName = (name.startsWith("#") ? name.substring(1) : name).toLowerCase();

        return hashtagRepository.findByName(normalizedName)
                .orElseGet(() -> {
                    Hashtag newHashtag = Hashtag.builder()
                            .name(normalizedName)
                            .postCount(0)
                            .build();
                    return hashtagRepository.save(newHashtag);
                });
    }

    @Override
    public Set<Hashtag> extractHashtagsFromText(String text) {
        if (text == null || text.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> hashtagNames = new HashSet<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(text);

        while (matcher.find()) {
            hashtagNames.add(matcher.group(1).toLowerCase());
        }

        // Create hashtag for each name
        return hashtagNames.stream()
                .map(this::createHashtagIfNotExists)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void linkPostWithHashtags(Post post, Set<Hashtag> hashtags) {
        // Remove existing hashtags
        post.getHashtags().clear();

        // Add new hashtags
        post.getHashtags().addAll(hashtags);

        // Update post
        postRepository.save(post);

        // Update postCount for each hashtag
        hashtags.forEach(hashtag -> {
            hashtag.setPostCount(hashtag.getPostCount() + 1);
            hashtagRepository.save(hashtag);
        });
    }

    @Override
    public Page<HashtagResponse> searchHashtags(String q, Pageable pageable) {
        Page<Hashtag> hashtags = hashtagRepository.searchHashtags(q, pageable);
        return hashtags.map(this::convertToHashtagResponse);
    }

    @Override
    public Page<HashtagResponse> getTrendingHashtags(Pageable pageable) {
        Page<Hashtag> trendingHashtags = hashtagRepository.findTrendingHashtags(pageable);
        return trendingHashtags.map(this::convertToHashtagResponse);
    }

    @Override
    public List<Post> getPostsByHashtag(String hashtagName, int limit) {
        Hashtag hashtag = getHashtagByName(hashtagName);
        return hashtag.getPosts().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<HashtagResponse> getHashtagsByPost(int postId) {
        List<Hashtag> hashtags = hashtagRepository.findHashtagsByPostId(postId);
        return hashtags.stream()
                .map(this::convertToHashtagResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeHashtagFromPost(int postId, String hashtagName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Hashtag hashtag = getHashtagByName(hashtagName);

        if (post.getHashtags().contains(hashtag)) {
            post.getHashtags().remove(hashtag);
            postRepository.save(post);

            hashtag.setPostCount(Math.max(0, hashtag.getPostCount() - 1));
            hashtagRepository.save(hashtag);
        }
    }

    @Override
    @Transactional
    public void updateHashtagPostCount() {
        List<Hashtag> allHashtags = hashtagRepository.findAll();

        for (Hashtag hashtag : allHashtags) {
            int postCount = hashtag.getPosts().size();
            hashtag.setPostCount(postCount);
            hashtagRepository.save(hashtag);
        }
    }

    // Helper method to convert from Hashtag to HashtagResponse
    private HashtagResponse convertToHashtagResponse(Hashtag hashtag) {
        return HashtagResponse.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .postCount(hashtag.getPostCount())
                .build();
    }
}