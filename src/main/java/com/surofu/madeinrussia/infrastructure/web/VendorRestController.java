package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.service.user.operation.GetVendorById;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vendor")
@Tag(
        name = "Vendor Profile",
        description = "API for accessing vendor information"
)
public class VendorRestController {

    private final UserService userService;

    private final GetVendorById.Result.Processor<ResponseEntity<?>> getVendorByIdProcessor;

    @GetMapping("{vendorId}")
    @Operation(
            summary = "Get vendor information by ID",
            description = "Returns complete information of the vendor",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved vendor information",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getVendorById(
            @Parameter(
                    name = "vendorId",
                    description = "ID of the vendor to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long vendorId) {
        GetVendorById operation = GetVendorById.of(vendorId);
        return userService.getVendorById(operation).process(getVendorByIdProcessor);
    }
}
