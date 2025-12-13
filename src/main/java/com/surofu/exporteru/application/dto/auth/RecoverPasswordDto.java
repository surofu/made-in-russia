package com.surofu.exporteru.application.dto.auth;

import com.surofu.exporteru.core.model.user.password.UserPasswordPassword;

import java.io.Serializable;

public record RecoverPasswordDto(
        String recoverCode,
        UserPasswordPassword newUserPassword
) implements Serializable {
}
