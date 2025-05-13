package com.surofu.madeinrussia.core.service.me;

import com.surofu.madeinrussia.core.service.me.operation.GetMeByJwt;
import com.surofu.madeinrussia.core.service.me.operation.UpdateMeAccessToken;

public interface MeService {
    GetMeByJwt.Result getMeByJwt(GetMeByJwt operation);
    UpdateMeAccessToken.Result updateMeAccessToken(UpdateMeAccessToken operation);
}
