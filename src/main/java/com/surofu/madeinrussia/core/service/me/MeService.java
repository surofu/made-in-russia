package com.surofu.madeinrussia.core.service.me;

import com.surofu.madeinrussia.core.service.me.operation.GetMe;
import com.surofu.madeinrussia.core.service.me.operation.GetMeCurrentSession;
import com.surofu.madeinrussia.core.service.me.operation.GetMeSessions;
import com.surofu.madeinrussia.core.service.me.operation.RefreshMeCurrentSession;

public interface MeService {
    GetMe.Result getMeByJwt(GetMe operation);

    GetMeSessions.Result getMeSessions(GetMeSessions operation);

    GetMeCurrentSession.Result getMeCurrentSession(GetMeCurrentSession operation);

    RefreshMeCurrentSession.Result refreshMeCurrentSession(RefreshMeCurrentSession operation);
}
