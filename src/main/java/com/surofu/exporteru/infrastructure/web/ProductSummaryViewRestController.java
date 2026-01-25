package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.dto.error.ValidationExceptionDto;
import com.surofu.exporteru.application.dto.product.GetProductSummaryViewPageDto;
import com.surofu.exporteru.application.dto.product.ProductSummaryViewDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.service.product.ProductSummaryService;
import com.surofu.exporteru.core.service.product.operation.GetProductSummaryViewById;
import com.surofu.exporteru.core.service.product.operation.GetProductSummaryViewPage;
import com.surofu.exporteru.core.service.product.operation.GetProductSummaryViewsByIds;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products-summary")
@Tag(name = "Products Summary", description = "API for managing product summary")
public class ProductSummaryViewRestController {

    private final ProductSummaryService productSummaryService;

    private final GetProductSummaryViewPage.Result.Processor<ResponseEntity<?>> getProductSummaryPageProcessor;
    private final GetProductSummaryViewsByIds.Result.Processor<ResponseEntity<?>> getProductSummaryViewsByIdsProcessor;
    private final GetProductSummaryViewById.Result.Processor<ResponseEntity<?>> getProductSummaryByIdProcessor;

    @GetMapping
    @Operation(summary = "Get filtered and paginated list of products summary")
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,
            @RequestParam(defaultValue = "")
            String title,
            @RequestParam(required = false)
            List<Long> deliveryMethodIds,
            @RequestParam(required = false)
            List<Long> categoryIds,
            @RequestParam(required = false)
            BigDecimal minPrice,
            @RequestParam(required = false)
            BigDecimal maxPrice,
            @RequestParam(required = false)
            List<ApproveStatus> approveStatuses,
            @RequestParam(required = false, defaultValue = "id")
            String sort,
            @RequestParam(required = false, defaultValue = "asc")
            String direction,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetProductSummaryViewPage operation = GetProductSummaryViewPage.of(
                locale,
                page,
                size,
                title,
                deliveryMethodIds,
                categoryIds,
                minPrice,
                maxPrice,
                Objects.requireNonNullElse(approveStatuses, new ArrayList<>()),
                sort,
                direction,
                securityUser
        );
        return productSummaryService.getProductSummaryPage(operation).process(getProductSummaryPageProcessor);
    }

    @GetMapping("ids")
    public ResponseEntity<?> getProductsByIds(
            @Parameter(
                    name = "ids",
                    description = "Find product summaries by it's ids",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "array",
                            format = "int64",
                            example = "[1, 2, 3]",
                            minLength = 1,
                            maxLength = 100
                    ),
                    explode = Explode.FALSE,
                    examples = {
                            @ExampleObject(
                                    name = "Single product id",
                                    value = "1",
                                    description = "Filter by single product ID"
                            ),
                            @ExampleObject(
                                    name = "Multiple product ids",
                                    value = "1,2,3",
                                    description = "Find by multiple product IDs"
                            )
                    }
            )
            @RequestParam
            List<Long> ids
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetProductSummaryViewsByIds operation = GetProductSummaryViewsByIds.of(locale, ids);
        return productSummaryService.getProductSummaryViewsByIds(operation).process(getProductSummaryViewsByIdsProcessor);
    }

    @GetMapping("{productId}")
    @Operation(
            summary = "Get product summary by ID",
            description = "Retrieves a single product by its unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductSummaryViewDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductById(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable
            Long productId
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetProductSummaryViewById operation = GetProductSummaryViewById.of(locale, productId);
        return productSummaryService.getProductSummaryViewById(operation).process(getProductSummaryByIdProcessor);
    }
}
