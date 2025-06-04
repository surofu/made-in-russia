package com.surofu.madeinrussia.core.service.auth;

import com.surofu.madeinrussia.core.service.auth.operation.*;

public interface AuthService {
    Register.Result register(Register operation);

    RegisterVendor.Result registerVendor(RegisterVendor operation);

    LoginWithEmail.Result loginWithEmail(LoginWithEmail operation);

    LoginWithLogin.Result loginWithLogin(LoginWithLogin operation);

    VerifyEmail.Result verifyEmail(VerifyEmail operation);

    Logout.Result logout(Logout operation);
}
