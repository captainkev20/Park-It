package com.rocks.kevinwalker.parkit.authentication.login;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface LoginModel {
    interface LocationHelperHandler {
        void setupLocationHelper(Context context);
    }

    interface PasswordValidation {
        void validPassword();
        void inValidPassword();
    }

    interface EmailValidation {
        void validEmail();
        void inValidEmail();
    }

    interface SignOn {
        void signOnSuccess();
        void signOnFail(Task<AuthResult> task);
    }
}
