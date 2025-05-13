package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.core.service.me.operation.RefreshMeCurrentSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshMeCurrentSessionMapperResultToResponseEntity implements RefreshMeCurrentSession.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(RefreshMeCurrentSession.Result.Success result) {
        return new ResponseEntity<>(result.getTokenDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processInvalidRefreshToken(RefreshMeCurrentSession.Result.InvalidRefreshToken result) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> processUserNotFound(RefreshMeCurrentSession.Result.UserNotFound result) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
