package com.surofu.madeinrussia.application.command.moderation;

import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;

public record SetReviewModerateStatusCommand(ApproveStatus status) {
}
