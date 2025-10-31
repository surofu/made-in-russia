package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.me.operation.RefreshMeCurrentSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshMeCurrentSessionMapperResultToResponseEntity
        implements RefreshMeCurrentSession.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(RefreshMeCurrentSession.Result.Success result) {
        return new ResponseEntity<>(result.getTokenDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processInvalidRefreshToken(RefreshMeCurrentSession.Result.InvalidRefreshToken result) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processUserNotFound(RefreshMeCurrentSession.Result.UserNotFound result) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processSessionNotFound(RefreshMeCurrentSession.Result.SessionNotFound result) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processSaveSessionError(RefreshMeCurrentSession.Result.SaveSessionError result) {
        String message = localizationManager.localize("auth.register.save_error");
        SimpleResponseErrorDto responseErrorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
