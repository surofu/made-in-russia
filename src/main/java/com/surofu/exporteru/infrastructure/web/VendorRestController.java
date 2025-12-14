package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.vendor.CreateVendorFaqCommand;
import com.surofu.exporteru.application.command.vendor.ForceUpdateVendorCommand;
import com.surofu.exporteru.application.command.vendor.UpdateVendorFaqCommand;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsAddress;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsDescription;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaqAnswer;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaqQuestion;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.exporteru.core.service.product.ProductSummaryService;
import com.surofu.exporteru.core.service.product.operation.GetProductSummaryViewPageByVendorId;
import com.surofu.exporteru.core.service.vendor.VendorService;
import com.surofu.exporteru.core.service.vendor.operation.CreateVendorFaq;
import com.surofu.exporteru.core.service.vendor.operation.DeleteVendorFaqById;
import com.surofu.exporteru.core.service.vendor.operation.ForceUpdateVendorById;
import com.surofu.exporteru.core.service.vendor.operation.GetVendorById;
import com.surofu.exporteru.core.service.vendor.operation.GetVendorReviewPageById;
import com.surofu.exporteru.core.service.vendor.operation.SendCallRequestMail;
import com.surofu.exporteru.core.service.vendor.operation.UpdateVendorFaq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vendor")
@Tag(name = "Vendor Profile")
public class VendorRestController {
  private final VendorService vendorService;
  private final ProductSummaryService productSummaryService;
  private final GetVendorById.Result.Processor<ResponseEntity<?>> getVendorByIdProcessor;
  private final GetVendorReviewPageById.Result.Processor<ResponseEntity<?>>
      getVendorReviewPageByIdProcessor;
  private final GetProductSummaryViewPageByVendorId.Result.Processor<ResponseEntity<?>>
      getProductSummaryViewPageByVendorIdProcessor;
  private final CreateVendorFaq.Result.Processor<ResponseEntity<?>> createVendorFaqProcessor;
  private final UpdateVendorFaq.Result.Processor<ResponseEntity<?>> updateVendorFaqProcessor;
  private final DeleteVendorFaqById.Result.Processor<ResponseEntity<?>> deleteVendorFaqProcessor;
  private final ForceUpdateVendorById.Result.Processor<ResponseEntity<?>>
      forceUpdateVendorByIdProcessor;
  private final SendCallRequestMail.Result.Processor<ResponseEntity<?>>
      sendCallRequestMailProcessor;

  @GetMapping("{vendorId}")
  @Operation(summary = "Get vendor information by ID")
  public ResponseEntity<?> getVendorById(
      @PathVariable Long vendorId,
      @AuthenticationPrincipal SecurityUser securityUser) {
    Locale locale = LocaleContextHolder.getLocale();
    GetVendorById operation = GetVendorById.of(securityUser, vendorId, locale);
    return vendorService.getVendorById(operation).process(getVendorByIdProcessor);
  }

  @GetMapping("{vendorId}/products-summary")
  @Operation(summary = "Get filtered and paginated list of vendor products summary")
  public ResponseEntity<?> getProductSummary(
      @PathVariable Long vendorId,
      @RequestParam(defaultValue = "0")
      @Min(0)
      int page,
      @RequestParam(defaultValue = "10")
      @Min(1)
      @Max(100)
      int size,
      @RequestParam(defaultValue = "")
      String title,
      @RequestParam(required = false)
      List<Long> deliveryMethodIds,
      @RequestParam(required = false)
      List<Long> categoryIds,
      @RequestParam(required = false)
      BigDecimal minPrice,
      @RequestParam(required = false)
      BigDecimal maxPrice,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    GetProductSummaryViewPageByVendorId operation = GetProductSummaryViewPageByVendorId.of(
        locale,
        vendorId,
        page,
        size,
        title,
        categoryIds,
        deliveryMethodIds,
        minPrice,
        maxPrice,
        securityUser
    );
    return productSummaryService.getProductSummaryViewPageByVendorId(operation)
        .process(getProductSummaryViewPageByVendorIdProcessor);
  }

