package com.surofu.exporteru.core.service.general;

import com.surofu.exporteru.core.service.general.operation.GetAllGeneral;

public interface GeneralService {
    GetAllGeneral.Result getAll(GetAllGeneral operation);
}
