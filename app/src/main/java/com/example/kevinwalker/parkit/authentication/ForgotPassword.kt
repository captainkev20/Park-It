package com.example.kevinwalker.parkit.authentication
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.kevinwalker.parkit.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity (), View.OnClickListener  {

    private val TAG_FORGOT_PASSWORD = "Forgot Password"

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()

        btn_send_password.setOnClickListener{ view ->
            if(!et_registration_email.text.toString().isEmpty()) {
                forgotPassword(et_registration_email.text.toString())
            } else {
                Toast.makeText(this, "You must enter an e-mail address to retrieve your password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(p0: View?) {
        if(!et_registration_email.text.toString().isEmpty()) {
            forgotPassword(et_registration_email.text.toString())
        } else {
            Toast.makeText(this, "You must enter an e-mail address to retrieve your password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun forgotPassword(forgotEmail: String) {
        mAuth.sendPasswordResetEmail(forgotEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG_FORGOT_PASSWORD, "Email Sent!")
                Toast.makeText(this, "Password Sent!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sorry, that e-mail is not valid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}