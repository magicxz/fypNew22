package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.actionbar.*
import kotlinx.android.synthetic.main.login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        back.setOnClickListener{
            startActivity(Intent(this,Home::class.java))
        }

        val text = findViewById<TextView>(R.id.forgotPass)
        val content = SpannableString("Forgot Password?")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        text.setText(content)

        val text1 = findViewById<TextView>(R.id.register)
        val content1 = SpannableString("Register Now")
        content1.setSpan(UnderlineSpan(), 0, content1.length, 0)
        text1.setText(content1)

        forgotPass.setOnClickListener{
            startActivity(Intent(this,ForgotPassword::class.java))
        }

        register.setOnClickListener{
            startActivity(Intent(this,Register::class.java))
        }
    }
}