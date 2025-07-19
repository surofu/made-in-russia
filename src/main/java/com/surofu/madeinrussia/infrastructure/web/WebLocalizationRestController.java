package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.localization.LocalizationService;
import com.surofu.madeinrussia.core.service.localization.service.DeleteLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.GetAllLocalizations;
import com.surofu.madeinrussia.core.service.localization.service.GetLocalizationByLanguageCode;
import com.surofu.madeinrussia.core.service.localization.service.SaveLocalizationByLanguageCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/localization")
@Tag(
        name = "Web Localization",
        description = "API for managing application localizations"
)
public class WebLocalizationRestController {

    private final LocalizationService service;

    private final GetAllLocalizations.Result.Processor<ResponseEntity<?>> getAllLocalizationsProcessor;
    private final GetLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> getLocalizationByLanguageCodeProcessor;
    private final SaveLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> saveLocalizationByLanguageCodeProcessor;
    private final DeleteLocalizationByLanguageCode.Result.Processor<ResponseEntity<?>> deleteLocalizationByLanguageCodeProcessor;

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get all localizations",
            description = "Retrieves all available application localizations",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all localizations",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Map.class,
                                            example = "{\"en\": {\"welcome\": \"Welcome\"}, \"ru\": {\"welcome\": \"Добро пожаловать\"}}"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getAllLocalizations() {
        return service.getAllLocalizations().process(getAllLocalizationsProcessor);
    }

    @GetMapping("{languageCode}")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get localization by language code",
            description = "Retrieves localization for specified language",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved localization",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Map.class,
                                            example = "{\"welcome\": \"Welcome\", \"goodbye\": \"Goodbye\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Localization not found for specified language code",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> getLocalizationByLanguageCode(
            @Parameter(
                    description = "Language code (e.g., 'en', 'ru')",
                    required = true,
                    example = "en"
            )
            @PathVariable String languageCode) {
        GetLocalizationByLanguageCode operation = GetLocalizationByLanguageCode.of(languageCode);
        return service.getLocalizationByLanguageCode(operation).process(getLocalizationByLanguageCodeProcessor);
    }

    @PostMapping("{languageCode}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(
            summary = "Create or update localization",
            description = "Creates new or updates existing localization for specified language",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully saved localization",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Map.class,
                                            example = "{\"status\": \"success\", \"message\": \"Localization saved\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid localization data provided",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - admin role required"
                    )
            }
    )
    public ResponseEntity<?> saveLocalizationByLanguageCode(
            @Parameter(
                    description = "Language code (e.g., 'en', 'ru')",
                    required = true,
                    example = "en"
            )
            @PathVariable String languageCode,

            @Parameter(
                    description = "Localization content as key-value pairs",
                    required = true,
                    example = "{\"welcome\": \"Welcome\", \"goodbye\": \"Goodbye\"}"
            )
            @RequestBody Map<String, Object> content) {
        SaveLocalizationByLanguageCode operation = SaveLocalizationByLanguageCode.of(languageCode, content);
        return service.saveLocalization(operation).process(saveLocalizationByLanguageCodeProcessor);
    }

    @DeleteMapping("{languageCode}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(
            summary = "Delete localization",
            description = "Deletes localization for specified language",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted localization",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Map.class,
                                            example = "{\"status\": \"success\", \"message\": \"Localization deleted\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Localization not found for specified language code",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - admin role required"
                    )
            }
    )
    public ResponseEntity<?> deleteLocalizationByLanguageCode(
            @Parameter(
                    description = "Language code to delete (e.g., 'en', 'ru')",
                    required = true,
                    example = "en"
            )
            @PathVariable String languageCode) {
        DeleteLocalizationByLanguageCode operation = DeleteLocalizationByLanguageCode.of(languageCode);
        return service.deleteLocalization(operation).process(deleteLocalizationByLanguageCodeProcessor);
    }
}