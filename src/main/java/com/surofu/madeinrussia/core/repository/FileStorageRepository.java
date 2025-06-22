package com.surofu.madeinrussia.core.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageRepository {
    String upload(MultipartFile file) throws IOException;

    String uploadWithoutCompress(MultipartFile file) throws IOException;
}
