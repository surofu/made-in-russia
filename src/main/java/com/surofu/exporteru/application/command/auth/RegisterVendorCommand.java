package com.surofu.exporteru.application.command.auth;

import java.util.List;

public record RegisterVendorCommand(
        String email,
        String login,
        String password,
        String phoneNumber,
        String inn,
        String address,
        List<String> countries,
        List<String> productCategories,
        String avatarUrl
) {
}
