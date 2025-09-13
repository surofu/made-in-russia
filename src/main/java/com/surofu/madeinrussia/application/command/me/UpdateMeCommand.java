package com.surofu.madeinrussia.application.command.me;

import java.util.List;

public record UpdateMeCommand(
        String phoneNumber,
        String region,
        String inn,
        String address,
        String description,
        List<String> countries,
        List<String> categories,
        List<String> phoneNumbers,
        List<String> emails,
        List<String> sites
) {
}
