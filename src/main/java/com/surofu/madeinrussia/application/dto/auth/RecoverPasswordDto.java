package com.surofu.madeinrussia.application.dto.auth;

import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;

import java.io.Serializable;

public record RecoverPasswordDto(
        String recoverCode,
        UserPasswordPassword newUserPassword
) implements Serializable {
}
