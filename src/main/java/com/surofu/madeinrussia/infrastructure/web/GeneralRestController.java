package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.GeneralDto;
import com.surofu.madeinrussia.core.service.general.GeneralService;
import com.surofu.madeinrussia.core.service.general.operation.GetAllGeneral;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/general")
@PreAuthorize("permitAll()")
@Tag(name = "General", description = "Retrieves combined DTOs")
public class GeneralRestController {
    private final GeneralService generalService;
    private final GetAllGeneral.Result.Processor<ResponseEntity<?>> getAllGeneralProcessor;

    @GetMapping
    @Operation(
            summary = "Get all general data",
            description = "Retrieves DTO with products and categories",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "General dto retrieves",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GeneralDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> getAll() {
        Locale locale = LocaleContextHolder.getLocale();
        GetAllGeneral operation = GetAllGeneral.of(locale);
        return generalService.getAll(operation).process(getAllGeneralProcessor);
    }
}
