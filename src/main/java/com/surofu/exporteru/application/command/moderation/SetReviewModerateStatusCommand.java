package com.surofu.exporteru.application.command.moderation;

import com.surofu.exporteru.core.model.moderation.ApproveStatus;

public record SetReviewModerateStatusCommand(ApproveStatus status) {
}
