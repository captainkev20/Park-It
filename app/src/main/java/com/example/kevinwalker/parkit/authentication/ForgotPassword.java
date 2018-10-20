package com.example.kevinwalker.parkit.authentication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.kevinwalker.parkit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG_FORGOT_PASSWORD = "Forgot Password";

    @BindView(R.id.et_registration_email) EditText et_email;
    @BindView(R.id.btn_send_password) Button btn_send_password;
    @BindView(R.id.txt_enter_email) TextView txt_enter_email;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        btn_send_password.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (!et_email.getText().toString().isEmpty()) {
            forgotPassword(et_email.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "You must enter an email to retrieve your password", Toast.LENGTH_SHORT).show();
        }
    }

    private void forgotPassword (String forgot_email) {
        mAuth.sendPasswordResetEmail(forgot_email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG_FORGOT_PASSWORD, "Email sent");
                    Toast.makeText(ForgotPassword.this, "Password Sent",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, that e-mail is not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
