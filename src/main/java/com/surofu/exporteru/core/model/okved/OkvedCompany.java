package com.surofu.exporteru.core.model.okved;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Company information with OKVED classification")
public record OkvedCompany(
        @Schema(description = "Official company name", example = "ООО Ромашка")
        String name,

        @Schema(
                description = "Tax Identification Number (10 or 12 digits)",
                example = "7701234567",
                pattern = "^[0-9]{10,12}$"
        )
        String inn,

        @Schema(
                description = "Company age in years",
                example = "5",
                minimum = "0",
                maximum = "200"
        )
        Integer ageInYears
) implements Serializable {
}
