package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.ForceRegisterVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForceRegisterVendorMapperResultToResponseEntity
        implements ForceRegisterVendor.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(ForceRegisterVendor.Result.Success result) {
        String message = localizationManager.localize("auth.register.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processVendorWithEmailAlreadyExists(ForceRegisterVendor.Result.VendorWithEmailAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_email_already_exists", result.getEmail().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processVendorWithLoginAlreadyExists(ForceRegisterVendor.Result.VendorWithLoginAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_login_already_exists", result.getLogin().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processVendorWithPhoneNumberAlreadyExists(ForceRegisterVendor.Result.VendorWithPhoneNumberAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_phone_already_exists", result.getPhoneNumber().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processVendorWithInnAlreadyExists(ForceRegisterVendor.Result.VendorWithInnAlreadyExists result) {
        String message = localizationManager.localize("auth.register.vendor_with_inn_already_exists", result.getInn().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processEmptyVendorCountries(ForceRegisterVendor.Result.EmptyVendorCountries result) {
        String message = localizationManager.localize("validation.vendor.country.name.empty");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processEmptyVendorProductCategories(ForceRegisterVendor.Result.EmptyVendorProductCategories result) {
        String message = localizationManager.localize("validation.vendor.product_category.name.empty");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processTranslationError(ForceRegisterVendor.Result.TranslationError result) {
        String message = localizationManager.localize("translation.translation_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSaveError(ForceRegisterVendor.Result.SaveError result) {
        String message = localizationManager.localize("auth.register.save_vendor_details_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
