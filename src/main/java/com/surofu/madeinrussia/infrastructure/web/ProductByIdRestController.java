package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.product.GetProductByIdQuery;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductById;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products/{id}")
@Tag(name = "Product By ID", description = "API for managing product by ID")
public class ProductByIdRestController {

    private final ProductService productService;

    private final GetProductById.Result.Processor<ResponseEntity<?>> getProductByIdProcessor;

    @GetMapping
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a single product by its unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<?> getProductById(
            @Parameter(
                    name = "id",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id
    ) {
        GetProductByIdQuery query = new GetProductByIdQuery(id);
        return productService.getProductById(GetProductById.of(query)).process(getProductByIdProcessor);
    }
}
