package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.userPassword.UserPasswordPassword;

public record RecoverPasswordDto(
        String recoverCode,
        UserPasswordPassword newUserPassword
) {
}
