package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.advertisement.SaveAdvertisementCommand;
import com.surofu.madeinrussia.application.dto.advertisement.AdvertisementDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.core.model.advertisement.*;
import com.surofu.madeinrussia.core.service.advertisement.AdvertisementService;
import com.surofu.madeinrussia.core.service.advertisement.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/advertisements")
@Tag(name = "Advertisements", description = "API for managing advertisements")
public class AdvertisementRestController {

    private final AdvertisementService service;

    private final GetAllAdvertisements.Result.Processor<ResponseEntity<?>> getAllAdvertisementsProcessor;
    private final GetAdvertisementById.Result.Processor<ResponseEntity<?>> getAdvertisementByIdProcessor;
    private final GetAdvertisementWithTranslationsById.Result.Processor<ResponseEntity<?>> getAdvertisementWithTranslationsByIdProcessor;
    private final CreateAdvertisement.Result.Processor<ResponseEntity<?>> createAdvertisementProcessor;
    private final UpdateAdvertisementById.Result.Processor<ResponseEntity<?>> updateAdvertisementByIdProcessor;
    private final DeleteAdvertisementById.Result.Processor<ResponseEntity<?>> deleteAdvertisementByIdProcessor;

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get all advertisements",
            description = "Retrieves a list of all available advertisements",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved advertisements",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = AdvertisementDto.class))
                            )
                    )
            }
    )
    public ResponseEntity<?> getAll() {
        Locale locale = LocaleContextHolder.getLocale();
        GetAllAdvertisements operation = GetAllAdvertisements.of(locale);
        return service.getAllAdvertisements(operation).process(getAllAdvertisementsProcessor);
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get advertisement by ID",
            description = "Retrieves a single advertisement by its unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Advertisement found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdvertisementDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Advertisement not found",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> getById(
            @Parameter(
                    description = "ID of the advertisement to retrieve",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @RequestParam(name = "hasTranslations", required = false, defaultValue = "false")
            Boolean hasTranslations) {
        Locale locale = LocaleContextHolder.getLocale();

        if (hasTranslations) {
            GetAdvertisementWithTranslationsById operation = GetAdvertisementWithTranslationsById.of(id, locale);
            return service.getAdvertisementWithTranslationsById(operation).process(getAdvertisementWithTranslationsByIdProcessor);
        }

        GetAdvertisementById operation = GetAdvertisementById.of(id, locale);
        return service.getAdvertisementById(operation).process(getAdvertisementByIdProcessor);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Create new advertisement",
            description = "Creates a new advertisement with title, subtitle and image",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Advertisement successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdvertisementDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - requires ADMIN role",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> create(
            @Parameter(
                    description = "Advertisement data",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = SaveAdvertisementCommand.class))
            )
            @RequestPart("data") SaveAdvertisementCommand command,
            @Parameter(
                    description = "Advertisement image file",
                    required = true,
                    content = @Content(mediaType = "application/octet-stream")
            )
            @RequestPart("image") MultipartFile image
    ) {
        AdvertisementTitle title = AdvertisementTitle.of(command.title());
        title.setTranslations(HstoreTranslationDto.of(command.titleTranslations()));
        AdvertisementSubtitle subtitle = AdvertisementSubtitle.of(command.subtitle());
        subtitle.setTranslations(HstoreTranslationDto.of(command.subtitleTranslations()));
        AdvertisementThirdText thirdText = AdvertisementThirdText.of(command.thirdText());
        thirdText.setTranslations(HstoreTranslationDto.of(command.thirdTextTranslations()));
        AdvertisementLink link = AdvertisementLink.of(command.link());
        AdvertisementIsBig isBig = AdvertisementIsBig.of(command.isBig());
        AdvertisementExpirationDate expirationDate = AdvertisementExpirationDate.of(command.expirationDate());
        CreateAdvertisement operation = CreateAdvertisement.of(title, subtitle, thirdText, link, isBig, expirationDate, image);
        return service.createAdvertisement(operation).process(createAdvertisementProcessor);
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Update advertisement by ID",
            description = "Updates an existing advertisement with new data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Advertisement successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdvertisementDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - requires ADMIN role",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Advertisement not found",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> updateById(
            @Parameter(
                    description = "ID of the advertisement to update",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @Parameter(
                    description = "Updated advertisement data",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = SaveAdvertisementCommand.class))
            )
            @RequestPart("data") SaveAdvertisementCommand command,
            @Parameter(
                    description = "Updated advertisement image file",
                    content = @Content(mediaType = "application/octet-stream")
            )
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        AdvertisementTitle title = AdvertisementTitle.of(command.title());
        title.setTranslations(HstoreTranslationDto.of(command.titleTranslations()));
        AdvertisementSubtitle subtitle = AdvertisementSubtitle.of(command.subtitle());
        subtitle.setTranslations(HstoreTranslationDto.of(command.subtitleTranslations()));
        AdvertisementThirdText thirdText = AdvertisementThirdText.of(command.thirdText());
        thirdText.setTranslations(HstoreTranslationDto.of(command.thirdTextTranslations()));
        AdvertisementLink link = AdvertisementLink.of(command.link());
        AdvertisementIsBig isBig = AdvertisementIsBig.of(command.isBig());
        AdvertisementExpirationDate expirationDate = AdvertisementExpirationDate.of(command.expirationDate());
        UpdateAdvertisementById operation = UpdateAdvertisementById.of(id, title, subtitle, thirdText, link, isBig, expirationDate, image);
        return service.updateAdvertisementById(operation).process(updateAdvertisementByIdProcessor);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Delete advertisement by ID",
            description = "Deletes an advertisement by its unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Advertisement successfully deleted",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - requires ADMIN role",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Advertisement not found",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> delete(
            @Parameter(
                    description = "ID of the advertisement to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        DeleteAdvertisementById operation = DeleteAdvertisementById.of(id);
        return service.deleteAdvertisementById(operation).process(deleteAdvertisementByIdProcessor);
    }
}