  @GetMapping("{vendorId}/reviews")
  public ResponseEntity<?> getVendorReviews(
      @PathVariable Long vendorId,
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
    GetVendorReviewPageById operation = GetVendorReviewPageById.of(
        vendorId,
        page,
        size,
        minRating,
        maxRating,
        locale
    );
    return vendorService.getVendorReviewPageById(operation)
        .process(getVendorReviewPageByIdProcessor);
  }

  @PostMapping("faq")
  @SecurityRequirement(name = "Bearer Authentication")
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @Operation(summary = "Create a new vendor FAQ")
  public ResponseEntity<?> createVendorFaq(
      @RequestBody @Valid CreateVendorFaqCommand createVendorFaqCommand,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    CreateVendorFaq operation = CreateVendorFaq.of(
        securityUser,
        new VendorFaqQuestion(createVendorFaqCommand.question(), new HashMap<>()),
        new VendorFaqAnswer(createVendorFaqCommand.answer(), new HashMap<>())
    );
    return vendorService.createVendorFaq(operation).process(createVendorFaqProcessor);
  }

  @PutMapping("faq/{faqId}")
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> updateVendorFaq(
      @PathVariable Long faqId,
      @RequestBody @Valid UpdateVendorFaqCommand updateVendorFaqCommand,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    UpdateVendorFaq operation = UpdateVendorFaq.of(
        faqId,
        new VendorFaqQuestion(updateVendorFaqCommand.question(), new HashMap<>()),
        new VendorFaqAnswer(updateVendorFaqCommand.answer(), new HashMap<>()),
        securityUser
    );
    return vendorService.updateVendorFaq(operation).process(updateVendorFaqProcessor);
  }

  @DeleteMapping("faq/{faqId}")
  @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> deleteVendorFaq(
      @PathVariable Long faqId,
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    DeleteVendorFaqById operation = DeleteVendorFaqById.of(securityUser, faqId);
    return vendorService.deleteVendorFaqById(operation).process(deleteVendorFaqProcessor);
  }

  @PutMapping("{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> forceUpdateVendorById(@PathVariable("id") Long id,
                                                 @RequestBody ForceUpdateVendorCommand command) {
    ForceUpdateVendorById operation = ForceUpdateVendorById.of(
        id,
        new UserEmail(command.email()),
        new UserLogin(command.login(), new HashMap<>()),
        new UserPhoneNumber(command.phoneNumber()),
        new VendorDetailsInn(command.inn()),
        new VendorDetailsDescription(command.description(), new HashMap<>()),
        new VendorDetailsAddress(command.address(), new HashMap<>()),
        command.countries() != null ?
            command.countries().stream().map(c -> new VendorCountryName(c, new HashMap<>()))
                .toList() : new ArrayList<>(),
        command.productCategories() != null ?
            command.productCategories().stream()
                .map(c -> new VendorProductCategoryName(c, new HashMap<>())).toList() :
            new ArrayList<>(),
        command.phoneNumbers() != null ?
            command.phoneNumbers().stream().map(VendorPhoneNumberPhoneNumber::new).toList() :
            new ArrayList<>(),
        command.emails() != null ? command.emails().stream().map(VendorEmailEmail::new).toList() :
            new ArrayList<>(),
        command.sites() != null ? command.sites().stream().map(VendorSiteUrl::new).toList() :
            new ArrayList<>()
    );
    return vendorService.forceUpdateVendorById(operation).process(forceUpdateVendorByIdProcessor);
  }

  @PostMapping("{id}/request-call")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> sendCallRequestMail(
      @PathVariable("id") Long id,
      @Parameter(hidden = true)
      @AuthenticationPrincipal SecurityUser securityUser
  ) {
    SendCallRequestMail operation = SendCallRequestMail.of(id, securityUser);
    return vendorService.sendCallRequestMail(operation).process(sendCallRequestMailProcessor);
  }
}
