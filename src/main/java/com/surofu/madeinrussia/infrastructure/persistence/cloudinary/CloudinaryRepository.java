package com.surofu.madeinrussia.infrastructure.persistence.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.EagerTransformation;
import com.surofu.madeinrussia.core.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CloudinaryRepository implements FileStorageRepository {

    private final Cloudinary cloudinary;

    @Value("${app.compress.image.max-width}")
    private int imageMaxWidth;

    @Value("${app.compress.image.max-height}")
    private int imageMaxHeight;

    @Value("${app.compress.image.quality}")
    private int imageQuality;

    @Value("${app.compress.video.max-width}")
    private int videoMaxWidth;

    @Value("${app.compress.video.max-height}")
    private int videoMaxHeight;

    @Value("${app.compress.video.quality}")
    private int videoQuality;

    private EagerTransformation imageTransformation;

    private EagerTransformation videoTransformation;

    @Override
    public String uploadImageToFolder(MultipartFile file, String folderName) throws IOException {
        Map<Object, Object> options = new HashMap<>();
        options.put("resource_type", "image");
        options.put("eager", List.of(getImageTransformation()));
        Map<?, ?> resultMap = uploadFileToFolder(file, folderName, options);
        return resultMap.get("secure_url").toString();
    }

    @Override
    public String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException {
        Map<Object, Object> options = new HashMap<>();
        options.put("resource_type", "video");
        options.put("eager", List.of(getVideoTransformation()));
        Map<?, ?> resultMap = uploadFileToFolder(file, folderName, options);
        return resultMap.get("secure_url").toString();
    }

    private Map<?, ?> uploadFileToFolder(MultipartFile file, String folderName, Map<?, ?> options) throws IOException {
        Map<Object, Object> optionsMap = new HashMap<>();

        EagerTransformation transformation = new EagerTransformation()
                .width(imageMaxWidth)
                .height(imageMaxHeight)
                .quality(imageQuality)
                .gravity("north_east")
                .crop("fill");

        optionsMap.put("folder", folderName);
        optionsMap.put("eager", List.of(transformation));
        optionsMap.put("eager_async", true);
        optionsMap.putAll(options);

        return cloudinary.uploader().upload(file.getBytes(), optionsMap);
    }

    private EagerTransformation getImageTransformation() {
        if (imageTransformation != null) {
            return imageTransformation;
        }

        imageTransformation = new EagerTransformation()
                .width(imageMaxWidth)
                .height(imageMaxHeight)
                .quality(imageQuality)
                .gravity("north_east")
                .crop("fill");

        return imageTransformation;
    }

    private EagerTransformation getVideoTransformation() {
        if (videoTransformation != null) {
            return videoTransformation;
        }

        videoTransformation = new EagerTransformation()
                .width(videoMaxWidth)
                .height(videoMaxHeight)
                .quality(videoQuality)
                .gravity("north_east")
                .crop("fill");

        return videoTransformation;
    }
}
