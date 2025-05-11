package com.surofu.madeinrussia.core.service.auth;

import com.surofu.madeinrussia.core.service.auth.operation.LoginWithEmail;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithLogin;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyEmail;

public interface AuthService {
    Register.Result register(Register operation);

    LoginWithEmail.Result loginWithEmail(LoginWithEmail operation);

    LoginWithLogin.Result loginWithLogin(LoginWithLogin operation);

    VerifyEmail.Result verifyEmail(VerifyEmail operation);
}
