package com.surofu.exporteru.application.command.vendor;

import java.util.List;

public record ForceUpdateVendorCommand(
        String email,
        String login,
        String phoneNumber,
        String inn,
        String address,
        String description,
        List<String> countries,
        List<String> productCategories,
        List<String> phoneNumbers,
        List<String> emails,
        List<String> sites
) {
}
