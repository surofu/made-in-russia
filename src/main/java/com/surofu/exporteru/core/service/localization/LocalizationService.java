package com.surofu.exporteru.core.service.localization;

import com.surofu.exporteru.core.service.localization.service.DeleteLocalizationByLanguageCode;
import com.surofu.exporteru.core.service.localization.service.GetAllLocalizations;
import com.surofu.exporteru.core.service.localization.service.GetLocalizationByLanguageCode;
import com.surofu.exporteru.core.service.localization.service.SaveLocalizationByLanguageCode;

public interface LocalizationService {
    GetAllLocalizations.Result getAllLocalizations();

    GetLocalizationByLanguageCode.Result getLocalizationByLanguageCode(GetLocalizationByLanguageCode operation);

    SaveLocalizationByLanguageCode.Result saveLocalization(SaveLocalizationByLanguageCode operation);

    DeleteLocalizationByLanguageCode.Result deleteLocalization(DeleteLocalizationByLanguageCode operation);
}
