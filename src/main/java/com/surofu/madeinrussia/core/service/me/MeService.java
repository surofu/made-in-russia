package com.surofu.madeinrussia.core.service.me;

import com.surofu.madeinrussia.core.service.me.operation.*;

public interface MeService {
    GetMe.Result getMeByJwt(GetMe operation);

    GetMeSessions.Result getMeSessions(GetMeSessions operation);

    GetMeCurrentSession.Result getMeCurrentSession(GetMeCurrentSession operation);

    GetMeReviewPage.Result getMeReviewPage(GetMeReviewPage operation);

    GetMeProductSummaryViewPage.Result getMeProductSummaryViewPage(GetMeProductSummaryViewPage operation);

    GetMeVendorProductReviewPage.Result getMeVendorProductReviewPage(GetMeVendorProductReviewPage operation);

    RefreshMeCurrentSession.Result refreshMeCurrentSession(RefreshMeCurrentSession operation);

    UpdateMe.Result updateMe(UpdateMe operation);

    DeleteMeSessionById.Result deleteMeSessionById(DeleteMeSessionById operation);

    SaveMeAvatar.Result saveMeAvatar(SaveMeAvatar operation);

    DeleteMeAvatar.Result deleteMeAvatar(DeleteMeAvatar operation);

    DeleteMe.Result deleteMe(DeleteMe operation);

    VerifyDeleteMe.Result verifyDeleteMe(VerifyDeleteMe operation);

    UploadMeVendorMedia.Result uploadMeVendorMedia(UploadMeVendorMedia operation);

    DeleteMeVendorMediaById.Result deleteMeVendorMediaById(DeleteMeVendorMediaById operation);

    DeleteMeVendorMediaByIdList.Result deleteMeVendorMediaByIdList(DeleteMeVendorMediaByIdList operation);
}
