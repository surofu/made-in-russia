package com.surofu.madeinrussia.infrastructure.web.mapper.auth;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.auth.operation.RegisterVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterVendorMapperResultToResponseEntity
        implements RegisterVendor.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(RegisterVendor.Result.Success result) {
        String message = localizationManager.localize("auth.register.verification_code_sand_to_email", result.getUserEmail().toString());
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processUserWithEmailAlreadyExists(RegisterVendor.Result.UserWithEmailAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_email_already_exists", result.getUserEmail().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithLoginAlreadyExists(RegisterVendor.Result.UserWithLoginAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_login_already_exists", result.getUserLogin().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processUserWithPhoneNumberAlreadyExists(RegisterVendor.Result.UserWithPhoneNumberAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_phone_already_exists", result.getUserPhoneNumber().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processVendorWithInnAlreadyExists(RegisterVendor.Result.VendorWithInnAlreadyExists result) {
        String message = localizationManager.localize("auth.register.vendor_with_inn_already_exists", result.getInn().toString());
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.CONFLICT);
    }
}
