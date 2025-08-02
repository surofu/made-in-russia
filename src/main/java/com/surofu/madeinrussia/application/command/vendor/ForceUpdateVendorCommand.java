package com.surofu.madeinrussia.application.command.vendor;

import java.util.List;

public record ForceUpdateVendorCommand(
        String email,
        String login,
        String phoneNumber,
        String inn,
        List<String> countries,
        List<String> productCategories
) {
}
