package com.surofu.madeinrussia.core.service.auth;

import com.surofu.madeinrussia.core.service.auth.operation.*;

public interface AuthService {
    Register.Result register(Register operation);

    RegisterVendor.Result registerVendor(RegisterVendor operation);

    LoginWithEmail.Result loginWithEmail(LoginWithEmail operation);

    VerifyEmail.Result verifyEmail(VerifyEmail operation);

    Logout.Result logout(Logout operation);

    RecoverPassword.Result recoverPassword(RecoverPassword operation);

    VerifyRecoverPassword.Result verifyRecoverPassword(VerifyRecoverPassword operation);

    ForceRegister.Result forceRegister(ForceRegister operation);

    ForceRegisterVendor.Result forceRegisterVendor(ForceRegisterVendor operation);

    LoginWithTelegram.Result loginWithTelegram(LoginWithTelegram operation);
}
