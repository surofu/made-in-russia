package com.surofu.madeinrussia.core.service.general;

import com.surofu.madeinrussia.core.service.general.operation.GetAllGeneral;

public interface GeneralService {
    GetAllGeneral.Result getAll(GetAllGeneral operation);
}
