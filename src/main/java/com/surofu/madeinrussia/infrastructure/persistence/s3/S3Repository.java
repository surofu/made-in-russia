package com.surofu.madeinrussia.infrastructure.persistence.s3;

import com.surofu.madeinrussia.application.utils.MediaProcessor;
import com.surofu.madeinrussia.core.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class S3Repository implements FileStorageRepository {

    private final S3Client client;

    private final MediaProcessor mediaProcessor;
    private final String imageTargetExtension = "jpg";
    private final String videoTargetExtension = "mp4";
    @Value("${s3.endpoint}")
    private String endpoint;
    @Value("${s3.bucket}")
    private String bucketName;
    @Value("${s3.domain}")
    private String domain;

    @Override
    public String uploadImageToFolder(MultipartFile file, String folderName) throws IOException {
        String key = createKey(folderName, imageTargetExtension);
        byte[] compressedImage = mediaProcessor.compressImage(file, imageTargetExtension);
        InputStream inputStream = new ByteArrayInputStream(compressedImage);
        RequestBody request = RequestBody.fromInputStream(inputStream, compressedImage.length);

        client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/" + imageTargetExtension)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build(), request);

        return generatePublicUrl(key);
    }

    @Override
    public List<String> uploadManyImagesToFolder(String folderName, MultipartFile... files) throws IOException {
        List<String> links = new ArrayList<>();

        for (MultipartFile file : files) {
            var link = uploadImageToFolder(file, folderName);
            links.add(link);
        }

        return links;
    }

    @Override
    public String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException, InterruptedException {
        String key = createKey(folderName, videoTargetExtension);
        byte[] resultData = mediaProcessor.compressVideo(file, videoTargetExtension);
        InputStream inputStream = new ByteArrayInputStream(resultData);
        RequestBody request = RequestBody.fromInputStream(inputStream, resultData.length);

        client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/" + videoTargetExtension)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build(), request);

        return generatePublicUrl(key);
    }

    @Override
    public List<String> uploadManyVideosToFolder(String folderName, MultipartFile... files) throws IOException, InterruptedException {
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
            deleteMediaByLink(link);
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

    private String createKey(String folderName, String extension) {
        return folderName + "/" + UUID.randomUUID() + "." + extension;
    }

    private String generatePublicUrl(String key) {
        String url = domain + "/" + key;
        return UriComponentsBuilder.fromUriString(url)
                .build()
                .encode()
                .toUriString();
    }

    private String extractKeyFromUrl(String url) {
        String[] parts = url.split(domain + "/");
        return parts[parts.length - 1];
    }
}
