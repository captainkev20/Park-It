package com.example.kevinwalker.parkit.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevinwalker.parkit.NavDrawer;
import com.example.kevinwalker.parkit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_LOGIN = "TAG_LOGIN";
    public static final String EXTRA_USER = "EXTRA_USER_UUID";
    public static final String EXTRA_USER_EMAIL = "EXTRA_USER_EMAIL";
    public static final String EXTRA_USER_FIRST_NAME = "EXTRA_USER_FIRST_NAME";


    private EditText et_email;
    private EditText et_password;
    private Button btn_register;

    private FirebaseAuth mAuth;

    // This is the entry point for our Activity's lifecycle - this always comes before "onStart()"
    // Set up any resources we can before the application becomes visible to the user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in redirect to NavDrawer if so.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startNavDrawerActivity();
        }

        setContentView(R.layout.activity_login);

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        TextView txt_forgot_password;
        TextView txt_register;
        Button btn_login;

        // Find our Views so the corresponding objects we've declared can be inflated
        et_email = findViewById(R.id.et_phone_number);
        et_password = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);

        // Attach "this" OnClickListener - the Activity's implementation of View.OnClickListener
        et_email.setOnClickListener(this);
        et_password.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_forgot_password.setOnClickListener(this);
        constraintLayout.setOnClickListener(this);

        btn_login.setEnabled(false);
        btn_register.setEnabled(true);

        io.reactivex.Observable<CharSequence> loginObservable = RxTextView.textChanges(et_email);
        loginObservable
                .map(this::validateEmail)
                .subscribe(isValidEmail -> btn_login.setEnabled(isValidEmail ? true : false));


        io.reactivex.Observable<CharSequence> passwordObservable = RxTextView.textChanges(et_password);
        passwordObservable
                .map(this::validatePassword)
                .subscribe(isValidPass -> btn_login.setEnabled(isValidPass ? true : false));



        io.reactivex.Observable<Boolean> combinedObservables = io.reactivex.Observable.combineLatest(loginObservable, passwordObservable, (o1, o2) -> validateEmail(o1) && validatePassword(o2));
        combinedObservables.subscribe(isVisible -> btn_login.setEnabled(isVisible ? true : false));
    }

    // Called after directly after onCreate() or after onRestart() (In the event that the Activity was stopped and is restarting)
    @Override
    protected void onStart() {
        // To @Override, we have to make a call to the parent's (AppCompatActivity) method "onStart()"
        super.onStart();

    }

    // Called immediately before the Activity becomes visible
    // Use this method to update views (if necessary) when resuming the Activity, for instance, after onStop() was called
    @Override
    protected void onResume() {
        super.onResume();

    }

    // Called immediately before the UI is hidden
    // Use this opportunity to save or store any data in the Activity
    @Override
    protected void onPause() {
        super.onPause();

    }

    // Called immediately after onPause() - the Activity is no longer visible
    // *** WARNING *** NOT GUARANTEED TO BE CALLED
    // Often used to stop processes that may have been spawned by the Activity
    @Override
    protected void onStop() {
        super.onStop();

    }

    // The last lifecycle method to be called - final chance to clean up resources
    // *** WARNING *** NOT GUARANTEED TO BE CALLED
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // Determine whether or not an EditText contains text
    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }

    private void startNavDrawerActivity() {
        Intent navDrawer = new Intent(this, NavDrawer.class);
        navDrawer.putExtra(EXTRA_USER, mAuth.getCurrentUser().getUid());
        navDrawer.putExtra(EXTRA_USER_EMAIL, mAuth.getCurrentUser().getEmail());
        startActivity(navDrawer);
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Authentication succeed.",
                                    Toast.LENGTH_SHORT).show();
                            startNavDrawerActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_LOGIN, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateEmail(CharSequence email) {

        if (Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
            return true;
        } else {
            return false;
        }

    }

    private boolean validatePassword(CharSequence password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^.{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password.toString());

        return matcher.matches();
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed(){

    }

    // Login's implementation of View.OnClickListener
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
                if (validateEmail(et_email.getText().toString())) {
                    if (validatePassword(et_password.getText().toString())) {
                        signIn(et_email.getText().toString(), et_password.getText().toString());
                    }
                }
                hideSoftKeyBoard();
                break;
            case R.id.txt_forgot_password:
                Intent forgotPassword = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(forgotPassword);
                break;
            case R.id.btn_register:
                startActivity(new Intent(getApplicationContext(), Registration.class));
                break;
            case R.id.constraint_layout:
                hideSoftKeyBoard();
                break;
        } // Closes switch(view.getId())
    } // Closes onClick(View view)
} // Closes Login