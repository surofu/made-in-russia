package com.surofu.madeinrussia.application.query.me;

import com.surofu.madeinrussia.application.security.SecurityUser;

public record GetMeSessionsQuery(
        SecurityUser securityUser,
        String userAgent,
        String ipAddress
) {
}
