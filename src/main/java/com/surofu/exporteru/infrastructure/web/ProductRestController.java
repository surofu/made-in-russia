package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.order.CreateOrderCommand;
import com.surofu.exporteru.application.command.product.create.CreateProductCommand;
import com.surofu.exporteru.application.command.product.review.CreateProductReviewCommand;
import com.surofu.exporteru.application.command.product.review.UpdateProductReviewCommand;
import com.surofu.exporteru.application.command.product.update.UpdateProductCommand;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.product.ProductArticleCode;
import com.surofu.exporteru.core.model.product.ProductDescription;
import com.surofu.exporteru.core.model.product.ProductDiscountExpirationDate;
import com.surofu.exporteru.core.model.product.ProductMinimumOrderQuantity;
import com.surofu.exporteru.core.model.product.ProductTitle;
import com.surofu.exporteru.core.model.product.review.ProductReviewContent;
import com.surofu.exporteru.core.model.product.review.ProductReviewRating;
import com.surofu.exporteru.core.service.order.OrderService;
import com.surofu.exporteru.core.service.order.operation.CreateOrder;
import com.surofu.exporteru.core.service.product.ProductService;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import com.surofu.exporteru.core.service.product.operation.DeleteProductById;
import com.surofu.exporteru.core.service.product.operation.GetProductByArticle;
import com.surofu.exporteru.core.service.product.operation.GetProductById;
import com.surofu.exporteru.core.service.product.operation.GetProductCategoryByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductCharacteristicsByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductDeliveryMethodsByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductFaqByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductMediaByProductId;
import com.surofu.exporteru.core.service.product.operation.GetProductWithTranslationsById;
import com.surofu.exporteru.core.service.product.operation.GetSearchHints;
import com.surofu.exporteru.core.service.product.operation.GetSimilarProducts;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import com.surofu.exporteru.core.service.product.operation.UpdateProductOwner;
import com.surofu.exporteru.core.service.productReview.ProductReviewService;
import com.surofu.exporteru.core.service.productReview.operation.CreateProductReview;
import com.surofu.exporteru.core.service.productReview.operation.DeleteProductReview;
import com.surofu.exporteru.core.service.productReview.operation.GetProductReviewPageByProductId;
import com.surofu.exporteru.core.service.productReview.operation.UpdateProductReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Products", description = "API for managing product")
public class ProductRestController {
  private final ProductService productService;
  private final ProductReviewService productReviewService;
  private final OrderService orderService;
  private final LocalizationManager localizationManager;
  private final GetProductById.Result.Processor<ResponseEntity<?>> getProductByIdProcessor;
  private final GetProductWithTranslationsById.Result.Processor<ResponseEntity<?>>
      getProductWithTranslationsByIdProcessor;
  private final GetProductByArticle.Result.Processor<ResponseEntity<?>>
      getProductByArticleProcessor;
  private final GetProductCategoryByProductId.Result.Processor<ResponseEntity<?>>
      getProductCategoryByProductIdProcessor;
  private final GetProductDeliveryMethodsByProductId.Result.Processor<ResponseEntity<?>>
      getProductDeliveryMethodsByProductIdProcessor;
  private final GetProductMediaByProductId.Result.Processor<ResponseEntity<?>>
      getProductMediaByProductIdProcessor;
  private final GetProductCharacteristicsByProductId.Result.Processor<ResponseEntity<?>>
      getProductCharacteristicsByProductIdProcessor;
  private final GetProductReviewPageByProductId.Result.Processor<ResponseEntity<?>>
      getProductReviewPageByProductIdProcessor;
  private final GetProductFaqByProductId.Result.Processor<ResponseEntity<?>>
      getProductFaqByProductIdProcessor;
  private final CreateProductReview.Result.Processor<ResponseEntity<?>>
      createProductReviewProcessor;
  private final UpdateProductReview.Result.Processor<ResponseEntity<?>>
      updateProductReviewProcessor;
  private final DeleteProductReview.Result.Processor<ResponseEntity<?>>
      deleteProductReviewProcessor;
  private final CreateProduct.Result.Processor<ResponseEntity<?>> createProductProcessor;
  private final UpdateProduct.Result.Processor<ResponseEntity<?>> updateProductProcessor;
  private final GetSearchHints.Result.Processor<ResponseEntity<?>> getSearchHintsProcessor;
  private final DeleteProductById.Result.Processor<ResponseEntity<?>> deleteProductByIdProcessor;
  private final CreateOrder.Result.Processor<ResponseEntity<?>> createOrderProcessor;
  private final UpdateProductOwner.Result.Processor<ResponseEntity<?>> updateProductOwnerProcessor;
  private final GetSimilarProducts.Result.Processor<ResponseEntity<?>> getSimilarProductsProcessor;

