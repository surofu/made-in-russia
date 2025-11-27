package com.surofu.exporteru.application.service.product.update;

import com.surofu.exporteru.application.command.product.update.UpdateOldMediaDto;
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
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorMediaProductUpdatingLoader {
  private final VendorMediaRepository mediaRepository;
  private final FileStorageRepository storageRepository;
  private final VendorDetailsRepository vendorDetailsRepository;

  public void uploadMedia(Long productId, UpdateProduct operation) {
    try {
      List<String> urls = uploadFiles(operation);
      List<VendorMedia> resultMedia = new ArrayList<>(
          operation.getOldVendorDetailsMedia().size() + operation.getVendorMedia().size());
      VendorDetails vendorDetails =
          vendorDetailsRepository.getByProductId(productId).iterator().next();

      for (VendorMedia media : vendorDetails.getMedia()) {
        Optional<UpdateOldMediaDto> dtoOptional = operation.getOldVendorDetailsMedia().stream()
            .filter(d -> Objects.equals(d.id(), media.getId()))
            .findFirst();

        if (dtoOptional.isPresent()) {
          UpdateOldMediaDto dto = dtoOptional.get();
          media.setPosition(VendorMediaPosition.of(dto.position()));
          resultMedia.add(media);
        }
      }

      for (int i = 0; i < operation.getVendorMedia().size(); i++) {
        MultipartFile file = operation.getVendorMedia().get(i);
        VendorMedia media = new VendorMedia();
        media.setVendorDetails(vendorDetails);
        media.setPosition(VendorMediaPosition.of(getFreePosition(resultMedia)));
        media.setMediaType(getMediaType(file));
        media.setMimeType(VendorMediaMimeType.of(file.getContentType()));
        media.setUrl(VendorMediaUrl.of(urls.get(i)));
        resultMedia.add(media);
      }

      List<VendorMedia> mediaToDelete = vendorDetails.getMedia().stream()
          .filter(m -> !resultMedia.contains(m))
          .toList();
      List<String> mediaUrlsToDelete = mediaToDelete.stream()
          .map(VendorMedia::getUrl)
          .map(VendorMediaUrl::getValue)
          .toList();

      storageRepository.deleteMediaByLink(mediaUrlsToDelete.toArray(new String[0]));
      mediaRepository.saveAll(resultMedia);
      mediaRepository.deleteAll(mediaToDelete);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private int getFreePosition(List<VendorMedia> mediaList) {
    for (int i = 0; i < mediaList.size(); i++) {
      int finalI = i;
      if (mediaList.stream().noneMatch(m -> Objects.equals(m.getPosition().getValue(), finalI))) {
        return finalI;
      }
    }
    return mediaList.size();
  }

  private List<String> uploadFiles(UpdateProduct operation) throws Exception {
    List<String> urls = new ArrayList<>();
    List<MultipartFile> images = operation.getVendorMedia().stream()
        .filter(f -> f.getContentType() != null)
        .filter(f -> f.getContentType().startsWith("image/"))
        .toList();
    List<MultipartFile> videos = operation.getVendorMedia().stream()
        .filter(f -> f.getContentType() != null)
        .filter(f -> f.getContentType().startsWith("video/"))
        .toList();
    List<String> imageUrls =
        storageRepository.uploadManyImagesToFolder(FileStorageFolders.VENDOR_IMAGES.getValue(),
            images.toArray(MultipartFile[]::new));
    List<String> videoUrls =
        storageRepository.uploadManyVideosToFolder(FileStorageFolders.VENDOR_VIDEOS.getValue(),
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
