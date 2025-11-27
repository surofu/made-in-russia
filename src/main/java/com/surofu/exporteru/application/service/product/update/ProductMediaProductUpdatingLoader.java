package com.surofu.exporteru.application.service.product.update;

import com.surofu.exporteru.application.command.product.update.UpdateOldMediaDto;
import com.surofu.exporteru.application.command.product.update.UpdateProductMediaAltTextCommand;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductPreviewImageUrl;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.core.model.product.media.ProductMediaAltText;
import com.surofu.exporteru.core.model.product.media.ProductMediaMimeType;
import com.surofu.exporteru.core.model.product.media.ProductMediaPosition;
import com.surofu.exporteru.core.model.product.media.ProductMediaUrl;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.ProductMediaRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductMediaProductUpdatingLoader {
  private final ProductMediaRepository mediaRepository;
  private final ProductRepository productRepository;
  private final FileStorageRepository storageRepository;
  private final TranslationRepository translationRepository;

  public void uploadMedia(Long productId, UpdateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<Map<String, String>> translatedAltTexts = translateTexts(operation);
      List<String> urls = uploadFiles(operation);
      List<ProductMedia> resultMedia = new ArrayList<>(
          operation.getOldProductMedia().size() + operation.getProductMedia().size());

      for (ProductMedia media : product.getMedia()) {
        Optional<UpdateOldMediaDto> dtoOptional = operation.getOldProductMedia().stream()
            .filter(d -> Objects.equals(d.id(), media.getId()))
            .findFirst();

        if (dtoOptional.isPresent()) {
          UpdateOldMediaDto dto = dtoOptional.get();
          media.setPosition(ProductMediaPosition.of(dto.position()));
          resultMedia.add(media);
        }
      }

      for (int i = 0; i < operation.getProductMedia().size(); i++) {
        MultipartFile file = operation.getProductMedia().get(i);
        ProductMedia media = new ProductMedia();
        media.setProduct(product);
        media.setPosition(ProductMediaPosition.of(getFreePosition(resultMedia)));
        media.setMediaType(getMediaType(file));
        media.setMimeType(ProductMediaMimeType.of(file.getContentType()));
        media.setUrl(ProductMediaUrl.of(urls.get(i)));
        if (i < translatedAltTexts.size() && StringUtils.trimToNull(
            operation.getUpdateProductMediaAltTextCommands().get(i).altText()) != null) {
          media.setAltText(new ProductMediaAltText(
              operation.getUpdateProductMediaAltTextCommands().get(i).altText(),
              translatedAltTexts.get(i)
          ));
        } else {
          media.setAltText(new ProductMediaAltText(
              file.getOriginalFilename(),
              new HashMap<>()
          ));
        }
        resultMedia.add(media);
      }

      List<ProductMedia> mediaToDelete = product.getMedia().stream()
          .filter(m -> !resultMedia.contains(m))
          .toList();
      List<String> mediaUrlsToDelete = mediaToDelete.stream()
          .map(ProductMedia::getUrl)
          .map(ProductMediaUrl::getValue)
          .toList();
      product.setPreviewImageUrl(ProductPreviewImageUrl.of(resultMedia.stream()
          .sorted(Comparator.comparingInt(a -> a.getPosition().getValue()))
          .toList().get(0).getUrl().getValue()));
      storageRepository.deleteMediaByLink(mediaUrlsToDelete.toArray(new String[0]));
      mediaRepository.saveAll(resultMedia);
      mediaRepository.deleteAll(mediaToDelete);
      productRepository.save(product);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private int getFreePosition(List<ProductMedia> mediaList) {
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
    List<MultipartFile> images = operation.getProductMedia().stream()
        .filter(f -> f.getContentType() != null)
        .filter(f -> f.getContentType().startsWith("image/"))
        .toList();
    List<MultipartFile> videos = operation.getProductMedia().stream()
        .filter(f -> f.getContentType() != null)
        .filter(f -> f.getContentType().startsWith("video/"))
        .toList();
    List<String> imageUrls =
        storageRepository.uploadManyImagesToFolder(FileStorageFolders.PRODUCT_IMAGES.getValue(),
            images.toArray(MultipartFile[]::new));
    List<String> videoUrls =
        storageRepository.uploadManyVideosToFolder(FileStorageFolders.PRODUCT_VIDEOS.getValue(),
            videos.toArray(MultipartFile[]::new));
    urls.addAll(imageUrls);
    urls.addAll(videoUrls);
    return urls;
  }

  private List<Map<String, String>> translateTexts(UpdateProduct operation) {
    List<String> questionsToTranslate =
        operation.getUpdateProductMediaAltTextCommands().stream()
            .map(UpdateProductMediaAltTextCommand::altText)
            .filter(t -> !t.isBlank())
            .toList();
    return translationRepository.expand(questionsToTranslate);
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
