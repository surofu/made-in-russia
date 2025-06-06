package com.surofu.madeinrussia.core.service.me;

import com.surofu.madeinrussia.core.service.me.operation.*;

public interface MeService {
    GetMe.Result getMeByJwt(GetMe operation);

    GetMeSessions.Result getMeSessions(GetMeSessions operation);

    GetMeCurrentSession.Result getMeCurrentSession(GetMeCurrentSession operation);

    GetMeReviewPage.Result getMeReviewPage(GetMeReviewPage operation);

    GetMeProductPage.Result getMeProductPage(GetMeProductPage operation);

    GetMeVendorProductReviewPage.Result getMeVendorProductReviewPage(GetMeVendorProductReviewPage operation);

    RefreshMeCurrentSession.Result refreshMeCurrentSession(RefreshMeCurrentSession operation);

    UpdateMe.Result updateMe(UpdateMe operation);
}
