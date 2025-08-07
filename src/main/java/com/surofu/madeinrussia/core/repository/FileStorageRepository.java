package com.surofu.madeinrussia.core.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageRepository {
    String uploadImageToFolder(MultipartFile file, String folderName) throws IOException;

    String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException;

    void deleteMediaByLink(String ...links) throws Exception;
}
