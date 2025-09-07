package com.surofu.madeinrussia.core.service.moderation;

import com.surofu.madeinrussia.core.service.moderation.operation.SetProductApproveStatus;
import com.surofu.madeinrussia.core.service.moderation.operation.SetProductReviewApproveStatus;

public interface ModerationService {
    SetProductApproveStatus.Result setProductApproveStatus(SetProductApproveStatus operation);

    SetProductReviewApproveStatus.Result setProductReviewApproveStatus(SetProductReviewApproveStatus operation);
}
