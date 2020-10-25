package com.example.fyp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.isVisible
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.actionbar.*
import kotlinx.android.synthetic.main.actionbar.back
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.profile.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.header.*
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class Profile : AppCompatActivity() {

    var currentUser= FirebaseAuth.getInstance().currentUser!!.uid
    val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser)
    lateinit var ref: DatabaseReference
    lateinit var storageReference: StorageReference
    lateinit var imageUri: Uri
    private val RequestCode = 438
    var user = Users()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        storageReference = FirebaseStorage.getInstance().reference.child("Images")

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        back1.setOnClickListener {
            startActivity(Intent(this,Home::class.java))
        }

        displayInfo()

        addPic.setOnClickListener{
            selectPhoto()
        }

        done.setOnClickListener{
            updateProfile()
        }
    }

      private fun displayInfo(){
          //var currentUser= FirebaseAuth.getInstance().currentUser!!.uid

          val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser)

        val edit = findViewById<TextView>(R.id.edit)

        eEmail.isEnabled = false
        ePhone.isEnabled = false
        eGender.isEnabled = false

        usersRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    user =snapshot.getValue<Users>(Users::class.java)!!
                    profileName.setText(user.username)
                    eEmail.setText(user.email)
                    ePhone.setText(user.phone)
                    eGender.setText(user.gender)
                    eUsername.setText(user.username)
                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(profileImage)
                }
            }
        })

        edit.setOnClickListener{
            eEmail.isEnabled = true
            ePhone.isEnabled = true
            gen.isVisible = false
            eGender.isVisible = false
            edit.isVisible = false
            done.isVisible = true
            profileName.isVisible = false
            addPic.isVisible = true
            textName.isVisible = true
            eUsername.isVisible = true
            chgPass.isVisible = false
            textEmail.isVisible = false
            eEmail.isVisible = false
        }
    }

    private fun selectPhoto(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data!!
            Toast.makeText(this,"Uploading...",Toast.LENGTH_SHORT).show()
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Image is uploading, please wait...")
                progressDialog.show()
                profileImage!!.setImageBitmap(bitmap)
                progressDialog.dismiss()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateProfile(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updating Profile...")
        progressDialog.show()

        if(eUsername.text.toString().isEmpty()){
            eUsername.error="Username cannot be empty"
            eUsername.requestFocus()
            return
        }else if(ePhone.text.toString().isEmpty()){
            ePhone.error="Phone cannot be empty"
            ePhone.requestFocus()
            return
        }else if(user.username.equals(eUsername.text.toString()) && user.phone.equals(eEmail.text.toString())){
            this.finish()
        }else if(!(user.username.equals(eUsername.text.toString())) || !(user.phone.equals(eEmail.text.toString()))){
            user.username = eUsername.text.toString()
            user.phone = ePhone.text.toString()
            val curUser = FirebaseDatabase.getInstance().getReference("Users")
            curUser.child(currentUser).setValue(user)
            progressDialog.dismiss()
            Toast.makeText(this,"Update Successful!!!",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,Profile::class.java))
        }

        if(imageUri != null){
            val fileRef = storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{task ->
                if(!task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    val mapProfileImg = HashMap<String,Any>()
                    mapProfileImg["image"] = url
                    usersRef!!.updateChildren(mapProfileImg)
                    progressDialog.dismiss()
                    Toast.makeText(this,"Update Successful!!!",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,Profile::class.java))
                }
            }
        }
    }
}