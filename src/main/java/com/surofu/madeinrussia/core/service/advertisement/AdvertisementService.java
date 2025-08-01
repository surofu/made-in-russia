package com.surofu.madeinrussia.core.service.advertisement;

import com.surofu.madeinrussia.core.service.advertisement.operation.*;

public interface AdvertisementService {
    GetAllAdvertisements.Result getAllAdvertisements(GetAllAdvertisements operation);

    GetAllAdvertisementsWithTranslations.Result getAllAdvertisementsWithTranslations(GetAllAdvertisementsWithTranslations operation);

    GetAdvertisementById.Result getAdvertisementById(GetAdvertisementById operation);

    GetAdvertisementWithTranslationsById.Result getAdvertisementWithTranslationsById(GetAdvertisementWithTranslationsById operation);

    CreateAdvertisement.Result createAdvertisement(CreateAdvertisement operation);

    UpdateAdvertisementById.Result updateAdvertisementById(UpdateAdvertisementById operation);

    DeleteAdvertisementById.Result deleteAdvertisementById(DeleteAdvertisementById operation);
}
