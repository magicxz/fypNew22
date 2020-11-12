package com.example.fyp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.change_password.*

class ChangePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        back2.setOnClickListener {
            startActivity(Intent(this,Profile::class.java))
            this.finish()
        }

        confirm.setOnClickListener{
            changePassword()
        }

    }

    private fun changePassword() {
        var auth= FirebaseAuth.getInstance()
        val dialogBuilder = AlertDialog.Builder(this)

        if(oldPass.text.toString().isEmpty()){
            oldPass.error="Please enter current password"
            oldPass.requestFocus()
        }else if(newPass.text.toString().isEmpty()){
            newPass.error="Please enter new password"
            newPass.requestFocus()
        }else if(cfmnewPass.length()<6){
            cfmnewPass.error="Password at least 6 characters"
            cfmnewPass.requestFocus()
        }else if(cfmnewPass.text.toString().isEmpty()){
            cfmnewPass.error="Please enter confirm password"
            cfmnewPass.requestFocus()
        }else{
            if(newPass.text.toString().equals(cfmnewPass.text.toString())){
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Changing Password")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.show()
                val user=auth.currentUser
                val credential = EmailAuthProvider
                    .getCredential(user!!.email!!, oldPass.text.toString())
                //Log.d("changePW","${user.email}")
                user?.reauthenticate(credential)
                    ?.addOnCompleteListener {
                        if(it.isSuccessful){
                            user?.updatePassword(newPass.text.toString())
                                ?.addOnCompleteListener{task ->
                                    if(task.isSuccessful){
                                        progressDialog.dismiss()
                                        dialogBuilder.setView(R.layout.okalertbox)
                                        val dialog = dialogBuilder.create()
                                        dialog.setTitle("Change Password")
                                        dialog.show()

                                        val ok = dialog.findViewById<Button>(R.id.ok)
                                        ok.setOnClickListener {
                                            dialog.dismiss()
                                            startActivity(Intent(this,Profile::class.java))
                                            this.finish()
                                        }
                                        //Toast.makeText(this,"Password Changed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }else{
                            progressDialog.dismiss()
                            oldPass.error="Wrong current password"
                            //oldPass.requestFocus()
                        }
                    }
            }else{
                cfmnewPass.error="Password mismatching"
                //cfmnewPass.requestFocus()
            }
        }
    }
}