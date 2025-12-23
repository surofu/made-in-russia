package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.me.RefreshMeCurrentSessionCommand;
import com.surofu.exporteru.application.command.me.UpdateMeCommand;
import com.surofu.exporteru.application.command.me.VerifyDeleteMeCommand;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.model.user.UserRegion;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsAddress;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsDescription;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.exporteru.core.service.me.MeService;
import com.surofu.exporteru.core.service.me.operation.DeleteMe;
import com.surofu.exporteru.core.service.me.operation.DeleteMeAvatar;
import com.surofu.exporteru.core.service.me.operation.DeleteMeReviewById;
import com.surofu.exporteru.core.service.me.operation.DeleteMeSessionById;
import com.surofu.exporteru.core.service.me.operation.DeleteMeVendorMediaById;
import com.surofu.exporteru.core.service.me.operation.DeleteMeVendorMediaByIdList;
import com.surofu.exporteru.core.service.me.operation.GetMe;
import com.surofu.exporteru.core.service.me.operation.GetMeCurrentSession;
import com.surofu.exporteru.core.service.me.operation.GetMeFavoriteProducts;
import com.surofu.exporteru.core.service.me.operation.GetMeProductSummaryViewPage;
import com.surofu.exporteru.core.service.me.operation.GetMeReviewPage;
import com.surofu.exporteru.core.service.me.operation.GetMeSessions;
import com.surofu.exporteru.core.service.me.operation.GetMeVendorProductReviewPage;
import com.surofu.exporteru.core.service.me.operation.RefreshMeCurrentSession;
import com.surofu.exporteru.core.service.me.operation.SaveMeAvatar;
import com.surofu.exporteru.core.service.me.operation.ToggleMeFavoriteProductById;
import com.surofu.exporteru.core.service.me.operation.UpdateMe;
import com.surofu.exporteru.core.service.me.operation.UploadMeVendorMedia;
import com.surofu.exporteru.core.service.me.operation.VerifyDeleteMe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/me")
@Tag(name = "Current User Profile")
public class MeRestController {
  private final MeService meService;

  private final GetMe.Result.Processor<ResponseEntity<?>> getMeByJwtProcessor;
  private final GetMeSessions.Result.Processor<ResponseEntity<?>> getMeSessionsProcessor;
  private final GetMeCurrentSession.Result.Processor<ResponseEntity<?>>
      getMeCurrentSessionProcessor;
  private final RefreshMeCurrentSession.Result.Processor<ResponseEntity<?>>
      refreshMeCurrentSessionProcessor;
  private final UpdateMe.Result.Processor<ResponseEntity<?>> updateMeProcessor;
  private final GetMeReviewPage.Result.Processor<ResponseEntity<?>> getMeReviewsProcessor;
  private final GetMeVendorProductReviewPage.Result.Processor<ResponseEntity<?>>
      getMeVendorProductReviewPageProcessor;
  private final GetMeProductSummaryViewPage.Result.Processor<ResponseEntity<?>>
      getMeProductSummaryViewPageProcessor;
  private final DeleteMeSessionById.Result.Processor<ResponseEntity<?>>
      deleteMeSessionByIdProcessor;
  private final SaveMeAvatar.Result.Processor<ResponseEntity<?>> saveMeAvatarProcessor;
  private final DeleteMeAvatar.Result.Processor<ResponseEntity<?>> deleteMeAvatarProcessor;
  private final DeleteMe.Result.Processor<ResponseEntity<?>> deleteMeProcessor;
  private final VerifyDeleteMe.Result.Processor<ResponseEntity<?>> verifyDeleteMeProcessor;
  private final UploadMeVendorMedia.Result.Processor<ResponseEntity<?>>
      uploadMeVendorMediaProcessor;
  private final DeleteMeVendorMediaById.Result.Processor<ResponseEntity<?>>
      deleteMeVendorMediaByIdProcessor;
  private final DeleteMeVendorMediaByIdList.Result.Processor<ResponseEntity<?>>
      deleteMeVendorMediaByIdListProcessor;
  private final DeleteMeReviewById.Result.Processor<ResponseEntity<?>> deleteMeReviewByIdProcessor;
  private final GetMeFavoriteProducts.Result.Processor<ResponseEntity<?>>
      getMeFavoriteProductsProcessor;
  private final ToggleMeFavoriteProductById.Result.Processor<ResponseEntity<?>>
      toggleMeFavoriteProductByIdProcessor;

