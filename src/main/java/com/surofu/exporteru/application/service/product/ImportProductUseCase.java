package com.surofu.exporteru.application.service.product;

import com.surofu.exporteru.application.command.importproduct.ImportProductCommand;
import com.surofu.exporteru.application.components.ImageDownloadService;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportProductUseCase {
  private static final String STUB_UNIT = "тонна";
  private static final String STUB_DELIVERY_DETAIL_NAME = "[ЗАГЛУШКА] Железнодорожная доставка";
  private static final String STUB_ALT_TEXT = "[ЗАГЛУШКА] Logo";

  private final TranslationRepository translationRepository;
  private final FileStorageRepository fileStorageRepository;
  private final ImageDownloadService imageDownloadService;
  private final ImportProductPersistenceService persistenceService;

  public void execute(ImportProductCommand command) {
    TranslationBatch batch = buildBatch(command);
    List<Map<String, String>> allTranslations = translationRepository.expand(batch.texts());
    List<String> uploadedUrls = uploadImages(command.images());

    persistenceService.persist(command, batch, allTranslations, uploadedUrls);
  }

  private TranslationBatch buildBatch(ImportProductCommand command) {
    List<String> texts = new ArrayList<>();

    int titleIdx = add(texts, command.title());
    int mainDescIdx = add(texts, command.mainDescription());
    int furtherDescIdx = add(texts, command.furtherDescription());
    int priceUnitIdx = add(texts, command.price() != null ? command.price().unit() : STUB_UNIT);
    int delivDetailIdx = add(texts, STUB_DELIVERY_DETAIL_NAME);
    int stubAltIdx = add(texts, STUB_ALT_TEXT);

    int charsBaseIdx = texts.size();
    if (command.characteristics() != null) {
      for (var ch : command.characteristics()) {
        texts.add(ch.name());
        texts.add(ch.value());
      }
    }

    return new TranslationBatch(texts, titleIdx, mainDescIdx, furtherDescIdx, priceUnitIdx,
        delivDetailIdx, stubAltIdx, charsBaseIdx);
  }

  private int add(List<String> list, String value) {
    int idx = list.size();
    list.add(value);
    return idx;
  }

  private List<String> uploadImages(List<String> imageUrls) {
    if (imageUrls == null || imageUrls.isEmpty()) {
      return List.of();
    }
    List<String> result = new ArrayList<>();
    for (String url : imageUrls) {
      imageDownloadService.download(url).ifPresentOrElse(
          file -> {
            try {
              result.add(fileStorageRepository.uploadImageToFolder(
                  file, FileStorageFolders.PRODUCT_IMAGES.getValue()));
            } catch (Exception e) {
              log.warn("Failed to upload image to S3 from url {}: {}", url, e.getMessage());
            }
          },
          () -> log.warn("Skipping image, download failed: {}", url)
      );
    }
    return result;
  }

  public record TranslationBatch(
      List<String> texts,
      int titleIdx,
      int mainDescIdx,
      int furtherDescIdx,
      int priceUnitIdx,
      int delivDetailNameIdx,
      int stubAltTextIdx,
      int charsBaseIdx
  ) {
    public int charNameIdx(int i) {
      return charsBaseIdx + i * 2;
    }

    public int charValueIdx(int i) {
      return charsBaseIdx + i * 2 + 1;
    }
  }
}