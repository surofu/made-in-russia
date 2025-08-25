package com.surofu.madeinrussia.application.command.vendor;

import java.util.List;

public record ForceUpdateVendorCommand(
        String email,
        String login,
        String phoneNumber,
        String inn,
        String description,
        String site,
        List<String> countries,
        List<String> productCategories,
        List<String> phoneNumbers,
        List<String> emails
) {
}
