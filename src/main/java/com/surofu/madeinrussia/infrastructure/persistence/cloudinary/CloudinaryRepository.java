package com.surofu.madeinrussia.infrastructure.persistence.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.surofu.madeinrussia.application.utils.FileCompressor;
import com.surofu.madeinrussia.core.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class CloudinaryRepository implements FileStorageRepository {

    private final Cloudinary cloudinary;
    private final FileCompressor fileCompressor;

    private static final long MAX_FILE_SIZE = 500_000_000L; // 500MB
    private static final String VIDEO_FOLDER = "productsVideos";
    private static final int TARGET_WIDTH = 1000;
    private static final int TARGET_HEIGHT = 1000;

    @Override
    public String upload(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        if (Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            bytes = fileCompressor.compressImageAndConvertToWebP(file);
        }

        if (Objects.requireNonNull(file.getContentType()).startsWith("video")) {
           return uploadVideo(file, file.getBytes());
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                "folder", "productsImages"
        ));

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public String uploadWithoutCompress(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return uploadBytes(bytes);
    }

    private String uploadVideo(MultipartFile file, byte[] bytes) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("Invalid file type. Expected video file.");
        }

        validateFileSize(bytes.length);

        try {
            Map<String, Object> uploadParams = buildUploadParams();
            Map<?, ?> uploadResult = cloudinary.uploader().uploadLarge(bytes, uploadParams);

            logUploadResult(uploadResult); // Debug logging
            return extractSecureUrl(uploadResult);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video file to Cloudinary", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during video upload: " + e.getMessage(), e);
        }
    }

    private void validateFileSize(long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("File size (%d bytes) exceeds maximum limit (%d bytes)",
                            fileSize, MAX_FILE_SIZE));
        }
    }

    private Map<String, Object> buildUploadParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "video");
        params.put("folder", VIDEO_FOLDER);

        // Remove async parameters that might be causing issues
        // params.put("eager_async", true);
        // params.put("async", true);

        // Add timeout and other reliability parameters
        params.put("timeout", 120); // 2 minutes timeout
        params.put("chunk_size", 20_000_000); // 20MB chunks for large files

        // Only add transformations for smaller files to avoid timeout
        // params.put("eager", createTransformations());

        return params;
    }

    private List<Transformation> createTransformations() {
        return Arrays.asList(
                createMp4Transformation(),
                createWebmTransformation()
        );
    }

    private Transformation createMp4Transformation() {
        return new Transformation()
                .width(TARGET_WIDTH)
                .height(TARGET_HEIGHT)
                .crop("fill")
                .videoCodec("h264")
                .audioCodec("aac")
                .quality("auto:good")
                .fetchFormat("mp4");
    }

    private Transformation createWebmTransformation() {
        return new Transformation()
                .width(TARGET_WIDTH)
                .height(TARGET_HEIGHT)
                .crop("fill")
                .videoCodec("vp9")
                .audioCodec("opus")
                .quality("auto:good")
                .fetchFormat("webm");
    }

    private void logUploadResult(Map<?, ?> uploadResult) {
        // Log the full response for debugging
        System.out.println("Cloudinary upload result: " + uploadResult);

        // Log specific fields that might be present
        Object publicId = uploadResult.get("public_id");
        Object url = uploadResult.get("url");
        Object secureUrl = uploadResult.get("secure_url");
        Object status = uploadResult.get("status");
        Object error = uploadResult.get("error");

        System.out.println("Public ID: " + publicId);
        System.out.println("URL: " + url);
        System.out.println("Secure URL: " + secureUrl);
        System.out.println("Status: " + status);
        System.out.println("Error: " + error);
    }

    private String extractSecureUrl(Map<?, ?> uploadResult) {
        // Check for errors first
        Object error = uploadResult.get("error");
        if (error != null) {
            throw new RuntimeException("Cloudinary upload error: " + error);
        }

        // Try secure_url first
        String secureUrl = (String) uploadResult.get("secure_url");
        if (secureUrl != null && !secureUrl.trim().isEmpty()) {
            return secureUrl;
        }

        // Fallback to regular url if secure_url is not available
        String regularUrl = (String) uploadResult.get("url");
        if (regularUrl != null && !regularUrl.trim().isEmpty()) {
            // Convert http to https if needed
            return regularUrl.replace("http://", "https://");
        }

        // Check if this is an async upload
        Object status = uploadResult.get("status");
        if ("pending".equals(status)) {
            Object publicId = uploadResult.get("public_id");
            if (publicId != null) {
                // For async uploads, construct the URL manually
                return constructCloudinaryUrl(publicId.toString());
            }
        }

        throw new RuntimeException("Upload failed: No URL returned from Cloudinary. Response: " + uploadResult);
    }

    private String constructCloudinaryUrl(String publicId) {
        // Construct Cloudinary URL manually for async uploads
        // Format: https://res.cloudinary.com/{cloud_name}/video/upload/{public_id}
        String cloudName = getCloudName(); // You'll need to implement this
        return String.format("https://res.cloudinary.com/%s/video/upload/%s", cloudName, publicId);
    }

    private String getCloudName() {
        // Extract cloud name from Cloudinary configuration
        // This assumes you have access to the cloudinary configuration
        try {
            return cloudinary.config.cloudName;
        } catch (Exception e) {
            throw new RuntimeException("Unable to get cloud name from Cloudinary configuration", e);
        }
    }

    private String uploadBytes(byte[] data) throws IOException {
        Map<?, ?> uploadOptions = ObjectUtils.emptyMap();
        Map<?, ?> uploadResult = cloudinary.uploader().upload(data, uploadOptions);
        return uploadResult.get("secure_url").toString();
    }
}
