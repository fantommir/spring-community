package com.JKS.community.controller;

import com.JKS.community.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("upload") MultipartFile file) {
        String storedFileUrl = fileStorageService.uploadFile(file);
        String response = "{\"url\": \"" + storedFileUrl + "\"}";

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

