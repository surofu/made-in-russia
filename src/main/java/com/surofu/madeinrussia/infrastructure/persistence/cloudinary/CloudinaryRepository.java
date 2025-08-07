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
import java.util.*;

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

    @Override
    public String uploadImageToFolder(MultipartFile file, String folderName) throws IOException {
        Map<Object, Object> options = new HashMap<>();
        options.put("resource_type", "image");
        options.put("eager", Collections.singletonList(getImageTransformation()));
        Map<?, ?> resultMap = uploadFileToFolder(file, folderName, options);
        List<Map> eagerList = (List<Map>) resultMap.get("eager");
        return eagerList.get(0).get("secure_url").toString();
    }

    @Override
    public String uploadVideoToFolder(MultipartFile file, String folderName) throws IOException {
        Map<Object, Object> options = new HashMap<>();
        options.put("resource_type", "video");
        options.put("eager", Collections.singletonList(getVideoTransformation()));
        Map<?, ?> resultMap = uploadFileToFolder(file, folderName, options);
        List<Map> eagerList = (List<Map>) resultMap.get("eager");
        return eagerList.get(0).get("secure_url").toString();
    }

    @Override
    public void deleteMediaByLink(String ...links) throws Exception {
        Set<String> publicIdImageSet = new HashSet<>(links.length);
        Set<String> publicIdVideoSet = new HashSet<>(links.length);

        for (String link : links) {
            String[] split = link.split("/");

            if (split.length < 2) {
                return;
            }

            String folderName = split[split.length - 2];
            String mediaLink = split[split.length - 1];
            String mediaId = mediaLink.split(".web")[0];
            String publicId = folderName + "/" + mediaId;

            if (mediaLink.contains("webp")) {
                publicIdImageSet.add(publicId);
            } else {
                publicIdVideoSet.add(publicId);
            }
        }

        if (!publicIdImageSet.isEmpty()) {
            cloudinary.api().deleteResources(publicIdImageSet, Map.of("resource_type", "image"));
        }

        if (!publicIdVideoSet.isEmpty()) {
            cloudinary.api().deleteResources(publicIdVideoSet, Map.of("resource_type", "video"));
        }
    }

    private Map<?, ?> uploadFileToFolder(MultipartFile file, String folderName, Map<?, ?> options) throws IOException {
        Map<Object, Object> optionsMap = new HashMap<>();
        optionsMap.put("folder", folderName);
        optionsMap.put("eager_async", true);
        optionsMap.put("quality", "auto:good");
        optionsMap.putAll(options);
        return cloudinary.uploader().upload(file.getBytes(), optionsMap);
    }

    private EagerTransformation getImageTransformation() {
        return new EagerTransformation()
                .width(imageMaxWidth)
                .height(imageMaxHeight)
                .quality(imageQuality)
                .gravity("north")
                .crop("fill")
                .fetchFormat("webp");
    }

    private EagerTransformation getVideoTransformation() {
        return new EagerTransformation()
                .width(videoMaxWidth)
                .height(videoMaxHeight)
                .quality(videoQuality)
                .gravity("north")
                .crop("fill")
                .fetchFormat("webm");
    }
}
