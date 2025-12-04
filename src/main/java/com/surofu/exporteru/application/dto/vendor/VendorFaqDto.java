package com.surofu.exporteru.application.dto.vendor;

import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.exporteru.infrastructure.persistence.vendor.faq.VendorFaqView;
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
@Schema(name = "VendorFaq")
public final class VendorFaqDto implements Serializable {
  private Long id;
  private String question;
  private String answer;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static VendorFaqDto of(VendorFaq vendorFaq) {
    return VendorFaqDto.builder()
        .id(vendorFaq.getId())
        .question(vendorFaq.getQuestion().toString())
        .answer(vendorFaq.getAnswer().toString())
        .creationDate(vendorFaq.getCreationDate().getValue())
        .lastModificationDate(vendorFaq.getLastModificationDate().getValue())
        .build();
  }

  public static VendorFaqDto of(VendorFaqView view) {
    return VendorFaqDto.builder()
        .id(view.getId())
        .question(view.getQuestion())
        .answer(view.getAnswer())
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }
}