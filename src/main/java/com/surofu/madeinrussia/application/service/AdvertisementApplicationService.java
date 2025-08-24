package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.advertisement.AdvertisementDto;
import com.surofu.madeinrussia.application.dto.advertisement.AdvertisementWithTranslationsDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.enums.FileStorageFolders;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import com.surofu.madeinrussia.core.model.advertisement.AdvertisementImage;
import com.surofu.madeinrussia.core.repository.AdvertisementRepository;
import com.surofu.madeinrussia.core.repository.FileStorageRepository;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import com.surofu.madeinrussia.core.service.advertisement.AdvertisementService;
import com.surofu.madeinrussia.core.service.advertisement.operation.*;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementView;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementWithTranslationsView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvertisementApplicationService implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final TranslationRepository translationRepository;
    private final FileStorageRepository fileStorageRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public GetAllAdvertisements.Result getAllAdvertisements(GetAllAdvertisements operation) {
        List<AdvertisementView> viewList = advertisementRepository.getAllViewsByLang(operation.getLocale().getLanguage());
        List<AdvertisementDto> dtoList = viewList.stream().map(AdvertisementDto::of).toList();
        return GetAllAdvertisements.Result.success(dtoList);
    }

    @Override
    @Transactional(readOnly = true)
    public GetAllAdvertisementsWithTranslations.Result getAllAdvertisementsWithTranslations(GetAllAdvertisementsWithTranslations operation) {
        List<AdvertisementWithTranslationsView> viewList = advertisementRepository.getAllViewsWithTranslationsByLang(operation.getLocale().getLanguage());
        List<AdvertisementWithTranslationsDto> dtoList = viewList.stream().map(AdvertisementWithTranslationsDto::of).toList();
        return GetAllAdvertisementsWithTranslations.Result.success(dtoList);
    }

    @Override
    @Transactional(readOnly = true)
    public GetAdvertisementById.Result getAdvertisementById(GetAdvertisementById operation) {
        Optional<AdvertisementView> view = advertisementRepository.getViewByIdAndLang(
                operation.getId(), operation.getLocale().getLanguage());

        if (view.isEmpty()) {
            return GetAdvertisementById.Result.notFound(operation.getId());
        }

        AdvertisementDto dto = AdvertisementDto.of(view.get());
        return GetAdvertisementById.Result.success(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public GetAdvertisementWithTranslationsById.Result getAdvertisementWithTranslationsById(GetAdvertisementWithTranslationsById operation) {
        Optional<AdvertisementWithTranslationsView> view = advertisementRepository.getViewWithTranslationsByIdAndLang(
                operation.getId(), operation.getLocale().getLanguage());

        if (view.isEmpty()) {
            return GetAdvertisementWithTranslationsById.Result.notFound(operation.getId());
        }

        AdvertisementWithTranslationsDto dto = AdvertisementWithTranslationsDto.of(view.get());
        return GetAdvertisementWithTranslationsById.Result.success(dto);
    }

    @Override
    @Transactional
    public CreateAdvertisement.Result createAdvertisement(CreateAdvertisement operation) {
        entityManager.setFlushMode(FlushModeType.COMMIT);

        if (operation.getImage() == null || operation.getImage().isEmpty()) {
            return CreateAdvertisement.Result.emptyFile();
        }

        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(operation.getTitle());
        advertisement.setSubtitle(operation.getSubtitle());
        advertisement.setThirdText(operation.getThirdText());
        advertisement.setLink(operation.getLink());
        advertisement.setIsBig(operation.getIsBig());
        advertisement.setExpirationDate(operation.getExpirationDate());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        translationMap.put(TranslationKeys.TITLE.name(), operation.getTitle().getTranslations());
        translationMap.put(TranslationKeys.SUBTITLE.name(), operation.getSubtitle().getTranslations());

        if (operation.getThirdText().getTranslations() != null) {
            translationMap.put(TranslationKeys.THIRD_TEXT.name(), operation.getThirdText().getTranslations());
        }

        Map<String, HstoreTranslationDto> translationResultMap;

        try {
            translationResultMap = translationRepository.expand(translationMap);
        } catch (EmptyTranslationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateAdvertisement.Result.emptyTranslation(e);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateAdvertisement.Result.translationError(e);
        }

        String imageUrl;

        try {
            imageUrl = fileStorageRepository.uploadImageToFolder(operation.getImage(), "advertisement");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateAdvertisement.Result.savingFileError(e);
        }

        advertisement.getTitle().setTranslations(translationResultMap.get(TranslationKeys.TITLE.name()));
        advertisement.getSubtitle().setTranslations(translationResultMap.get(TranslationKeys.SUBTITLE.name()));
        advertisement.getThirdText().setTranslations(translationResultMap.get(TranslationKeys.THIRD_TEXT.name()));
        advertisement.setImage(AdvertisementImage.of(imageUrl));

        try {
            advertisementRepository.save(advertisement);
            return CreateAdvertisement.Result.success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateAdvertisement.Result.savingAdvertisementError(e);
        }
    }

    @Override
    @Transactional
    public UpdateAdvertisementById.Result updateAdvertisementById(UpdateAdvertisementById operation) {
        Optional<Advertisement> optionalAdvertisement = advertisementRepository.getById(operation.getAdvertisementId());

        if (optionalAdvertisement.isEmpty()) {
            return UpdateAdvertisementById.Result.notFound(operation.getAdvertisementId());
        }

        Advertisement advertisement = optionalAdvertisement.get();

        advertisement.setTitle(operation.getTitle());
        advertisement.setSubtitle(operation.getSubtitle());
        advertisement.setThirdText(operation.getThirdText());
        advertisement.setLink(operation.getLink());
        advertisement.setIsBig(operation.getIsBig());
        advertisement.setExpirationDate(operation.getExpirationDate());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        translationMap.put(TranslationKeys.TITLE.name(), operation.getTitle().getTranslations());
        translationMap.put(TranslationKeys.SUBTITLE.name(), operation.getSubtitle().getTranslations());

        if (operation.getThirdText().getTranslations() != null) {
            translationMap.put(TranslationKeys.THIRD_TEXT.name(), operation.getThirdText().getTranslations());
        }

        Map<String, HstoreTranslationDto> translationResultMap;

        try {
            translationResultMap = translationRepository.expand(translationMap);
        } catch (EmptyTranslationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateAdvertisementById.Result.emptyTranslation(e);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateAdvertisementById.Result.translationError(e);
        }

        if (operation.getImage() != null) {
            String oldImageUrl = advertisement.getImage().toString();
            String newImageUrl;

            try {
                newImageUrl = fileStorageRepository.uploadImageToFolder(operation.getImage(), FileStorageFolders.ADVERTISEMENT_IMAGES.getValue());
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateAdvertisementById.Result.savingFileError(e);
            }

            try {
                fileStorageRepository.deleteMediaByLink(oldImageUrl);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return UpdateAdvertisementById.Result.deletingFileError(e);
            }

            advertisement.setImage(AdvertisementImage.of(newImageUrl));
        }

        advertisement.getTitle().setTranslations(translationResultMap.get(TranslationKeys.TITLE.name()));
        advertisement.getSubtitle().setTranslations(translationResultMap.get(TranslationKeys.SUBTITLE.name()));
        advertisement.getThirdText().setTranslations(translationResultMap.get(TranslationKeys.THIRD_TEXT.name()));

        try {
            advertisementRepository.save(advertisement);
            return UpdateAdvertisementById.Result.success(operation.getAdvertisementId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateAdvertisementById.Result.savingAdvertisementError(e);
        }
    }

    @Override
    @Transactional
    public DeleteAdvertisementById.Result deleteAdvertisementById(DeleteAdvertisementById operation) {
        entityManager.setFlushMode(FlushModeType.COMMIT);

        Optional<Advertisement> advertisement = advertisementRepository.getById(operation.getAdvertisementId());

        if (advertisement.isEmpty()) {
            return DeleteAdvertisementById.Result.notFound(operation.getAdvertisementId());
        }

        try {
            fileStorageRepository.deleteMediaByLink(advertisement.get().getImage().toString());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteAdvertisementById.Result.deletingFileError(e);
        }

        try {
            advertisementRepository.delete(advertisement.get());
            return DeleteAdvertisementById.Result.success(operation.getAdvertisementId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteAdvertisementById.Result.deletingAdvertisementError(e);
        }
    }

    private enum TranslationKeys {
        TITLE, SUBTITLE, THIRD_TEXT
    }
}
