package com.example.fyp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.actionbar.*
import kotlinx.android.synthetic.main.actionbar.back
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.profile.*
import kotlinx.android.synthetic.main.register.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        loadData()

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

        btnLogin.setOnClickListener{
            loginUser()
        }

        checkRmb.setOnCheckedChangeListener{ buttonView: CompoundButton?, isChecked: Boolean ->
            if(checkRmb.isChecked){
                val email = txtEmail.text.toString()
                val pass = txtPass.text.toString()
                val preference = getSharedPreferences("checkbox", Context.MODE_PRIVATE)
                val editor = preference.edit()
                editor.putString("EMAIL",email)
                editor.putString("PASSWORD",pass)
                editor.putBoolean("CHECKBOX",isChecked)
                editor.apply()
            }
        }
    }

    private fun loginUser(){
        val email=txtEmail.text.toString()
        val pass=txtPass.text.toString()
        val checked: Boolean = checkRmb.isChecked
        val preference = getSharedPreferences("checkbox", Context.MODE_PRIVATE)
        val editor = preference.edit()

        editor.putString("EMAIL",email)
        editor.putString("PASSWORD",pass)
        editor.putBoolean("CHECKBOX",checked)
        editor.apply()

        if(email.isEmpty()) {
            txtEmail.error = "Please enter your email"
            txtEmail.requestFocus()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString()).matches()) {
            txtEmail.error = "Please enter correct format eg.xx@xxxx.com"
            txtEmail.requestFocus()
        }else if(pass.isEmpty()) {
            txtPass.error = "Please enter your password"
            txtPass.requestFocus()
        }else {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Login")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

            mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        if (mAuth.currentUser!!.isEmailVerified) {
                            progressDialog.dismiss()
                            val intent = Intent(this, Home::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            //Toast.makeText(this, "Login successfull", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                baseContext,"Please verify your email address.",
                                Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                        }
                    } else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
        }
    }

    private fun loadData(){
        val preference = getSharedPreferences("checkbox", Context.MODE_PRIVATE)
        val savedBoolean = preference.getBoolean("CHECKBOX",false)
        val email = preference.getString("EMAIL","")
        txtEmail.setText(email)
        val pass = preference.getString("PASSWORD","")
        txtPass.setText(pass)
        checkRmb.isChecked = savedBoolean
    }

    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}