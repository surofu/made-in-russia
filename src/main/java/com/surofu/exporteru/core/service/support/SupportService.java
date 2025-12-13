package com.surofu.exporteru.core.service.support;

import com.surofu.exporteru.core.service.support.operation.SendSupportMail;

public interface SupportService {
    SendSupportMail.Result sendSupportMail(SendSupportMail operation);
}
