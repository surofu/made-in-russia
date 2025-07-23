package com.surofu.madeinrussia.core.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageRepository {
    String uploadImageToFolder(MultipartFile file, String folderName) throws IOException;

    String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException;

    void deleteMediaByLink(String link) throws Exception;

    void deleteAllMediaByLink(List<String> links) throws Exception;
}
