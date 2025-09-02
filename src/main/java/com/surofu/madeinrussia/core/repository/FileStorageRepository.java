package com.surofu.madeinrussia.core.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageRepository {
    String uploadImageToFolder(MultipartFile file, String folderName) throws IOException;

    List<String> uploadManyImagesToFolder(String folderName, MultipartFile ...files) throws IOException;

    String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException;

    List<String> uploadManyVideosToFolder(String folderName, MultipartFile ...files) throws IOException;

    void deleteMediaByLink(String ...links) throws Exception;
}