  @GetMapping("{productId}")
  @Operation(summary = "Get product by ID")
  public ResponseEntity<?> getProductById(
      @PathVariable
      Long productId,
      @RequestParam(name = "hasTranslations", required = false, defaultValue = "false")
      Boolean hasTranslations,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    if (hasTranslations) {
      GetProductWithTranslationsById operation =
          GetProductWithTranslationsById.of(productId, locale, securityUser);
      return productService.getProductWithTranslationsByProductId(operation)
          .process(getProductWithTranslationsByIdProcessor);
    }
    GetProductById operation = GetProductById.of(locale, productId, securityUser);
    return productService.getProductById(operation).process(getProductByIdProcessor);
  }

  @GetMapping("article/{article}")
  @Operation(summary = "Get product by article code")
  public ResponseEntity<?> getProductByArticle(@PathVariable String article) {
    Locale locale = LocaleContextHolder.getLocale();
    GetProductByArticle operation = GetProductByArticle.of(locale, ProductArticleCode.of(article));
    return productService.getProductByArticle(operation).process(getProductByArticleProcessor);
  }

  @GetMapping("{productId}/category")
  @Operation(summary = "Get product category by product ID")
  public ResponseEntity<?> getProductCategoryByProductId(@PathVariable Long productId) {
    Locale locale = LocaleContextHolder.getLocale();
    GetProductCategoryByProductId operation = GetProductCategoryByProductId.of(productId, locale);
    return productService.getProductCategoryByProductId(operation)
        .process(getProductCategoryByProductIdProcessor);
  }

  @GetMapping("{productId}/delivery-methods")
  @Operation(summary = "Get product delivery methods by product ID")
  public ResponseEntity<?> getProductDeliveryMethodsByProductId(@PathVariable Long productId) {
    GetProductDeliveryMethodsByProductId operation =
        GetProductDeliveryMethodsByProductId.of(productId);
    return productService.getProductDeliveryMethodsByProductId(operation)
        .process(getProductDeliveryMethodsByProductIdProcessor);
  }

  @GetMapping("{productId}/media")
  @Operation(summary = "Get product media by product ID")
  public ResponseEntity<?> getProductMediaByProductId(@PathVariable Long productId) {
    GetProductMediaByProductId operation = GetProductMediaByProductId.of(productId);
    return productService.getProductMediaByProductId(operation)
        .process(getProductMediaByProductIdProcessor);
  }

  @GetMapping("{productId}/characteristics")
  @Operation(summary = "Get product characteristics by product ID")
  public ResponseEntity<?> getProductCharacteristicsByProductId(@PathVariable Long productId) {
    GetProductCharacteristicsByProductId operation =
        GetProductCharacteristicsByProductId.of(productId);
    return productService.getProductCharacteristicsByProductId(operation)
        .process(getProductCharacteristicsByProductIdProcessor);
  }

  @GetMapping("{productId}/reviews")
  @Operation(summary = "Get filtered and paginated list of product reviews")
  public ResponseEntity<?> getProductReviewPageByProductId(
      @PathVariable Long productId,
      @RequestParam(defaultValue = "0")
      @Min(0)
      int page,
      @RequestParam(defaultValue = "10")
      @Min(1)
      @Max(100)
      int size,
      @RequestParam(required = false)
      Integer minRating,
      @RequestParam(required = false)
      Integer maxRating
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    GetProductReviewPageByProductId operation =
        GetProductReviewPageByProductId.of(productId, page, size, minRating, maxRating, locale);
    return productReviewService.getProductReviewPageByProductId(operation)
        .process(getProductReviewPageByProductIdProcessor);
  }

  @PostMapping(value = "{productId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Create product review")
  public ResponseEntity<?> createProductReview(
      @PathVariable Long productId,
      @RequestPart("data") CreateProductReviewCommand command,
      @RequestPart(value = "media", required = false) List<MultipartFile> media,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    CreateProductReview operation = CreateProductReview.of(
        productId,
        securityUser,
        ProductReviewContent.of(command.text()),
        ProductReviewRating.of(command.rating()),
        Objects.requireNonNullElse(media, Collections.emptyList())
    );
    return productReviewService.createProductReview(operation)
        .process(createProductReviewProcessor);
  }

