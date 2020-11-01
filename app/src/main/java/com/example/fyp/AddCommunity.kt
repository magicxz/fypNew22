package com.example.fyp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.add_community.*
import com.google.android.gms.tasks.Continuation
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AddCommunity : AppCompatActivity() {

    lateinit var usersRef: DatabaseReference
    lateinit var storageReference: StorageReference
    lateinit var imageUri: Uri
    private val RequestCode = 438
    lateinit var postlist : MutableList<Post>
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_community)

        usersRef = FirebaseDatabase.getInstance().getReference("Posts")
        storageReference = FirebaseStorage.getInstance().reference.child("Images")

        postlist = mutableListOf()

        getCount1()

        closePopup.setOnClickListener {
            startActivity(Intent(this,Home::class.java))
            this.finish()
        }

        uploadPic.setOnClickListener {
            selectPicture()
        }

        post.setOnClickListener {
            uploadPost()
        }
    }

    private fun selectPicture(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data!!
            Toast.makeText(this,"Uploading...", Toast.LENGTH_SHORT).show()
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Image is uploading, please wait...")
                progressDialog.show()
                uploadPic!!.setImageBitmap(bitmap)
                progressDialog.dismiss()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadPost(){
        val alertbox =AlertDialog.Builder(this)
        alertbox.setTitle("Error")
        alertbox.setIcon(R.mipmap.icon)

        val progressDialog = ProgressDialog(this)

        alertbox.setNegativeButton("Close"){dialog, which ->
            dialog.dismiss()
        }

        if(caption.text.toString().isEmpty()){
            alertbox.setMessage("Please enter your message...")
            alertbox.show()
        }else{
            val msg = caption.text.toString()
            val postId = usersRef.push().key.toString()

            if(imageUri != null){

                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                val countt = getCount1()
                val fileRef = storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if(!task.isSuccessful){
                        task.exception?.let{
                            progressDialog.dismiss()
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        var currentUser= FirebaseAuth.getInstance().currentUser!!.uid
                        //val storePost = Post()
                        val downloadUrl = task.result
                        val url = downloadUrl.toString()
                        val mapPost = HashMap<String,Any>()
                        mapPost["postId"] = postId
                        mapPost["datetime"] = getTime()
                        mapPost["content"] = msg
                        mapPost["userId"] = currentUser
                        mapPost["postImage"] = url

                        count = 0
                        usersRef.child(postId).setValue(mapPost)
                            .addOnCompleteListener { task ->
                                Toast.makeText(this,"Post Successful!!!",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,Home::class.java))
                            }
                    }
                }
            }else{
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    private fun getCount1(): Int {

        count = 0

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    postlist.clear()

                    for (h in p0.children) {
                        //CountOrder.number--
                        val post = h.getValue(Post::class.java)
                        postlist.add(post!!)
                    }

                    val adapter = CommunityAdapter(postlist)

                    CountOrder.total = adapter.itemCount
                }
            }
        })
        //Log.d("TotalCCC", "Total = " +  CountOrder.total)
        return CountOrder.total
    }
}