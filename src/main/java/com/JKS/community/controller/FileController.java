package com.JKS.community.controller;

import com.JKS.community.entity.File;
import com.JKS.community.repository.FileRepository;
import com.JKS.community.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class FileController {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("upload") MultipartFile multipartFile) {
        try {
            File file = fileStorageService.storeFile(multipartFile);
            fileRepository.save(file);
            return ResponseEntity.ok().body(Collections.singletonMap("url", "/files/" + file.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
