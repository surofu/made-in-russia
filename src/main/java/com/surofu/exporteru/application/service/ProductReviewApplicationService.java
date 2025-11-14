package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.product.ProductReviewDto;
import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductPreviewImageUrl;
import com.surofu.exporteru.core.model.product.ProductTitle;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.product.review.media.*;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.model.vendorDetails.view.VendorView;
import com.surofu.exporteru.core.repository.*;
import com.surofu.exporteru.core.repository.specification.ProductReviewSpecifications;
import com.surofu.exporteru.core.service.product.review.ProductReviewService;
import com.surofu.exporteru.core.service.product.review.operation.*;
import com.surofu.exporteru.infrastructure.persistence.product.ProductForReviewView;
import com.surofu.exporteru.infrastructure.persistence.translation.TranslationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewApplicationService implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final VendorViewRepository vendorViewRepository;
    private final TranslationRepository translationRepository;
    private final FileStorageRepository fileStorageRepository;
    private final TaskExecutor appTaskExecutor;
    private final TransactionTemplate transactionTemplate;

    @Override
    @Transactional(readOnly = true)
    public GetProductReviewPage.Result getProductReviewPage(GetProductReviewPage operation) {
        String[] sortStrings = operation.getSort().split(",");
        Sort sort = Sort.by(Sort.Direction.fromString(operation.getDirection()), sortStrings);
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), sort);

        Specification<ProductReview> specification = Specification
                .where(ProductReviewSpecifications.ratingBetween(operation.getMinRating(), operation.getMaxRating()))
                .and(ProductReviewSpecifications.byContent(operation.getContent()))
                .and(ProductReviewSpecifications.approveStatusIn(operation.getApproveStatuses()));

        Page<ProductReview> page = productReviewRepository.getPage(specification, pageable);

        if (!page.getContent().isEmpty()) {
            List<Long> reviewIds = page.getContent().stream()
                    .map(ProductReview::getId)
                    .toList();

            List<ProductReview> reviewsWithMedia = productReviewRepository.getByIdInWithMedia(reviewIds);

            List<ProductForReviewView> productForReviewViewList = productRepository.getProductForReviewViewsByLang(operation.getLocale().getLanguage());

            Map<Long, ProductReview> reviewMap = reviewsWithMedia.stream()
                    .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

            Map<Long, ProductForReviewView> productForReviewViewMap = productForReviewViewList.stream()
                    .collect(Collectors.toMap(ProductForReviewView::getId, Function.identity()));

            Page<ProductReview> productReviewPageWithMedia = page.map(p -> {
                if (reviewMap.containsKey(p.getId())) {
                    p.setMedia(reviewMap.get(p.getId()).getMedia());
                }

                if (productForReviewViewMap.containsKey(p.getProductId())) {
                    ProductForReviewView productForReviewView = productForReviewViewMap.get(p.getProductId());
                    Product productReference = productRepository.getReferenceById(p.getProductId());
                    productReference.setTitle(ProductTitle.of(productForReviewView.getTitle()));
                    productReference.setPreviewImageUrl(ProductPreviewImageUrl.of(productForReviewView.getPreviewImageUrl()));
                    p.setProduct(productReference);
                }

                return p;
            });

            Page<ProductReviewDto> dtoPage = productReviewPageWithMedia
                    .map(r -> ProductReviewDto.of(r, operation.getLocale()))
                    .map(this::transliterateUserLogin);
            return GetProductReviewPage.Result.success(dtoPage);
        }

        Page<ProductReviewDto> productReviewDtoPage = page
                .map(ProductReviewDto::of)
                .map(this::transliterateUserLogin);
        return GetProductReviewPage.Result.success(productReviewDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductReviewPageByProductId.Result getProductReviewPageByProductId(GetProductReviewPageByProductId operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), Sort.by("creationDate").descending());
        Specification<ProductReview> specification = Specification
                .where(ProductReviewSpecifications.byProductId(operation.getProductId()))
                .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(), operation.getMaxRating()))
                .and(ProductReviewSpecifications.approveStatusIn(ApproveStatus.APPROVED));
        Page<ProductReview> productReviewPage = productReviewRepository.getPage(specification, pageable);
        if (!productReviewPage.getContent().isEmpty()) {
            List<Long> reviewIds = productReviewPage.getContent().stream()
                    .map(ProductReview::getId)
                    .toList();
            List<ProductReview> reviewsWithMedia = productReviewRepository.getByIdInWithMedia(reviewIds);
            List<ProductForReviewView> productForReviewViewList = productRepository.getProductForReviewViewsByLang(operation.getLocale().getLanguage());
            Map<Long, ProductReview> reviewMap = reviewsWithMedia.stream()
                    .collect(Collectors.toMap(ProductReview::getId, Function.identity()));
            Map<Long, ProductForReviewView> productForReviewViewMap = productForReviewViewList.stream()
                    .collect(Collectors.toMap(ProductForReviewView::getId, Function.identity()));
            Page<ProductReview> productReviewPageWithMedia = productReviewPage.map(p -> {
                if (reviewMap.containsKey(p.getId())) {
                    p.setMedia(reviewMap.get(p.getId()).getMedia());
                }
                if (productForReviewViewMap.containsKey(p.getProductId())) {
                    ProductForReviewView productForReviewView = productForReviewViewMap.get(p.getProductId());
                    Product productReference = productRepository.getReferenceById(p.getProductId());
                    productReference.setTitle(ProductTitle.of(productForReviewView.getTitle()));
                    productReference.setPreviewImageUrl(ProductPreviewImageUrl.of(productForReviewView.getPreviewImageUrl()));
                    p.setProduct(productReference);
                }
                return p;
            });
            Page<ProductReviewDto> productReviewDtoPage = productReviewPageWithMedia
                    .map(r -> ProductReviewDto.of(r, operation.getLocale()))
                    .map(this::transliterateUserLogin);
            return GetProductReviewPageByProductId.Result.success(productReviewDtoPage);
        }
        Page<ProductReviewDto> productReviewDtoPage = productReviewPage
                .map(r -> translateProductReview(r, operation.getLocale()))
                .map(this::transliterateUserLogin);
        return GetProductReviewPageByProductId.Result.success(productReviewDtoPage);
    }

    private static ProductReviewDto translateProductReview(ProductReview productReview, Locale locale) {
        ProductReviewDto dto = ProductReviewDto.of(productReview);

        String translatedProductTitle =
            productReview.getProduct().getTitle().getTranslations().getLocale(locale);

        if (StringUtils.trimToNull(translatedProductTitle) != null) {
            dto.getProduct().setTitle(translatedProductTitle);
        }

        if (productReview.getUser().getLogin().getTransliteration() != null) {
            String translatedUserLogin =
                productReview.getUser().getLogin().getTransliteration().getLocale(locale);
            dto.getAuthor().setLogin(translatedUserLogin);
        }

        String translatedText = productReview.getContent().getTranslations().getLocale(locale);

        if (StringUtils.trimToNull(translatedText) != null) {
            dto.setText(translatedText);
        }

        return dto;
    }

    private ProductReviewDto transliterateUserLogin(ProductReviewDto dto) {
        dto.getAuthor().setLogin(TransliterationManager.transliterate(dto.getAuthor().getLogin()));
        return dto;
    }

    @Override
    @Transactional
    public CreateProductReview.Result createProductReview(CreateProductReview operation) {
        // Validation
        for (MultipartFile media : operation.getMedia()) {
            if (media == null || media.isEmpty()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return CreateProductReview.Result.emptyFile();
            }

            String contentType = Objects.requireNonNullElse(media.getContentType(), "null");

            if (!contentType.contains("image") && !contentType.contains("video")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return CreateProductReview.Result.invalidContentType(contentType);
            }
        }

        Optional<Product> product = productRepository.getProductByIdApproved(operation.getProductId());

        if (product.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.productNotFound(operation.getProductId());
        }

        User user = operation.getSecurityUser().getUser();

        if (user.getRegistrationDate().getValue().isAfter(ZonedDateTime.now().minusWeeks(1))) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.accountIsTooYoung(user.getEmail());
        }

        if (Objects.equals(user.getId(), product.get().getUser().getId())) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.userIsAuthor(user.getEmail());
        }

        Long userCurrentProductReviewsCount = productReviewRepository.getCountByProductIdAndUserId(operation.getProductId(), user.getId());

        if (userCurrentProductReviewsCount > 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.tooManyReviews(user.getEmail());
        }

        VendorView vendorView = new VendorView();
        vendorView.setVendorDetails(product.get().getUser().getVendorDetails());
        vendorView.setUser(user);

        boolean viewExists = false;

        if (product.get().getUser().getVendorDetails() != null) {
            viewExists = vendorViewRepository.existsByUserIdAndVendorDetailsId(user.getId(), product.get().getUser().getVendorDetails().getId());
        } else {
            log.error("Vendor Details not found for user {}", user.getEmail());
        }

        if (!viewExists) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.vendorProfileNotViewed(user.getEmail());
        }

        // Setting
        ProductReview productReview = new ProductReview();
        productReview.setUser(user);
        productReview.setProduct(product.get());
        productReview.setContent(operation.getProductReviewContent());
        productReview.setRating(operation.getProductReviewRating());

        // Translation
        HstoreTranslationDto contentTranslation;

        try {
            contentTranslation = translationRepository.expand(operation.getProductReviewContent().toString());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.translationError(e);
        }

        productReview.getContent().setTranslations(contentTranslation);

        // Upload Media
        List<CompletableFuture<String>> mediaFutureList = new ArrayList<>(operation.getMedia().size());

        for (MultipartFile file : operation.getMedia()) {
            String contentType = Objects.requireNonNullElse(file.getContentType(), "null");

            if (contentType.contains("image")) {
                mediaFutureList.add(CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
                    try {
                        return fileStorageRepository.uploadImageToFolder(file, FileStorageFolders.PRODUCT_REVIEW_IMAGES.getValue());
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }), appTaskExecutor));
            } else if (contentType.contains("video")) {
                mediaFutureList.add(CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
                    try {
                        return fileStorageRepository.uploadVideoToFolder(file, FileStorageFolders.PRODUCT_REVIEW_VIDEOS.getValue());
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }), appTaskExecutor));
            }
        }

        CompletableFuture<Void> mediaFutureAll = CompletableFuture.allOf(mediaFutureList.toArray(new CompletableFuture[0]));

        Set<ProductReviewMedia> productReviewMediaSet = new HashSet<>();

        try {
            mediaFutureAll.join();

            for (int i = 0; i < mediaFutureList.size(); i++) {
                MultipartFile file = operation.getMedia().get(i);
                ProductReviewMedia productReviewMedia = new ProductReviewMedia();
                productReviewMedia.setProductReview(productReview);

                String contentType = Objects.requireNonNullElse(file.getContentType(), "null");

                if (contentType.contains("image")) {
                    productReviewMedia.setMediaType(MediaType.IMAGE);
                } else if (contentType.contains("video")) {
                    productReviewMedia.setMediaType(MediaType.VIDEO);
                }

                productReviewMedia.setPosition(ProductReviewMediaMediaPosition.of(i));
                productReviewMedia.setMimeType(ProductReviewMediaMimeType.of(contentType));
                productReviewMedia.setAltText(ProductReviewMediaAltText.of(file.getOriginalFilename()));

                String url = mediaFutureList.get(i).get();
                productReviewMedia.setUrl(ProductReviewMediaUrl.of(url));
                productReviewMediaSet.add(productReviewMedia);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.uploadError(e);
        }

        productReview.setMedia(productReviewMediaSet);

        // Save
        try {
            productReviewRepository.save(productReview);
            return CreateProductReview.Result.success(productReview);
        } catch (Exception e) {
            for (ProductReviewMedia media : productReview.getMedia()) {
                try {
                    fileStorageRepository.deleteMediaByLink(media.getUrl().toString());
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                }
            }

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateProductReview.Result.saveError(e);
        }
    }

    @Override
    @Transactional
    public UpdateProductReview.Result updateProductReview(UpdateProductReview operation) {
        // Validation
        Optional<ProductReview> productReview = productReviewRepository.getById(operation.getProductReviewId());

        if (productReview.isEmpty()) {
            return UpdateProductReview.Result.productReviewNotFound(operation.getProductReviewId(), operation.getProductId());
        }

        User user = operation.getSecurityUser().getUser();

        if (!user.getRole().equals(UserRole.ROLE_ADMIN) && productReviewRepository.isUserOwnerOfProductReview(user.getId(), operation.getProductReviewId())) {
            return UpdateProductReview.Result.forbidden(
                    operation.getProductId(),
                    operation.getProductReviewId(),
                    user.getLogin(),
                    productReview.get().getUser().getLogin()
            );
        }

        // Setting
        if (operation.getProductReviewContent() != null) {
            productReview.get().setContent(operation.getProductReviewContent());

            // Translation
            TranslationResponse responseEn, responseRu, responseZh;

            try {
                responseEn = translationRepository.translateToEn(operation.getProductReviewContent().toString());
                responseRu = translationRepository.translateToRu(operation.getProductReviewContent().toString());
                responseZh = translationRepository.translateToZh(operation.getProductReviewContent().toString());
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateProductReview.Result.translationError(e);
            }

            productReview.get().getContent().setTranslations(new HstoreTranslationDto(
                    responseEn.getTranslations()[0].getText(),
                    responseRu.getTranslations()[0].getText(),
                    responseZh.getTranslations()[0].getText()
            ));
        }

        if (operation.getProductReviewRating() != null) {
            productReview.get().setRating(operation.getProductReviewRating());
        }

        // Save
        try {
            productReviewRepository.save(productReview.get());
            return UpdateProductReview.Result.success(productReview.get());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateProductReview.Result.saveError(e);
        }
    }

    @Override
    @Transactional
    public DeleteProductReview.Result deleteProductReview(DeleteProductReview operation) {
        User user = operation.getSecurityUser().getUser();

        Optional<ProductReview> productReview = productReviewRepository.getById(operation.getProductReviewId());

        if (productReview.isEmpty()) {
            return DeleteProductReview.Result.productReviewNotFound(operation.getProductReviewId(), operation.getProductId());
        }

        if (!user.getRole().equals(UserRole.ROLE_ADMIN) && productReviewRepository.isUserOwnerOfProductReview(user.getId(), operation.getProductReviewId())) {
            return DeleteProductReview.Result.forbidden(
                    operation.getProductId(),
                    operation.getProductReviewId(),
                    user.getLogin(),
                    productReview.get().getUser().getLogin()
            );
        }

        try {
            fileStorageRepository.deleteMediaByLink(productReview.get().getMedia().stream()
                    .map(ProductReviewMedia::getUrl)
                    .map(ProductReviewMediaUrl::getValue).distinct().toArray(String[]::new));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteProductReview.Result.deleteMediaError(e);
        }

        try {
            productReviewRepository.delete(productReview.get());
            return DeleteProductReview.Result.success(operation.getProductReviewId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteProductReview.Result.deleteError(e);
        }
    }

    @Override
    @Transactional
    public DeleteProductReviewById.Result deleteProductReviewById(DeleteProductReviewById operation) {
        Optional<ProductReview> productReview = productReviewRepository.getById(operation.getId());

        if (productReview.isEmpty()) {
            return DeleteProductReviewById.Result.notFound(operation.getId());
        }

        try {
            fileStorageRepository.deleteMediaByLink(productReview.get().getMedia().stream()
                    .map(ProductReviewMedia::getUrl)
                    .map(ProductReviewMediaUrl::getValue).distinct().toArray(String[]::new));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteProductReviewById.Result.deleteMediaError(operation.getId(), e);
        }

        try {
            productReviewRepository.delete(productReview.get());
            return DeleteProductReviewById.Result.success(operation.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteProductReviewById.Result.deleteError(operation.getId(), e);
        }
    }
}
