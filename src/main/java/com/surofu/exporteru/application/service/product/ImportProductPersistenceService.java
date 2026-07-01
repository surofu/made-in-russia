package com.surofu.exporteru.application.service.product;

import com.surofu.exporteru.application.command.importproduct.ImportProductCommand;
import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductDescription;
import com.surofu.exporteru.core.model.product.ProductPreviewImageUrl;
import com.surofu.exporteru.core.model.product.ProductTitle;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicName;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristicValue;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsName;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetailsValue;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.core.model.product.media.ProductMediaAltText;
import com.surofu.exporteru.core.model.product.media.ProductMediaMimeType;
import com.surofu.exporteru.core.model.product.media.ProductMediaPosition;
import com.surofu.exporteru.core.model.product.media.ProductMediaUrl;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceCurrency;
import com.surofu.exporteru.core.model.product.price.ProductPriceDiscount;
import com.surofu.exporteru.core.model.product.price.ProductPriceOriginalPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceQuantityRange;
import com.surofu.exporteru.core.model.product.price.ProductPriceUnit;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.DeliveryMethodRepository;
import com.surofu.exporteru.core.repository.DeliveryTermRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportProductPersistenceService {

  private static final String STUB_QUANTITY = "7777";
  private static final BigDecimal STUB_PRICE = new BigDecimal("777777777777");
  private static final String STUB_CURRENCY = "NO_CURRENCY";
  private static final String STUB_UNIT = "тонна";
  private static final String STUB_DELIVERY_DETAIL_NAME = "[ЗАГЛУШКА] Железнодорожная доставка";
  private static final String STUB_DELIVERY_DETAIL_VALUE = "100";
  private static final String STUB_LOGO_URL =
      "https://6e9e2c8a-521e-4620-9cf1-ef51353051d3.srvstatic.uz/products/placeholder.png";
  private static final String STUB_ALT_TEXT = "[ЗАГЛУШКА] Logo";

  private static final long HARDCODED_USER_ID = 201L;
  private static final long DEFAULT_CATEGORY_ID = 1L;
  private static final long DEFAULT_DELIVERY_METHOD_ID = 1L;
  private static final long DEFAULT_DELIVERY_TERM_ID = 3L;

  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final DeliveryMethodRepository deliveryMethodRepository;
  private final DeliveryTermRepository deliveryTermRepository;

  @Transactional
  public void persist(
      ImportProductCommand command,
      ImportProductUseCase.TranslationBatch batch,
      List<Map<String, String>> allTranslations,
      List<String> uploadedUrls
  ) {
    User user = userRepository.getById(HARDCODED_USER_ID)
        .orElseThrow(
            () -> new IllegalStateException("Import user not found: " + HARDCODED_USER_ID));
    Category category;

    if (command.categoryId() != null) {
      category = categoryRepository.getById(command.categoryId())
          .orElseThrow(() -> new IllegalStateException(
              "Import category not found: " + command.categoryId()));
    } else {
      category = categoryRepository.getById(DEFAULT_CATEGORY_ID)
          .orElseThrow(() -> new IllegalStateException("Default category not found"));
    }

    DeliveryMethod deliveryMethod = deliveryMethodRepository.getById(DEFAULT_DELIVERY_METHOD_ID)
        .orElseThrow(() -> new IllegalStateException("Default delivery method not found"));
    DeliveryTerm deliveryTerm = deliveryTermRepository.findById(DEFAULT_DELIVERY_TERM_ID)
        .orElseThrow(() -> new IllegalStateException("Default delivery term not found"));

    Map<String, String> titleTrans = allTranslations.get(batch.titleIdx());
    Map<String, String> mainDescTrans = allTranslations.get(batch.mainDescIdx());
    Map<String, String> furtherDescTrans = allTranslations.get(batch.furtherDescIdx());
    Map<String, String> priceUnitTrans = allTranslations.get(batch.priceUnitIdx());
    Map<String, String> delivDetailTrans = allTranslations.get(batch.delivDetailNameIdx());

    String previewUrl = uploadedUrls.stream()
        .filter(url -> !url.contains(".mp4"))
        .findFirst()
        .orElse(STUB_LOGO_URL);

    Product product = new Product();
    product.setUser(user);
    product.setCategory(category);
    product.setTitle(new ProductTitle(command.title(), titleTrans));
    product.setDescription(new ProductDescription(
        command.mainDescription(), command.furtherDescription(), mainDescTrans, furtherDescTrans));
    product.setPreviewImageUrl(new ProductPreviewImageUrl(previewUrl));
    product.getDeliveryMethods().add(deliveryMethod);
    product.getDeliveryTerms().add(deliveryTerm);
    product.getPrices().add(buildPrice(product, command.price(), priceUnitTrans));
    product.getDeliveryMethodDetails().add(buildStubDeliveryDetail(product, delivDetailTrans));

    if (uploadedUrls.isEmpty()) {
      product.getMedia().add(buildStubMedia(product, allTranslations.get(batch.stubAltTextIdx())));
    } else {
      for (int i = 0; i < uploadedUrls.size(); i++) {
        product.getMedia()
            .add(buildMedia(product, uploadedUrls.get(i), command.title(), titleTrans, i));
      }
    }

    if (command.characteristics() != null) {
      List<ImportProductCommand.Characteristic> chars = command.characteristics();
      for (int i = 0; i < chars.size(); i++) {
        product.getCharacteristics().add(buildCharacteristic(
            product, chars.get(i),
            allTranslations.get(batch.charNameIdx(i)),
            allTranslations.get(batch.charValueIdx(i))
        ));
      }
    }

    productRepository.save(product);
  }

  private ProductPrice buildPrice(Product product, ImportProductCommand.Price price,
                                  Map<String, String> unitTrans) {
    var productPrice = new ProductPrice();
    productPrice.setProduct(product);
    productPrice.setQuantityRange(ProductPriceQuantityRange.of(STUB_QUANTITY, STUB_QUANTITY));
    productPrice.setDiscount(new ProductPriceDiscount(BigDecimal.ZERO));

    if (price != null) {
      productPrice.setCurrency(new ProductPriceCurrency(price.currency().name()));
      productPrice.setOriginalPrice(new ProductPriceOriginalPrice(price.value()));
      productPrice.setUnit(new ProductPriceUnit(price.unit(), unitTrans));
    } else {
      productPrice.setCurrency(new ProductPriceCurrency(STUB_CURRENCY));
      productPrice.setOriginalPrice(new ProductPriceOriginalPrice(STUB_PRICE));
      productPrice.setUnit(new ProductPriceUnit(STUB_UNIT, unitTrans));
    }
    return productPrice;
  }

  private ProductDeliveryMethodDetails buildStubDeliveryDetail(Product product,
                                                               Map<String, String> nameTrans) {
    Map<String, String> valueTrans = Map.of(
        "ru", STUB_DELIVERY_DETAIL_VALUE,
        "en", STUB_DELIVERY_DETAIL_VALUE,
        "hi", STUB_DELIVERY_DETAIL_VALUE,
        "zh", STUB_DELIVERY_DETAIL_VALUE
    );
    var detail = new ProductDeliveryMethodDetails();
    detail.setProduct(product);
    detail.setName(new ProductDeliveryMethodDetailsName(STUB_DELIVERY_DETAIL_NAME, nameTrans));
    detail.setValue(new ProductDeliveryMethodDetailsValue(STUB_DELIVERY_DETAIL_VALUE, valueTrans));
    return detail;
  }

  private ProductMedia buildStubMedia(Product product, Map<String, String> altTrans) {
    var media = new ProductMedia();
    media.setProduct(product);
    media.setMediaType(MediaType.IMAGE);
    media.setMimeType(new ProductMediaMimeType("image/svg+xml"));
    media.setUrl(new ProductMediaUrl(STUB_LOGO_URL));
    media.setAltText(new ProductMediaAltText(STUB_ALT_TEXT, altTrans));
    media.setPosition(new ProductMediaPosition(0));
    return media;
  }

  private ProductMedia buildMedia(Product product, String url, String altText,
                                  Map<String, String> altTrans, int position) {
    var media = new ProductMedia();
    media.setProduct(product);
    media.setMediaType(MediaType.IMAGE);
    media.setMimeType(new ProductMediaMimeType("image/jpeg"));
    media.setUrl(new ProductMediaUrl(url));
    media.setAltText(new ProductMediaAltText(altText, altTrans));
    media.setPosition(new ProductMediaPosition(position));
    return media;
  }

  private ProductCharacteristic buildCharacteristic(Product product,
                                                    ImportProductCommand.Characteristic ch,
                                                    Map<String, String> nameTrans,
                                                    Map<String, String> valueTrans) {
    var characteristic = new ProductCharacteristic();
    characteristic.setProduct(product);
    characteristic.setName(new ProductCharacteristicName(ch.name(), nameTrans));
    characteristic.setValue(new ProductCharacteristicValue(ch.value(), valueTrans));
    return characteristic;
  }
}