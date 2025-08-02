package com.surofu.madeinrussia.infrastructure.web.mapper.vendor;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.vendor.operation.ForceUpdateVendorById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForceUpdateVendorByIdMapperResultToResponseEntity
        implements ForceUpdateVendorById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(ForceUpdateVendorById.Result.Success result) {
        String message = localizationManager.localize("user.save.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(ForceUpdateVendorById.Result.NotFound result) {
        String message = localizationManager.localize("vendor.not_found_by_id", result.getId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processEmailAlreadyExists(ForceUpdateVendorById.Result.EmailAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_email_already_exists", result.getEmail());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processLoginAlreadyExists(ForceUpdateVendorById.Result.LoginAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_login_already_exists", result.getLogin());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processInnAlreadyExists(ForceUpdateVendorById.Result.InnAlreadyExists result) {
        String message = localizationManager.localize("auth.register.vendor_with_inn_already_exists", result.getInn());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processPhoneNumberAlreadyExists(ForceUpdateVendorById.Result.PhoneNumberAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_phone_already_exists", result.getPhoneNumber());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processEmptyVendorCountries(ForceUpdateVendorById.Result.EmptyVendorCountries result) {
        String message = localizationManager.localize("validation.vendor.country.name.empty");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processEmptyVendorProductCategories(ForceUpdateVendorById.Result.EmptyVendorProductCategories result) {
        String message = localizationManager.localize("validation.vendor.product_category.name.empty");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processTranslationError(ForceUpdateVendorById.Result.TranslationError result) {
        String message = localizationManager.localize("translation.translation_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSaveError(ForceUpdateVendorById.Result.SaveError result) {
        String message = localizationManager.localize("user.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
