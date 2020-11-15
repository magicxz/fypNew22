package com.example.fyp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.actionbar.*
import kotlinx.android.synthetic.main.forgot_password.*

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        back.setOnClickListener{
            startActivity(Intent(this,Login::class.java))
        }

        btnSend.setOnClickListener{
            resetPassword()
        }
    }

    private fun resetPassword(){
        if(forgotEmail.text.toString().isEmpty()){
            forgotEmail.error="Please enter your email to reset password"
            forgotEmail.requestFocus()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(forgotEmail.text.toString()).matches()){
            forgotEmail.error="Please enter correct format eg.xx@xxxx.com"
            forgotEmail.requestFocus()
        }else {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Reset Password")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            val auth = FirebaseAuth.getInstance()
            auth.sendPasswordResetEmail(forgotEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Email Sent", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                        startActivity(Intent(this,Login::class.java))
                    }else{
                        Toast.makeText(this,"This Email is not exist", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                    }
                }
        }
    }
}