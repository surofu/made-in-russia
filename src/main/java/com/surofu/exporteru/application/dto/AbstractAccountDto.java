package com.surofu.exporteru.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Account")
public abstract class AbstractAccountDto implements Serializable {
    private Long id;
    private Boolean isEnabled;
    private String role;
    private String email;
    private String login;
    private String phoneNumber;
    private String avatarUrl;
    private ZonedDateTime registrationDate;
    private ZonedDateTime lastModificationDate;
}
