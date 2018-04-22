package com.example.kevinwalker.parkit.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kevinwalker.parkit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{

    EditText et_email;
    Button btn_send_password;
    private FirebaseAuth mAuth;
    private static final String TAG_FORGOT_PASSWORD = "Forgot Password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        et_email = findViewById(R.id.et_email);
        btn_send_password = findViewById(R.id.btn_send_password);

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
