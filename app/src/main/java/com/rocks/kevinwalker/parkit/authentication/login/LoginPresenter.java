package com.rocks.kevinwalker.parkit.authentication.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rocks.kevinwalker.parkit.NavDrawer;
import com.rocks.kevinwalker.parkit.ParkItPresenter;
import com.rocks.kevinwalker.parkit.authentication.login.loginUtils.LoginConstants;
import com.rocks.kevinwalker.parkit.utils.LocationHelper;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPresenter extends ParkItPresenter {
    private FirebaseAuth mAuth;
    private LocationHelper locationHelper;
    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    protected void cleanUp() {

    }

    public void setupFirebaseUser() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if user is signed in redirect to NavDrawer if so.
        if (currentUser != null && loginView != null) {
            loginView.navigateToNavDrawerActivity();
        }
    }

    public void setupLocationHelper(Context context) {
        locationHelper = new LocationHelper(context);
        locationHelper.userFirstLoginLocationPermissionCheck();
    }

    public void signIn(String email, String password, LoginModel.SignOn signOn) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signOn.signOnSuccess();
                            loginView.navigateToNavDrawerActivity();
                        } else {
                            signOn.signOnFail(task);
                        }
                    }
                });
    }

    public void validateEmail(CharSequence email, LoginModel.EmailValidation fieldValidation) {

        if (Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
            fieldValidation.validEmail();
        } else {
            fieldValidation.inValidEmail();
        }
    }

    public void validatePassword(CharSequence password, LoginModel.PasswordValidation passwordValidation) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^.{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password.toString());

        if (matcher.matches()) {
            passwordValidation.validPassword();
        } else {
            passwordValidation.inValidPassword();
        }
    }

    public void startForgotPasswordActivity() {
        if (loginView != null) {
            loginView.navigateToForgotPasswordActivity();
        }
    }

    public void startRegistrationActivity() {
        if (loginView != null) {
            loginView.navigateToRegisterActivity();
        }
    }

    public void startNavDrawerActivity() {
        if (loginView != null) {
            loginView.navigateToNavDrawerActivity();
        }
    }

    public interface View {
        void navigateToForgotPasswordActivity();
        void navigateToRegisterActivity();
        void navigateToNavDrawerActivity();
    }
}
