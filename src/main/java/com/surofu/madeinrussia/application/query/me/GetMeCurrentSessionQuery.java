package com.surofu.madeinrussia.application.query.me;

import com.surofu.madeinrussia.application.security.SecurityUser;

public record GetMeCurrentSessionQuery(SecurityUser securityUser) {
}
