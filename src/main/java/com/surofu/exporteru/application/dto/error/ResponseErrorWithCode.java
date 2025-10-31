package com.surofu.exporteru.application.dto.error;

import com.surofu.exporteru.application.enums.ResponseErrorCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "SimpleErrorResponse",
        description = "Represents a error response DTO"
)
public class ResponseErrorWithCode implements Serializable {
    @Schema(
            description = "Error type",
            example = "Not Found"
    )
    private String error;

    @Schema(
            description = "Error message describing what went wrong",
            example = "Product with ID '123' not found"
    )
    private String message;

    @Schema(
            description = "Error unique code",
            example = "1"
    )
    private ResponseErrorCodes code;

    @Schema(
            description = "HTTP status code",
            example = "404"
    )
    private Integer status;

    @Schema(hidden = true)
    public static ResponseErrorWithCode of(String errorMessage, HttpStatus httpStatus, ResponseErrorCodes code) {
        return new ResponseErrorWithCode(httpStatus.getReasonPhrase(), errorMessage, code, httpStatus.value());
    }
}
