package com.surofu.exporteru.infrastructure.persistence.s3;

import com.surofu.exporteru.application.utils.MediaProcessor;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class S3Repository implements FileStorageRepository {

    private static final Set<String> IGNORABLE_IMAGE_FORMATS = Set.of("svg");
    private final S3Client client;
    private final MediaProcessor mediaProcessor;
    @Value("${s3.bucket}")
    private String bucketName;
    @Value("${s3.domain}")
    private String domain;
    @Value("${bunny.cdn.host}")
    private String cdnHost;

    @Override
    public String uploadImageToFolder(MultipartFile file, String folderName) throws Exception {
        return uploadImageToFolder(file, folderName, UploadOptions.builder().build());
    }

    @Override
    public String uploadImageToFolder(MultipartFile file, String folderName, UploadOptions options) throws Exception {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        RequestBody request = RequestBody.fromBytes(file.getBytes());
        String resultExtension = fileExtension;

        String key = createImageKey(folderName, resultExtension);

        if (fileExtension.contains("svg")) {
            resultExtension = "image/svg+xml";
        }

        client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(resultExtension)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build(), request);

        return generatePublicUrlWithS3Host(key);
    }

    @Override
    public List<String> uploadManyImagesToFolder(String folderName, MultipartFile... files) throws Exception {
        if (files.length == 0) {
            return new ArrayList<>();
        }

        List<String> links = new ArrayList<>();

        for (MultipartFile file : files) {
            var link = uploadImageToFolder(file, folderName);
            links.add(link);
        }

        return links;
    }

    @Override
    public String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException {
        String key = createVideoKey(folderName);
        RequestBody request = RequestBody.fromBytes(file.getBytes());

        client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("video/webm")
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build(), request);

        return generatePublicUrlWithS3Host(key);
    }

    @Override
    public List<String> uploadManyVideosToFolder(String folderName, MultipartFile... files) throws IOException {
        if (files.length == 0) {
            return new ArrayList<>();
        }

        List<String> links = new ArrayList<>();

        for (MultipartFile file : files) {
            var link = uploadVideoToFolder(file, folderName);
            links.add(link);
        }

        return links;
    }

    @Override
    public void deleteMediaByLink(String... links) {
        for (String link : links) {
            try {
                deleteMediaByLink(link);
            } catch (Exception e) {
                log.warn("Error deleting media by link '{}': {}", link, e.getMessage());
            }
        }
    }

    private void deleteMediaByLink(String link) {
        String objectKey = extractKeyFromUrl(link);

        var deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        client.deleteObject(deleteRequest);
    }

    private String createImageKey(String folderName, String extension) {
        if (extension.contains("svg")) {
            return createKey(folderName, "svg");
        }

        if (extension.contains("/")) {
            return createKey(folderName, extension.substring(extension.indexOf("/")));
        }

        return createKey(folderName, extension);
    }

    private String createVideoKey(String folderName) {
        return createKey(folderName, "webm");
    }

    private String createKey(String folderName, String extension) {
        String resultExtension = extension;

        if (extension.contains("/")) {
            resultExtension = extension.split("/")[1];
        }

        return folderName + "/" + UUID.randomUUID() + "." + resultExtension;
    }

    private String generatePublicUrlWithCdn(String key) {
        return generatePublicUrl(key, cdnHost);
    }

    private String generatePublicUrlWithS3Host(String key) {
        return generatePublicUrl(key, domain);
    }

    private String generatePublicUrl(String key, String host) {
        String url = host + "/" + key;
        return UriComponentsBuilder.fromUriString(url)
                .build()
                .encode()
                .toUriString();
    }

    private String extractKeyFromUrl(String url) {
        String[] parts = url.split(domain + "/");

        if (parts.length != 0) {
            return parts[parts.length - 1];
        }

        String[] partsWithCdn = url.split(cdnHost + "/");
        return partsWithCdn[partsWithCdn.length - 1];
    }

    private String getFileExtension(@Nullable String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
