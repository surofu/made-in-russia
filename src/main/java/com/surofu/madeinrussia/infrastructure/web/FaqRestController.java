package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.faq.CreateFaqCommand;
import com.surofu.madeinrussia.application.command.faq.UpdateFaqCommand;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.dto.faq.FaqDto;
import com.surofu.madeinrussia.application.dto.faq.FaqWithTranslationsDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.core.model.faq.FaqAnswer;
import com.surofu.madeinrussia.core.model.faq.FaqQuestion;
import com.surofu.madeinrussia.core.service.faq.FaqService;
import com.surofu.madeinrussia.core.service.faq.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/faq")
@Tag(name = "FAQ", description = "Frequently Asked Questions management API")
public class FaqRestController {

    private final FaqService service;

    private final GetAllFaq.Result.Processor<ResponseEntity<?>> getAllFaqProcessor;
    private final GetFaqById.Result.Processor<ResponseEntity<?>> getFaqByIdProcessor;
    private final GetFaqWithTranslationsById.Result.Processor<ResponseEntity<?>> getFaqWithTranslationsByIdProcessor;
    private final CreateFaq.Result.Processor<ResponseEntity<?>> createFaqProcessor;
    private final UpdateFaqById.Result.Processor<ResponseEntity<?>> updateFaqProcessor;
    private final DeleteFaqById.Result.Processor<ResponseEntity<?>> deleteFaqProcessor;

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get all FAQ items",
            description = "Retrieves all frequently asked questions and their answers",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all FAQ items"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getAllFaq() {
        Locale locale = LocaleContextHolder.getLocale();
        GetAllFaq operation = GetAllFaq.of(locale);
        return service.getAllFaq(operation).process(getAllFaqProcessor);
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get FAQ item by ID",
            description = "Retrieves a frequently asked question by its ID. Optionally can include translations.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved FAQ item",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(oneOf = {
                                                    FaqDto.class,
                                                    FaqWithTranslationsDto.class
                                            })
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID format or request parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "FAQ item not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> getFaqById(
            @Parameter(
                    description = "ID of the FAQ item to retrieve",
                    required = true,
                    example = "123"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "Whether to include translations in the response",
                    example = "false"
            )
            @RequestParam(required = false, defaultValue = "false") Boolean hasTranslations
    ) {
        Locale locale = LocaleContextHolder.getLocale();

        if (hasTranslations) {
            GetFaqWithTranslationsById operation = GetFaqWithTranslationsById.of(id, locale);
            return service.getFaqWithTranslationsById(operation).process(getFaqWithTranslationsByIdProcessor);
        }

        GetFaqById operation = GetFaqById.of(id, locale);
        return service.getFaqById(operation).process(getFaqByIdProcessor);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Create new FAQ item",
            description = "Creates a new frequently asked question with its answer. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created FAQ item",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters or validation errors",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - authentication required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions (ROLE_ADMIN required)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> createFaq(
            @Parameter(
                    description = "FAQ creation request containing question and answer",
                    required = true,
                    schema = @Schema(implementation = CreateFaqCommand.class)
            )
            @RequestBody @Valid CreateFaqCommand command
    ) {
        CreateFaq operation = CreateFaq.of(
                FaqQuestion.of(command.question()),
                HstoreTranslationDto.of(command.questionTranslations()),
                FaqAnswer.of(command.answer()),
                HstoreTranslationDto.of(command.answerTranslations())
        );
        return service.createFaq(operation).process(createFaqProcessor);
    }

    @PutMapping("{faqId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Update FAQ item by ID",
            description = "Updates an existing frequently asked question and its answer by ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated FAQ item",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters or validation errors",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - authentication required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions (ROLE_ADMIN required)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "FAQ item not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> updateFaqById(
            @Parameter(
                    name = "faqId",
                    description = "ID of the FAQ item to be updated",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long faqId,

            @Parameter(
                    description = "FAQ update request containing new question and answer",
                    required = true,
                    schema = @Schema(implementation = UpdateFaqCommand.class)
            )
            @RequestBody @Valid UpdateFaqCommand command
    ) {
        UpdateFaqById operation = UpdateFaqById.of(
                faqId,
                FaqQuestion.of(command.question()),
                HstoreTranslationDto.of(command.questionTranslations()),
                FaqAnswer.of(command.answer()),
                HstoreTranslationDto.of(command.answerTranslations())
        );
        return service.updateFaqById(operation).process(updateFaqProcessor);
    }

    @DeleteMapping("{faqId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Delete FAQ item by ID",
            description = "Deletes an existing frequently asked question by ID. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted FAQ item"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - authentication required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions (ROLE_ADMIN required)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "FAQ item not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteFaqById(
            @Parameter(
                    name = "faqId",
                    description = "ID of the FAQ item to be deleted",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long faqId
    ) {
        DeleteFaqById operation = DeleteFaqById.of(faqId);
        return service.deleteFaqById(operation).process(deleteFaqProcessor);
    }
}