package com.surofu.madeinrussia.core.service.moderation;

import com.surofu.madeinrussia.core.service.moderation.operation.SetProductReviewApproveStatus;

public interface ModerationService {
    SetProductReviewApproveStatus.Result setProductReviewApproveStatus(SetProductReviewApproveStatus operation);
}
