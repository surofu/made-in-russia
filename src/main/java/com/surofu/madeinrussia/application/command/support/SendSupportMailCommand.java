package com.surofu.madeinrussia.application.command.support;

import java.io.Serializable;

public record SendSupportMailCommand(
        String username,
        String email,
        String subject,
        String body
) implements Serializable {
}
