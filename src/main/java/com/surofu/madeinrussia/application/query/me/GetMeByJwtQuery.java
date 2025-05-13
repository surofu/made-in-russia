package com.surofu.madeinrussia.application.query.me;

import java.security.Principal;

public record GetMeByJwtQuery(Principal principal) {
}
