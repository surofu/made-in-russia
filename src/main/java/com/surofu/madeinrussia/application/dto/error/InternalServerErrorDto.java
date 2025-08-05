package com.surofu.madeinrussia.application.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalServerErrorDto implements Serializable {
    private Integer status;
    private String error;
    private String message;
    private String errorClassName;
    private String errorMessage;
    private String stackTrace;
}
