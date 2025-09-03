package com.surofu.madeinrussia.core.service.support;

import com.surofu.madeinrussia.core.service.support.operation.SendSupportMail;

public interface SupportService {
    SendSupportMail.Result sendSupportMail(SendSupportMail operation);
}