  @PatchMapping("{productId}/reviews/{productReviewId}")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Update product review by id")
  public ResponseEntity<?> updateProductReviewById(
      @PathVariable Long productId,
      @PathVariable Long productReviewId,
      @RequestBody @Valid UpdateProductReviewCommand updateProductReviewCommand,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    UpdateProductReview operation = UpdateProductReview.of(
        productId,
        productReviewId,
        securityUser,
        ProductReviewContent.of(updateProductReviewCommand.text()),
        ProductReviewRating.of(updateProductReviewCommand.rating())
    );
    return productReviewService.updateProductReview(operation)
        .process(updateProductReviewProcessor);
  }

  @DeleteMapping("{productId}/reviews/{productReviewId}")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Delete product review by id")
  public ResponseEntity<?> deleteProductReviewById(
      @PathVariable Long productId,
      @PathVariable Long productReviewId,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    DeleteProductReview operation = DeleteProductReview.of(
        productId,
        productReviewId,
        securityUser
    );
    return productReviewService.deleteProductReview(operation)
        .process(deleteProductReviewProcessor);
  }

  @GetMapping("{productId}/faq")
  @Operation(summary = "Get list of product faq (question and answer)")
  public ResponseEntity<?> getProductFaqByProductId(
      @PathVariable Long productId) {
    GetProductFaqByProductId operation = GetProductFaqByProductId.of(productId);
    return productService.getProductFaqByProductId(operation)
        .process(getProductFaqByProductIdProcessor);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @Operation(summary = "Create new product")
  public ResponseEntity<?> createProduct(
      @RequestPart("data") @Valid CreateProductCommand createProductCommand,
      @RequestPart("productMedia") List<MultipartFile> productMedia,
      @RequestPart(value = "aboutVendorMedia", required = false)
      List<MultipartFile> productVendorDetailsMedia,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    if (createProductCommand.prices() == null || createProductCommand.prices().isEmpty()) {
      String message = localizationManager.localize("validation.product.create.empty_prices");
      SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    if (createProductCommand.characteristics() == null ||
        createProductCommand.characteristics().isEmpty()) {
      String message =
          localizationManager.localize("validation.product.create.empty_characteristics");
      SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    if (productMedia == null || productMedia.isEmpty()) {
      String message =
          localizationManager.localize("validation.product.create.empty_product_media");
      SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    ProductTitle productTitle =
        new ProductTitle(createProductCommand.title(), createProductCommand.titleTranslations());
    ProductDescription productDescription = new ProductDescription(
        createProductCommand.mainDescription(),
        createProductCommand.furtherDescription(),
        createProductCommand.mainDescriptionTranslations(),
        createProductCommand.furtherDescriptionTranslations()
    );
    CreateProduct operation = CreateProduct.of(
        securityUser,
        productTitle,
        productDescription,
        createProductCommand.categoryId(),
        createProductCommand.deliveryMethodIds(),
        Objects.requireNonNullElse(createProductCommand.deliveryTermIds(), new ArrayList<>()),
        Objects.requireNonNullElse(createProductCommand.similarProducts(), new ArrayList<>()),
        createProductCommand.prices(),
        createProductCommand.characteristics(),
        Objects.requireNonNullElse(createProductCommand.faq(), new ArrayList<>()),
        Objects.requireNonNullElse(createProductCommand.deliveryMethodDetails(), new ArrayList<>()),
        Objects.requireNonNullElse(createProductCommand.packageOptions(), new ArrayList<>()),
        Objects.requireNonNullElse(createProductCommand.mediaAltTexts(), new ArrayList<>()),
        ProductMinimumOrderQuantity.of(createProductCommand.minimumOrderQuantity()),
        ProductDiscountExpirationDate.of(
            ZonedDateTime.now().plusDays(createProductCommand.discountExpirationDate())),
        productMedia,
        Objects.requireNonNullElse(productVendorDetailsMedia, new ArrayList<>())
    );
    return productService.createProduct(operation).process(createProductProcessor);
  }

  @PutMapping(value = "{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @Operation(summary = "Update product by ID")
  public ResponseEntity<?> updateProductById(
      @PathVariable
      Long productId,
      @RequestPart("data") @Valid UpdateProductCommand updateProductCommand,
      @RequestPart(value = "productMedia", required = false) List<MultipartFile> productMedia,
      @RequestPart(value = "aboutVendorMedia", required = false)
      List<MultipartFile> productVendorDetailsMedia,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    if (updateProductCommand.prices() == null || updateProductCommand.prices().isEmpty()) {
      String message = localizationManager.localize("validation.product.update.empty_prices");
      SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    if (updateProductCommand.characteristics() == null ||
        updateProductCommand.characteristics().isEmpty()) {
      String message =
          localizationManager.localize("validation.product.update.empty_characteristics");
      SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    if ((productMedia == null || productMedia.isEmpty()) &&
        updateProductCommand.oldProductMedia().isEmpty()) {
      String message = localizationManager.localize("validation.product.update.empty_media");
      SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
      return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    ProductTitle productTitle =
        new ProductTitle(Objects.requireNonNullElse(updateProductCommand.title(), ""),
            updateProductCommand.titleTranslations());
    ProductDescription productDescription = new ProductDescription(
        updateProductCommand.mainDescription(),
        updateProductCommand.furtherDescription(),
        updateProductCommand.mainDescriptionTranslations(),
        updateProductCommand.furtherDescriptionTranslations()
    );
    UpdateProduct operation = UpdateProduct.of(
        productId,
        securityUser,
        productTitle,
        productDescription,
        updateProductCommand.categoryId(),
        Objects.requireNonNullElse(updateProductCommand.deliveryMethodIds(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.deliveryTermIds(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.similarProducts(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.prices(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.characteristics(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.faq(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.deliveryMethodDetails(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.packageOptions(), new ArrayList<>()),
        Objects.requireNonNullElse(updateProductCommand.mediaAltTexts(), new ArrayList<>()),
        ProductMinimumOrderQuantity.of(updateProductCommand.minimumOrderQuantity()),
        ProductDiscountExpirationDate.of(
            ZonedDateTime.now().plusDays(updateProductCommand.discountExpirationDate())),
        Objects.requireNonNullElse(updateProductCommand.oldProductMedia(), Collections.emptyList()),
        Objects.requireNonNullElse(updateProductCommand.oldAboutVendorMedia(),
            Collections.emptyList()),
        Objects.requireNonNullElse(productMedia, new ArrayList<>()),
        Objects.requireNonNullElse(productVendorDetailsMedia, new ArrayList<>())
    );
    return productService.updateProduct(operation).process(updateProductProcessor);
  }

  @GetMapping("hints")
  @Operation(summary = "Get product search hints")
  public ResponseEntity<?> getSearchHints(
      @RequestParam(required = false, defaultValue = "$$$") String text,
      @RequestParam(required = false) Long vendorId
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    GetSearchHints operation = GetSearchHints.of(text, vendorId, locale);
    return productService.getSearchHints(operation).process(getSearchHintsProcessor);
  }

  @DeleteMapping("{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Delete product by ID")
  public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
    DeleteProductById operation = DeleteProductById.of(id);
    return productService.deleteProductById(operation).process(deleteProductByIdProcessor);
  }

  @PostMapping("{id}/create-order")
  public ResponseEntity<?> createOrder(
      @PathVariable Long id,
      @RequestBody @Valid CreateOrderCommand command
  ) {
    CreateOrder operation = CreateOrder.of(id, command.firstName(), command.email().toLowerCase(),
        command.phoneNumber(), command.quantity());
    return orderService.createOrder(operation).process(createOrderProcessor);
  }

  @PatchMapping("{productId}/owner/{ownerId}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Update product owner")
  public ResponseEntity<?> updateOwner(
      @PathVariable Long productId,
      @PathVariable Long ownerId
  ) {
    UpdateProductOwner operation = UpdateProductOwner.of(productId, ownerId);
    return productService.updateProductOwner(operation).process(updateProductOwnerProcessor);
  }

  @GetMapping("{id}/similar")
  @Operation(summary = "Get similar products")
  public ResponseEntity<?> getSimilar(@PathVariable Long id, @AuthenticationPrincipal SecurityUser securityUser) {
    GetSimilarProducts operation = GetSimilarProducts.of(id , securityUser);
    return productService.getSimilarProducts(operation).process(getSimilarProductsProcessor);
  }
}
