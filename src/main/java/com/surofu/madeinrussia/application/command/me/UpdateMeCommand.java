package com.surofu.madeinrussia.application.command.me;

import java.util.List;

public record UpdateMeCommand(
        String phoneNumber,
        String region,
        String inn,
        List<String> countries,
        List<String> categories
) {
}
