package com.surofu.exporteru.application.service.product.create;

import com.surofu.exporteru.application.command.product.create.CreateProductMediaAltTextCommand;
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
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaProductCreationCreationLoader {
  private final ProductMediaRepository productMediaRepository;
  private final ProductRepository productRepository;
  private final FileStorageRepository storageRepository;
  private final TranslationRepository translationRepository;

  public void uploadMedia(Long productId, CreateProduct operation) {
    try {
      Product product = productRepository.getById(productId).orElseThrow();
      List<Map<String, String>> translatedAltTexts = translateTexts(operation);
      List<MultipartFile> productMedia = operation.getProductMedia();
      List<ProductMedia> mediaList = new ArrayList<>(productMedia.size());
      List<String> urls;

      try {
        urls = uploadFiles(operation);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return;
      }

      for (int i = 0; i < productMedia.size(); i++) {
        MultipartFile file = productMedia.get(i);
        ProductMedia media = new ProductMedia();
        media.setProduct(product);
        media.setPosition(ProductMediaPosition.of(i));
        media.setMediaType(getMediaType(file));
        media.setMimeType(ProductMediaMimeType.of(file.getContentType()));
        media.setUrl(ProductMediaUrl.of(urls.get(i)));
        mediaList.add(media);

        if (i < operation.getCreateProductMediaAltTextCommands().size() &&
            i < translatedAltTexts.size()) {
          CreateProductMediaAltTextCommand command =
              operation.getCreateProductMediaAltTextCommands().get(i);
          media.setAltText(new ProductMediaAltText(command.altText(), translatedAltTexts.get(i)));
        } else {
          media.setAltText(new ProductMediaAltText(file.getOriginalFilename(), new HashMap<>()));
        }
      }

      product.setPreviewImageUrl(ProductPreviewImageUrl.of(urls.iterator().next()));
      productRepository.save(product);
      productMediaRepository.saveAll(mediaList);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private List<String> uploadFiles(CreateProduct operation) throws Exception {
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
        storageRepository.uploadManyImagesToFolder(FileStorageFolders.PRODUCT_VIDEOS.getValue(),
            videos.toArray(MultipartFile[]::new));
    urls.addAll(imageUrls);
    urls.addAll(videoUrls);
    return urls;
  }

  private List<Map<String, String>> translateTexts(CreateProduct operation) {
    List<String> questionsToTranslate =
        operation.getCreateProductMediaAltTextCommands().stream()
            .map(CreateProductMediaAltTextCommand::altText)
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
