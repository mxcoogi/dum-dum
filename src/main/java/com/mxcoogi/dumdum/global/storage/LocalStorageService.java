package com.mxcoogi.dumdum.global.storage;

import com.mxcoogi.dumdum.global.common.ResponseCode;
import com.mxcoogi.dumdum.global.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Profile("local")
public class LocalStorageService implements StorageService {

    @Value("${storage.local.base-path:./uploads}")
    private String basePath;

    @Value("${storage.local.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @Override
    public String upload(MultipartFile file, String directory) {
        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
        Path dirPath = Paths.get(basePath, directory);
        Path filePath = dirPath.resolve(filename);

        try {
            Files.createDirectories(dirPath);
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new ApiException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        return baseUrl + "/" + directory + "/" + filename;
    }

    @Override
    public void delete(String fileUrl) {
        String prefix = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        String relativePath = fileUrl.startsWith(prefix)
                ? fileUrl.substring(prefix.length())
                : fileUrl;

        Path rootPath = Paths.get(basePath).toAbsolutePath().normalize();
        Path filePath = rootPath.resolve(relativePath).normalize();

        // basePath 벗어나는 경로 차단 (path traversal 방지)
        if (!filePath.startsWith(rootPath)) {
            throw new ApiException(ResponseCode.INTERNAL_SERVER_ERROR);
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ApiException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
