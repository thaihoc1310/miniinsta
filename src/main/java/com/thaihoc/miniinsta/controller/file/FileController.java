package com.thaihoc.miniinsta.controller.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.DownloadImageResponse;
import com.thaihoc.miniinsta.dto.UploadImageRequest;
import com.thaihoc.miniinsta.dto.UploadImageResponse;
import com.thaihoc.miniinsta.service.FileService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "api/v1/files")
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<UploadImageResponse> uploadImage(@Valid @RequestBody UploadImageRequest request) {
        try {
            String fileName = fileService.uploadImage(request.getBase64ImageString());
            log.info("Successfully uploaded image with filename: {}", fileName);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(UploadImageResponse.builder().url(fileName).build());
        } catch (Exception e) {
            log.error("Error uploading image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<DownloadImageResponse> downloadImage(@RequestParam("fileName") String fileName) {
        try {
            String url = fileService.downloadImage(fileName);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DownloadImageResponse.builder().url(url).build());
        } catch (Exception e) {
            log.error("Error downloading image {}: {}", fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
