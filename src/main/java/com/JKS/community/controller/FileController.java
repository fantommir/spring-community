package com.JKS.community.controller;

import com.JKS.community.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 API")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "파일 업로드")
    public ResponseEntity<String> upload(@RequestParam("upload") MultipartFile file) {
        String storedFileUrl = fileStorageService.uploadFile(file);
        String response = "{\"url\": \"" + storedFileUrl + "\"}";

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{fileName}")
    @Operation(summary = "파일 삭제")
    public ResponseEntity<Void> delete(@PathVariable String fileName) {
        fileStorageService.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

