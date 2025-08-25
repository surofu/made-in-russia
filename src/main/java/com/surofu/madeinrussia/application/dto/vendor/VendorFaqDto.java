package com.surofu.madeinrussia.application.dto.vendor;

import com.surofu.madeinrussia.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.faq.VendorFaqView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "VendorFaq",
        description = "Represents a vendor FAQ (Frequently Asked Questions) DTO"
)
public final class VendorFaqDto implements Serializable {

    @Schema(
            description = "Unique identifier of the FAQ entry",
            example = "42",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "FAQ question text",
            example = "How do I track my order?",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String question;

    @Schema(
            description = "FAQ answer text",
            example = "You can track your order using the tracking number sent to your email.",
            maxLength = 2000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String answer;

    @Schema(
            description = "Timestamp when the FAQ entry was created",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the FAQ entry was last modified",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static VendorFaqDto of(VendorFaq vendorFaq) {
        return VendorFaqDto.builder()
                .id(vendorFaq.getId())
                .question(vendorFaq.getQuestion().toString())
                .answer(vendorFaq.getAnswer().toString())
                .creationDate(vendorFaq.getCreationDate().getValue())
                .lastModificationDate(vendorFaq.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
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