package com.surofu.exporteru.core.service.moderation;

import com.surofu.exporteru.core.service.moderation.operation.SetProductApproveStatus;
import com.surofu.exporteru.core.service.moderation.operation.SetProductReviewApproveStatus;

public interface ModerationService {
    SetProductApproveStatus.Result setProductApproveStatus(SetProductApproveStatus operation);

    SetProductReviewApproveStatus.Result setProductReviewApproveStatus(SetProductReviewApproveStatus operation);
}
