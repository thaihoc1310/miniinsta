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

@RestController
@RequestMapping(path = "api/v1/file")
public class FileController {
    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<UploadImageResponse> postMethodName(@Valid @RequestBody UploadImageRequest request) {
        String fileName = fileService.uploadImage(request.getBase64ImageString());
        return ResponseEntity.status(HttpStatus.OK).body(UploadImageResponse.builder().url(fileName).build());
    }

    @GetMapping("/download")
    public ResponseEntity<DownloadImageResponse> getMethodName(@RequestParam("fileName") String fileName) {
        String url = fileService.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DownloadImageResponse.builder().url(url).build());
    }
}
