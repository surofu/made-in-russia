package com.surofu.exporteru.application.command.support;

import java.io.Serializable;

public record SendSupportMailCommand(
        String username,
        String email,
        String phoneNumber,
        String subject,
        String body
) implements Serializable {
}