  @GetMapping
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Get current user profile")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getMeByJwt(@Parameter(hidden = true)
                                      @AuthenticationPrincipal SecurityUser securityUser) {
    Locale locale = LocaleContextHolder.getLocale();
    GetMe operation = GetMe.of(securityUser, locale);
    return meService.getMeByJwt(operation).process(getMeByJwtProcessor);
  }

  @GetMapping("sessions")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Get user sessions")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getMeSessions(@Parameter(hidden = true)
                                         @AuthenticationPrincipal SecurityUser securityUser) {
    GetMeSessions operation = GetMeSessions.of(securityUser);
    return meService.getMeSessions(operation).process(getMeSessionsProcessor);
  }

  @GetMapping("current-session")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Get current session details")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getMeCurrentSession(
      @Parameter(hidden = true)
      @AuthenticationPrincipal SecurityUser securityUser) {
    GetMeCurrentSession operation = GetMeCurrentSession.of(securityUser);
    return meService.getMeCurrentSession(operation).process(getMeCurrentSessionProcessor);
  }

  @PatchMapping("current-session/refresh")
  @Operation(summary = "Refresh current session")
  public ResponseEntity<?> refreshMeCurrentSessionRefresh(
      @Valid @RequestBody RefreshMeCurrentSessionCommand refreshMeCurrentSessionCommand,
      @Parameter(hidden = true)
      HttpServletRequest request
  ) {
    SessionInfo sessionInfo = SessionInfo.of(request);
    RefreshMeCurrentSession operation = RefreshMeCurrentSession.of(
        sessionInfo,
        refreshMeCurrentSessionCommand.refreshToken()
    );
    return meService.refreshMeCurrentSession(operation).process(refreshMeCurrentSessionProcessor);
  }

  @PatchMapping
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Update user profile")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> updateMe(
      @Valid @RequestBody UpdateMeCommand command,
      @Parameter(hidden = true)
      @AuthenticationPrincipal SecurityUser securityUser) {
    Locale locale = LocaleContextHolder.getLocale();
    UpdateMe operation = UpdateMe.of(
        securityUser,
        command.login() != null ? new UserLogin(command.login(), new HashMap<>()) : null,
        command.phoneNumber() != null ? new UserPhoneNumber(command.phoneNumber()) : null,
        command.region() != null ? new UserRegion(command.region()) : null,
        command.inn() != null ? new VendorDetailsInn(command.inn()) : null,
        command.address() != null ? new VendorDetailsAddress(command.address(), new HashMap<>()) :
            null,
        command.description() != null ?
            new VendorDetailsDescription(command.description(), new HashMap<>()) : null,
        command.countries() != null ?
            command.countries().stream().map(c -> new VendorCountryName(c, new HashMap<>()))
                .toList() : null,
        command.categories() != null ?
            command.categories().stream().map(c -> new VendorProductCategoryName(c, new HashMap<>())).toList() : null,
        command.phoneNumbers() != null ?
            command.phoneNumbers().stream().map(VendorPhoneNumberPhoneNumber::new).toList() : null,
        command.emails() != null ? command.emails().stream().map(VendorEmailEmail::new).toList() :
            null,
        command.sites() != null ? command.sites().stream().map(VendorSiteUrl::new).toList() : null,
        locale
    );
    return meService.updateMe(operation).process(updateMeProcessor);
  }

  @GetMapping("reviews")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Get user reviews")
  public ResponseEntity<?> getMeReviewPage(
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
      Integer maxRating,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    GetMeReviewPage operation =
        GetMeReviewPage.of(securityUser, page, size, minRating, maxRating, locale);
    return meService.getMeReviewPage(operation).process(getMeReviewsProcessor);
  }

  @DeleteMapping("reviews/{id}")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Delete review by ID, that has been left by current user")
  public ResponseEntity<?> deleteMeReviewById(@PathVariable Long id,
                                              @AuthenticationPrincipal SecurityUser securityUser) {
    DeleteMeReviewById operation = DeleteMeReviewById.of(id, securityUser);
    return meService.deleteMeReviewById(operation).process(deleteMeReviewByIdProcessor);
  }

  @GetMapping("product-reviews")
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Get user product reviews")
  public ResponseEntity<?> getMeVendorProductReviewPage(
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
      Integer maxRating,
      @AuthenticationPrincipal SecurityUser securityUser) {
    Locale locale = LocaleContextHolder.getLocale();
    GetMeVendorProductReviewPage operation =
        GetMeVendorProductReviewPage.of(securityUser, page, size, minRating, maxRating, locale);
    return meService.getMeVendorProductReviewPage(operation)
        .process(getMeVendorProductReviewPageProcessor);
  }

  @GetMapping("products-summary")
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Get paginated product summary view")
  public ResponseEntity<?> getMeProductSummaryViewPage(
      @RequestParam(defaultValue = "0")
      @Min(0)
      int page,
      @RequestParam(defaultValue = "10")
      @Min(1)
      @Max(100)
      int size,
      @RequestParam(required = false)
      String title,
      @RequestParam(required = false)
      List<Long> deliveryMethodIds,
      @RequestParam(required = false)
      List<Long> categoryIds,
      @RequestParam(required = false)
      @DecimalMin("0")
      BigDecimal minPrice,
      @RequestParam(required = false)
      @DecimalMin("0")
      BigDecimal maxPrice,
      @RequestParam(required = false, defaultValue = "id")
      String sort,
      @RequestParam(required = false, defaultValue = "asc")
      String direction,
      @RequestParam(required = false)
      List<ApproveStatus> approveStatuses,
      @AuthenticationPrincipal
      SecurityUser securityUser
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    GetMeProductSummaryViewPage operation = GetMeProductSummaryViewPage.of(
        locale,
        securityUser,
        page,
        size,
        title,
        deliveryMethodIds,
        categoryIds,
        minPrice,
        maxPrice,
        sort,
        direction,
        Objects.requireNonNullElse(approveStatuses, new ArrayList<>())
    );

    return meService.getMeProductSummaryViewPage(operation)
        .process(getMeProductSummaryViewPageProcessor);
  }

  @DeleteMapping("sessions/{sessionId}")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Delete current user session by ID")
  public ResponseEntity<?> deleteSessionById(
      @PathVariable Long sessionId,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    DeleteMeSessionById operation = DeleteMeSessionById.of(
        securityUser,
        sessionId
    );
    return meService.deleteMeSessionById(operation).process(deleteMeSessionByIdProcessor);
  }

  @PutMapping(value = "avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Upload or update user avatar")
  public ResponseEntity<?> saveAvatar(
      @RequestPart("file") MultipartFile file,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    SaveMeAvatar operation = SaveMeAvatar.of(file, securityUser);
    return meService.saveMeAvatar(operation).process(saveMeAvatarProcessor);
  }

  @DeleteMapping("avatar")
  @PreAuthorize("isAuthenticated()")
  @Operation(summary = "Remove user avatar")
  public ResponseEntity<?> deleteAvatar(
      @Parameter(hidden = true)
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    DeleteMeAvatar operation = DeleteMeAvatar.of(securityUser);
    return meService.deleteMeAvatar(operation).process(deleteMeAvatarProcessor);
  }

  @DeleteMapping("delete-account")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> deleteMe(@AuthenticationPrincipal SecurityUser securityUser) {
    Locale locale = LocaleContextHolder.getLocale();
    DeleteMe operation = DeleteMe.of(securityUser, locale);
    return meService.deleteMe(operation).process(deleteMeProcessor);
  }

  @DeleteMapping("verify-delete-account")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> verifyMe(@AuthenticationPrincipal SecurityUser securityUser,
                                    @RequestBody VerifyDeleteMeCommand command) {
    Locale locale = LocaleContextHolder.getLocale();
    VerifyDeleteMe operation = VerifyDeleteMe.of(securityUser, command.code(), locale);
    return meService.verifyDeleteMe(operation).process(verifyDeleteMeProcessor);
  }

  @PostMapping(value = "vendor/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> uploadMeVendorMedia(
      @AuthenticationPrincipal SecurityUser securityUser,
      @RequestPart("media") List<MultipartFile> media,
      @RequestPart("oldMediaIds") List<Long> oldMediaIds,
      @RequestPart("newMediaPositions") List<Integer> newMediaPositions
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    UploadMeVendorMedia operation =
        UploadMeVendorMedia.of(securityUser, media, oldMediaIds, newMediaPositions, locale);
    return meService.uploadMeVendorMedia(operation).process(uploadMeVendorMediaProcessor);
  }

  @DeleteMapping("vendor/media/{id}")
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> deleteMeVendorMediaById(
      @AuthenticationPrincipal SecurityUser securityUser,
      @PathVariable Long id
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    DeleteMeVendorMediaById operation = DeleteMeVendorMediaById.of(securityUser, id, locale);
    return meService.deleteMeVendorMediaById(operation).process(deleteMeVendorMediaByIdProcessor);
  }

  @DeleteMapping("vendor/media")
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> deleteMeVendorMedia(
      @AuthenticationPrincipal SecurityUser securityUser,
      @RequestBody List<Long> ids
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    DeleteMeVendorMediaByIdList operation = DeleteMeVendorMediaByIdList.of(securityUser,
        Objects.requireNonNullElse(ids, new ArrayList<>()), locale);
    return meService.deleteMeVendorMediaByIdList(operation)
        .process(deleteMeVendorMediaByIdListProcessor);
  }

  @GetMapping("favorite-products")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> getFavoriteProducts(
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    GetMeFavoriteProducts operation = GetMeFavoriteProducts.of(securityUser, locale);
    return meService.getMeFavoriteProducts(operation).process(getMeFavoriteProductsProcessor);
  }

  @PutMapping("favorite-products/{productId}")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> toggleFavoriteProductById(
      @PathVariable Long productId,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    ToggleMeFavoriteProductById operation = ToggleMeFavoriteProductById.of(productId, securityUser);
    return meService.toggleFavoriteProductById(operation)
        .process(toggleMeFavoriteProductByIdProcessor);
  }
}