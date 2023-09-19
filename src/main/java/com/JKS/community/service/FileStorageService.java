package com.JKS.community.service;

import com.JKS.community.entity.File;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!");
        }
    }

    public File storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file " + file.getOriginalFilename());
        }

        try {
            String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uuidName = getUUID() + "_" + originalName;
            Path path = Paths.get(uploadDir + "/" + uuidName);

            if (Files.exists(path)) {
                throw new RuntimeException("Error: File " + originalName + " already exists!");
            }

            Files.copy(file.getInputStream(), path);

            File storedFile = new File();
            storedFile.setOriginalName(originalName);
            storedFile.setUuidName(uuidName);
            storedFile.setType(file.getContentType());
            storedFile.setSize(file.getSize());
            storedFile.setPath(path.toString());
            storedFile.setUploadTime(LocalDateTime.now());

            return storedFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    public String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
