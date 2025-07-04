package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/language")
@Tag(
        name = "Language",
        description = "API for retrieving language files"
)
public class LanguageController {

    @GetMapping
    @Operation(
            summary = "Get default language file",
            description = "Returns the default English language file in JSON format",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved default language file",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            description = "Path to the default language JSON file",
                                            example = "/language/en.json"
                                    )
                            )
                    )
            }
    )
    public String getDefaultLanguage() {
        return "/language/en.json";
    }

    @GetMapping("{language}")
    @Operation(
            summary = "Get language file by language code",
            description = "Returns the language file for the specified language code in JSON format",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved language file",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            description = "Path to the requested language JSON file",
                                            example = "/language/fr.json"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Language file not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public String getLanguage(
            @Parameter(
                    name = "language",
                    description = "Language code (e.g., 'en', 'fr', 'es')",
                    required = true,
                    example = "fr",
                    schema = @Schema(
                            type = "string",
                            minLength = 2,
                            maxLength = 5,
                            pattern = "^[a-zA-Z]{2,5}$"
                    )
            )
            @PathVariable String language) {
        return "/language/" + language + ".json";
    }
}
