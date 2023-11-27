package com.JKS.community.service;

import com.JKS.community.entity.File;
import com.JKS.community.repository.FileRepository;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileStorageService {
    private final FileRepository fileRepository;

    @Value("${cloud.azure.storage.blob.connection-string}")
    private String connectionString;

    @Value("${cloud.azure.storage.blob.container-name}")
    private String containerName;

    public String uploadFile(MultipartFile multipartFile) {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();

            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

            String fileName = multipartFile.getOriginalFilename() + ":" + UUID.randomUUID();
            BlobClient blob = blobContainerClient.getBlobClient(fileName);

            InputStream inputStream = multipartFile.getInputStream();
            blob.upload(inputStream, multipartFile.getSize(), true);

            File file = File.of(multipartFile.getOriginalFilename(), fileName, multipartFile.getContentType(), multipartFile.getSize(), blob.getBlobUrl());

            fileRepository.save(file);

            return blob.getBlobUrl();
        } catch (IOException e) {
            throw new RuntimeException("파일을 저장하지 못했습니다. 다시 시도해주세요.", e);
        }
    }
}
