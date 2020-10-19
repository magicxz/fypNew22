package com.example.fyp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.register.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.actionbar.*
import java.util.regex.Pattern

class Register : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""
    private var gender : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        mAuth = FirebaseAuth.getInstance()

        back.setOnClickListener{
            startActivity(Intent(this,Login::class.java))
        }

        btnRegister.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser() {
        val username: String = rName.text.toString()
        val email: String = rEmail.text.toString()
        val phone: String = rPhone.text.toString()
        val pass: String = rPass.text.toString()
        val cfmPass: String = rCfmPass.text.toString()
        val male = findViewById<RadioButton>(R.id.male)
        val female = findViewById<RadioButton>(R.id.female)
        val number = Regex("(?=.*[0-9])")
        val lower = Regex("(?=.*[a-z])")
        val upper = Regex( "(?=.*[A-Z])")
        val special = Regex("(?=.*[@#$%^&+=])")
        val passPatt = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\\\S+\$).{4,}\$")

        if (male.isChecked) {
            gender = "Male"
        } else if (female.isChecked) {
            gender = "Female"
        }

        if(username.isEmpty()){
            rName.error = "Please enter your name"
            rName.requestFocus()
        }else if(email.isEmpty()){
            rEmail.error = "Please enter your email"
            rEmail.requestFocus()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(rEmail.text.toString()).matches()){
            rEmail.error = "Please enter correct format eg.xx@xxxx.com"
            rEmail.requestFocus()
        }else if(phone.isEmpty()){
            rPhone.error = "Please enter your phone number"
            rPhone.requestFocus()
        }else if(phone.length != 10){
            rPhone.error = "Phone number only 10 digit"
            rPhone.requestFocus()
        }else if(pass.isEmpty()){
            rPass.error = "Please enter your password"
            rPass.requestFocus()
        }else if(pass.length < 6) {
            rPass.error = "Please enter password minimum in 6 character"
            rPass.requestFocus()
        }else if(cfmPass.isEmpty()){
            rCfmPass.error = "Please enter your password again"
            rCfmPass.requestFocus()
        }else if(!pass.equals(cfmPass)){
            rCfmPass.error = "Both password are not matched!"
            rCfmPass.requestFocus()
        }else{
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("SignUp")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            mAuth.createUserWithEmailAndPassword(email,cfmPass)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users")

                        val userHashMap = HashMap<String,Any>()
                        userHashMap["uid"] = currentUserID
                        userHashMap["username"] = username
                        userHashMap["email"] = email
                        userHashMap["phone"] = phone
                        userHashMap["gender"] = gender
                        userHashMap["image"] = "https://firebasestorage.googleapis.com/v0/b/final-year-project-9cdd9.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=c115d584-f003-4d33-818d-754f5ce290e6"

                        refUsers.child(currentUserID).setValue(userHashMap)
                            .addOnCompleteListener{task ->
                                if(task.isSuccessful){
                                    progressDialog.dismiss()
                                    Toast.makeText(this,"Account has been created successfully",Toast.LENGTH_LONG)
                                    val user=FirebaseAuth.getInstance().currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                mAuth.signOut()

                                                val intent = Intent(this, Login::class.java)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intent)
                                                Toast.makeText(
                                                    this,
                                                    "Account created",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                finish()
                                            }
                                        }
                                }
                            }
                    }
                    else{
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressDialog.dismiss()
                    }
                }
        }
    }
}