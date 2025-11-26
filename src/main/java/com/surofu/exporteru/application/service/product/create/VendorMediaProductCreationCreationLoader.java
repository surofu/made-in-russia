package com.surofu.exporteru.application.service.product.create;

import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaMimeType;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaPosition;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaUrl;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.VendorDetailsRepository;
import com.surofu.exporteru.core.repository.VendorMediaRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorMediaProductCreationCreationLoader {
  private final VendorMediaRepository repository;
  private final FileStorageRepository storageRepository;
  private final VendorDetailsRepository vendorDetailsRepository;

  public void uploadMedia(Long productId, CreateProduct operation) {
    try {
      VendorDetails vendorDetails = vendorDetailsRepository.getByProductId(productId).iterator().next();
      List<MultipartFile> productMedia = operation.getProductVendorDetailsMedia();
      List<VendorMedia> mediaList = new ArrayList<>(productMedia.size());
      List<String> urls;

      try {
        urls = uploadFiles(operation);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return;
      }

      for (int i = 0; i < productMedia.size(); i++) {
        MultipartFile file = productMedia.get(i);
        VendorMedia media = new VendorMedia();
        media.setVendorDetails(vendorDetails);
        media.setPosition(VendorMediaPosition.of(i));
        media.setMediaType(getMediaType(file));
        media.setMimeType(VendorMediaMimeType.of(file.getContentType()));
        media.setUrl(VendorMediaUrl.of(urls.get(i)));
        mediaList.add(media);
      }

      repository.saveAll(mediaList);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private List<String> uploadFiles(CreateProduct operation) throws Exception {
    List<String> urls = new ArrayList<>();
    List<MultipartFile> images = operation.getProductVendorDetailsMedia().stream()
        .filter(f -> f.getContentType() != null)
        .filter(f -> f.getContentType().startsWith("image/"))
        .toList();
    List<MultipartFile> videos = operation.getProductVendorDetailsMedia().stream()
        .filter(f -> f.getContentType() != null)
        .filter(f -> f.getContentType().startsWith("video/"))
        .toList();
    List<String> imageUrls =
        storageRepository.uploadManyImagesToFolder(FileStorageFolders.VENDOR_IMAGES.getValue(),
            images.toArray(MultipartFile[]::new));
    List<String> videoUrls =
        storageRepository.uploadManyImagesToFolder(FileStorageFolders.VENDOR_VIDEOS.getValue(),
            videos.toArray(MultipartFile[]::new));
    urls.addAll(imageUrls);
    urls.addAll(videoUrls);
    return urls;
  }

  private MediaType getMediaType(MultipartFile file) {
    if (Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
      return MediaType.IMAGE;
    }

    if (Objects.requireNonNull(file.getContentType()).startsWith("video/")) {
      return MediaType.VIDEO;
    }

    throw new IllegalArgumentException("Unsupported content type: " + file.getContentType());
  }
}
