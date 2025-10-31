package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.infrastructure.persistence.s3.UploadOptions;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageRepository {
    String uploadImageToFolder(MultipartFile file, String folderName) throws Exception;

    String uploadImageToFolder(MultipartFile file, String folderName, UploadOptions options) throws Exception;

    List<String> uploadManyImagesToFolder(String folderName, MultipartFile ...files) throws Exception;

    String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException, InterruptedException;

    List<String> uploadManyVideosToFolder(String folderName, MultipartFile ...files) throws IOException, InterruptedException;

    void deleteMediaByLink(String ...links) throws Exception;
}
