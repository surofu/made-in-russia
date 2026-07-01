package com.surofu.exporteru.infrastructure.persistence.s3;

import com.surofu.exporteru.application.utils.MediaProcessor;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
  public String uploadImageToFolder(MultipartFile file, String folderName, UploadOptions options)
      throws Exception {
    String originalFilename = file.getOriginalFilename();
    String fileExtension = getFileExtension(originalFilename);

    String contentType = URLConnection.guessContentTypeFromName(originalFilename);
    if (contentType == null) {
      contentType =
          fileExtension.equalsIgnoreCase("svg") ? "image/svg+xml" : "application/octet-stream";
    }

    String key = createKey(folderName, fileExtension.isEmpty() ? "jpg" : fileExtension);
    RequestBody request = RequestBody.fromBytes(file.getBytes());

    client.putObject(PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType(contentType)
        .acl(ObjectCannedACL.PUBLIC_READ)
        .build(), request);

    return generatePublicUrlWithS3Host(key);
  }

  @Override
  public List<String> uploadManyImagesToFolder(String folderName, MultipartFile... files)
      throws Exception {
    if (files.length == 0) {
      return new ArrayList<>();
    }
    List<String> links = new ArrayList<>();
    for (MultipartFile file : files) {
      links.add(uploadImageToFolder(file, folderName));
    }
    return links;
  }

  @Override
  public String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException {
    String key = createKey(folderName, "webm");
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
  public List<String> uploadManyVideosToFolder(String folderName, MultipartFile... files)
      throws IOException {
    if (files.length == 0) {
      return new ArrayList<>();
    }
    List<String> links = new ArrayList<>();
    for (MultipartFile file : files) {
      links.add(uploadVideoToFolder(file, folderName));
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
    client.deleteObject(DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .build());
  }

  private String createKey(String folderName, String extension) {
    return folderName + "/" + UUID.randomUUID() + "." + extension.toLowerCase();
  }

  private String generatePublicUrlWithCdn(String key) {
    return generatePublicUrl(key, cdnHost);
  }

  private String generatePublicUrlWithS3Host(String key) {
    return generatePublicUrl(key, domain);
  }

  private String generatePublicUrl(String key, String host) {
    return host + "/" + bucketName + "/" + key;
  }

  private String extractKeyFromUrl(String url) {
    String path = URI.create(url).getPath();
    return path.substring(path.indexOf("/" + bucketName + "/") + bucketName.length() + 2);
  }

  private String getFileExtension(@Nullable String filename) {
    if (filename == null || filename.lastIndexOf(".") == -1) {
      return "";
    }
    return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
  }
}