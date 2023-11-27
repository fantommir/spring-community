package com.JKS.community.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${cloud.azure.storage.blob.connection-string}")
    private String connectionString;

    @Value("${cloud.azure.storage.blob.container-name}")
    private String containerName;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("upload") MultipartFile file) {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();

            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

            String fileName = file.getOriginalFilename() + ":" + UUID.randomUUID();
            BlobClient blob = blobContainerClient.getBlobClient(fileName);

            InputStream inputStream = file.getInputStream();
            blob.upload(inputStream, file.getSize(), true);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("{\"url\": \"" + blob.getBlobUrl() + "\"}");

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

