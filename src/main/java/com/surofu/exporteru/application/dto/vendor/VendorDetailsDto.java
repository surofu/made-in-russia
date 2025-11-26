package com.surofu.exporteru.application.dto.vendor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmail;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSite;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.exporteru.infrastructure.persistence.vendor.VendorDetailsView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "VendorDetails",
    description = "Contains comprehensive business information about a vendor including registration details, operational countries, product categories, and FAQs"
)
public final class VendorDetailsDto implements Serializable {

  @Schema(
      description = "Unique database identifier of the vendor details record",
      example = "789",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  private Long id;

  @Schema(
      description = "Vendor's Tax Identification Number (ИНН) as registered with Russian tax authorities",
      example = "7707083893",
      minLength = 10,
      maxLength = 12,
      pattern = "^[0-9]{10,12}$",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  private String inn;

  private String address;

  @JsonIgnore
  private String addressTranslations;

  private String description;

  @Builder.Default
  private List<String> phoneNumbers = new ArrayList<>();

  @Builder.Default
  private List<String> emails = new ArrayList<>();

  @Builder.Default
  private List<String> sites = new ArrayList<>();

  @Builder.Default
  private List<VendorMediaDto> media = new ArrayList<>();

  @Schema(
      description = "List of countries where the vendor has business operations",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @Builder.Default
  private List<VendorCountryDto> countries = new ArrayList<>();

  @Schema(
      description = "Product categories the vendor is authorized to sell in",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @Builder.Default
  private List<VendorProductCategoryDto> productCategories = new ArrayList<>();

  @Schema(
      description = "Frequently Asked Questions specific to this vendor's operations",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @Builder.Default
  private List<VendorFaqDto> faq = new ArrayList<>();

  @Schema(
      description = "Counter of authenticated user profile views, automatically incremented",
      example = "789",
      minimum = "0",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  @Builder.Default
  private Long viewsCount = 0L;

  @Schema(
      description = "System timestamp of initial vendor details registration",
      example = "2025-05-15T14:30:00Z",
      type = "string",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  private ZonedDateTime creationDate;

  @Schema(
      description = "System timestamp of last modification to vendor details",
      example = "2025-06-01T10:15:30Z",
      type = "string",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  private ZonedDateTime lastModificationDate;

  @Schema(hidden = true)
  public static VendorDetailsDto of(VendorDetails vendorDetails, Locale locale) {
    if (vendorDetails == null) {
      return null;
    }

    String address = null;

    if (vendorDetails.getAddress() != null) {
      address = vendorDetails.getAddress().getLocalizedValue(locale);
    }

    String description = null;

    if (vendorDetails.getDescription() != null) {
      description = vendorDetails.getDescription().getLocalizedValue(locale);
    }

    return VendorDetailsDto.builder()
        .id(vendorDetails.getId())
        .inn(vendorDetails.getInn().toString())
        .address(address)
        .description(description)
        .phoneNumbers(
            vendorDetails.getPhoneNumbers().stream().map(VendorPhoneNumber::getPhoneNumber)
                .map(VendorPhoneNumberPhoneNumber::toString).toList())
        .emails(vendorDetails.getEmails().stream().map(VendorEmail::getEmail)
            .map(VendorEmailEmail::toString).toList())
        .sites(
            vendorDetails.getSites().stream().map(VendorSite::getUrl).map(VendorSiteUrl::toString)
                .toList())
        .media(vendorDetails.getMedia().stream().map(VendorMediaDto::of).toList())
        .countries(
            vendorDetails.getVendorCountries().stream()
                .map(VendorCountryDto::of)
                .toList())
        .productCategories(
            vendorDetails.getVendorProductCategories().stream().map(VendorProductCategoryDto::of)
                .toList())
        .faq(vendorDetails.getFaq().stream().map(VendorFaqDto::of).toList())
        .viewsCount(vendorDetails.getVendorViewsCount())
        .creationDate(vendorDetails.getCreationDate().getValue())
        .lastModificationDate(vendorDetails.getLastModificationDate().getValue())
        .build();
  }

  @Schema(hidden = true)
  public static VendorDetailsDto of(VendorDetailsView view, Locale locale) {
    if (view == null) {
      return null;
    }

    String address = null;

    if (view.getAddress() != null) {
      address = view.getAddress().getLocalizedValue(locale);
    }

    String description = null;

    if (view.getDescription() != null && view.getDescription().getTranslations() != null) {
      description = view.getDescription().getLocalizedValue(locale);
    }

    return VendorDetailsDto.builder()
        .id(view.getId())
        .inn(view.getInn().toString())
        .address(address)
        .description(description)
        .viewsCount(view.getViewsCount())
        .creationDate(view.getCreationDate().getValue())
        .lastModificationDate(view.getLastModificationDate().getValue())
        .build();
  }
}