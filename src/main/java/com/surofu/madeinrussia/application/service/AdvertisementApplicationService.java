package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.advertisement.AdvertisementDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import com.surofu.madeinrussia.core.model.advertisement.AdvertisementImage;
import com.surofu.madeinrussia.core.repository.AdvertisementRepository;
import com.surofu.madeinrussia.core.repository.FileStorageRepository;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import com.surofu.madeinrussia.core.service.advertisement.AdvertisementService;
import com.surofu.madeinrussia.core.service.advertisement.operation.*;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementView;
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

    @Override
    public GetAllAdvertisements.Result getAllAdvertisements(GetAllAdvertisements operation) {
        List<AdvertisementView> viewList = advertisementRepository.getAllViewsByLang(operation.getLocale().getLanguage());
        List<AdvertisementDto> dtoList = viewList.stream().map(AdvertisementDto::of).toList();
        return GetAllAdvertisements.Result.success(dtoList);
    }

    @Override
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
    @Transactional
    public CreateAdvertisement.Result createAdvertisement(CreateAdvertisement operation) {
        if (operation.getImage() == null || operation.getImage().isEmpty()) {
            return CreateAdvertisement.Result.emptyFile();
        }

        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(operation.getTitle());
        advertisement.setSubtitle(operation.getSubtitle());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        translationMap.put(TranslationKeys.TITLE.name(), operation.getTitle().getTranslations());
        translationMap.put(TranslationKeys.SUBTITLE.name(), operation.getSubtitle().getTranslations());

        Map<String, HstoreTranslationDto> translationResultMap;

        try {
            translationResultMap = translationRepository.expend(translationMap);
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
    public UpdateAdvertisementById.Result updateAdvertisementById(UpdateAdvertisementById operation) {
        Optional<Advertisement> advertisement = advertisementRepository.getById(operation.getAdvertisementId());

        if (advertisement.isEmpty()) {
            return UpdateAdvertisementById.Result.notFound(operation.getAdvertisementId());
        }

        advertisement.get().setTitle(operation.getTitle());
        advertisement.get().setSubtitle(operation.getSubtitle());

        Map<String, HstoreTranslationDto> translationMap = new HashMap<>();
        translationMap.put(TranslationKeys.TITLE.name(), operation.getTitle().getTranslations());
        translationMap.put(TranslationKeys.SUBTITLE.name(), operation.getSubtitle().getTranslations());

        Map<String, HstoreTranslationDto> translationResultMap;

        try {
            translationResultMap = translationRepository.expend(translationMap);
        } catch (EmptyTranslationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateAdvertisementById.Result.emptyTranslation(e);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateAdvertisementById.Result.translationError(e);
        }

        String oldImageUrl = advertisement.get().getImage().toString();
        String newImageUrl;

        try {
            newImageUrl = fileStorageRepository.uploadImageToFolder(operation.getImage(), "advertisement");
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

        advertisement.get().getTitle().setTranslations(translationResultMap.get(TranslationKeys.TITLE.name()));
        advertisement.get().getSubtitle().setTranslations(translationResultMap.get(TranslationKeys.SUBTITLE.name()));
        advertisement.get().setImage(AdvertisementImage.of(newImageUrl));

        try {
            advertisementRepository.save(advertisement.get());
            return UpdateAdvertisementById.Result.success(operation.getAdvertisementId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateAdvertisementById.Result.savingAdvertisementError(e);
        }
    }

    @Override
    public DeleteAdvertisementById.Result deleteAdvertisementById(DeleteAdvertisementById operation) {
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
        TITLE, SUBTITLE
    }
}
