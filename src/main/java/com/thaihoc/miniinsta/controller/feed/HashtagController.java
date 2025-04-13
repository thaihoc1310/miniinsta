package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CreateHashtagRequest;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.service.feed.HashtagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/hashtags")
public class HashtagController {

    private final HashtagService hashtagService;

    public HashtagController(HashtagService hashtagService) {
        this.hashtagService = hashtagService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hashtag> getHashtagById(@PathVariable long id) throws IdInvalidException {
        return ResponseEntity.ok(hashtagService.getHashtagById(id));
    }

    @PostMapping
    public ResponseEntity<Hashtag> createHashtag(@Valid @RequestBody CreateHashtagRequest request)
            throws AlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(hashtagService.createHashtag(request.getName()));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> searchHashtags(
            @RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(hashtagService.searchHashtags(q, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHashtag(@PathVariable long id) throws IdInvalidException {
        hashtagService.deleteHashtag(id);
        return ResponseEntity.noContent().build();
    }
}