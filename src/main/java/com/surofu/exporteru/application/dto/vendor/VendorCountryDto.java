package com.surofu.exporteru.application.dto.vendor;

import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.infrastructure.persistence.vendor.country.VendorCountryView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "VendorCountry")
public final class VendorCountryDto implements Serializable {
  private Long id;
  private String name;
  private String value;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static VendorCountryDto of(VendorCountry vendorCountry) {
    if (vendorCountry == null) {
      return null;
    }
    return VendorCountryDto.builder()
        .id(vendorCountry.getId())
        .name(vendorCountry.getName().getLocalizedValue())
        .value(vendorCountry.getName().getTranslations()
            .getOrDefault("en", vendorCountry.getName().getLocalizedValue()))
        .creationDate(vendorCountry.getCreationDate().getValue())
        .lastModificationDate(vendorCountry.getLastModificationDate().getValue())
        .build();
  }

  public static VendorCountryDto of(VendorCountryView view) {
    if (view == null) {
      return null;
    }
    return VendorCountryDto.builder()
        .id(view.getId())
        .name(view.getName())
        .value(view.getValue())
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }
}