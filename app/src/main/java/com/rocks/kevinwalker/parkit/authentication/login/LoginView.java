package com.rocks.kevinwalker.parkit.authentication.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.rocks.kevinwalker.parkit.NavDrawer;
import com.rocks.kevinwalker.parkit.ParkItActivity;
import com.rocks.kevinwalker.parkit.ParkItPresenter;
import com.rocks.kevinwalker.parkit.R;
import com.rocks.kevinwalker.parkit.authentication.ForgotPassword;
import com.rocks.kevinwalker.parkit.authentication.Registration;
import com.rocks.kevinwalker.parkit.authentication.login.loginUtils.LoginConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginView extends ParkItActivity<ParkItPresenter> implements View.OnClickListener, LoginPresenter.View {

    private LoginPresenter loginPresenter;
    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.et_password) EditText et_password;
    @BindView(R.id.btn_register) Button btn_register;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.txt_forgot_password) TextView txt_forgot_password;
    @BindView(R.id.constraint_layout) ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setupTextChangeObservables();

        ButterKnife.bind(this);
        setupUi();

        loginPresenter = new LoginPresenter(this);
        loginPresenter.setupLocationHelper(LoginView.this);
        loginPresenter.setupFirebaseUser();
    }

    // TODO: Correct so that observable on Login button is working again after MVP conversion

//    private void setupTextChangeObservables() {
//        io.reactivex.Observable<CharSequence> loginObservable = RxTextView.textChanges(et_email);
//        loginObservable
//                .map(this::validateEmail)
//                .subscribe(isValidEmail -> btn_login.setEnabled(isValidEmail ? true : false));
//
//
//        io.reactivex.Observable<CharSequence> passwordObservable = RxTextView.textChanges(et_password);
//        passwordObservable
//                .map(this::validatePassword)
//                .subscribe(isValidPass -> btn_login.setEnabled(isValidPass ? true : false));
//
//
//
//        io.reactivex.Observable<Boolean> combinedObservables = io.reactivex.Observable.
//                combineLatest(loginObservable, passwordObservable, (o1, o2) -> validateEmail(o1)
//                        && validatePassword(o2));
//        combinedObservables.subscribe(isVisible -> btn_login.setEnabled(isVisible ? true : false));
//    }

    private void setupUi() {
        et_email.setOnClickListener(this);
        et_password.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_forgot_password.setOnClickListener(this);
        constraintLayout.setOnClickListener(this);

        btn_login.setEnabled(true);
        btn_register.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter = null;
    }

    @Override
    public void onClick(View view) {
        // We'll switch on the Resource identifier (of type int)
        // We can't switch on the type "View" directly because it is not an acceptable argument for switch()
        switch (view.getId()) {
            // Since we're not doing anything when these views are clicked, we have them here simply to show they can be accessed
            case R.id.et_phone_number:
                // Do nothing
                break; // ends the case
            case R.id.et_password:
                // Do nothing
                break;
            // Navigate to Homepage only if both et_email and et_password are non-blank (they contain text)
            case R.id.btn_login:
                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();

                loginPresenter.validateEmail(email, new LoginModel.EmailValidation() {
                    @Override
                    public void validEmail() {
                        loginPresenter.validatePassword(password, new LoginModel.PasswordValidation() {
                            @Override
                            public void validPassword() {
                                loginPresenter.signIn(email, password, new LoginModel.SignOn() {
                                    @Override
                                    public void signOnSuccess() {
                                        Toast.makeText(getApplicationContext(), "Authentication succeed.",
                                                Toast.LENGTH_SHORT).show();
                                        loginPresenter.startNavDrawerActivity();
                                    }

                                    @Override
                                    public void signOnFail(Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user.
                                        Log.w(LoginConstants.TAG_LOGIN, "signInWithEmail:failure",
                                                task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication " +
                                                        "failed.",
                                                Toast.LENGTH_SHORT).show();                                    }
                                });
                            }

                            @Override
                            public void inValidPassword() {

                            }
                        });
                    }

                    @Override
                    public void inValidEmail() {

                    }
                });
                hideSoftKeyBoard();
                break;
            case R.id.txt_forgot_password:
                // TODO: Look into whether people start Acitivty from View or Presenter class
                loginPresenter.startForgotPasswordActivity();
                break;
            case R.id.btn_register:
                loginPresenter.startRegistrationActivity();
                break;
            case R.id.constraint_layout:
                hideSoftKeyBoard();
                break;
        } // Closes switch(view.getId())
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void navigateToForgotPasswordActivity() {
        startActivity(new Intent(LoginView.this, ForgotPassword.class));
    }

    @Override
    public void navigateToRegisterActivity() {
        startActivity(new Intent(LoginView.this, Registration.class));
    }

    @Override
    public void navigateToNavDrawerActivity() {
        startActivity(new Intent(LoginView.this, NavDrawer.class));
    }
}
