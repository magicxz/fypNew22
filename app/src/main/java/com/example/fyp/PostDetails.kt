package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.android.synthetic.main.post_details.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PostDetails :AppCompatActivity() {

    lateinit var query : Query
    lateinit var query2 : Query
    lateinit var query3 : Query
    lateinit var query4 : Query
    var likeNotify = LikeNotification()
    lateinit var ref : DatabaseReference
    lateinit var ref2 : DatabaseReference
    lateinit var ref3 : DatabaseReference
    lateinit var ref4 : DatabaseReference
    lateinit var ref5 : DatabaseReference
    lateinit var ref6 : DatabaseReference
    lateinit var commentList: MutableList<Comment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_details)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        name1.text = intent.getStringExtra("Username")
        datetime1.text = intent.getStringExtra("DateTime")
        content1.text = intent.getStringExtra("Content")
        likeCount1.text = intent.getStringExtra("LikeCount")
        commentCount1.text = intent.getStringExtra("CommentCount")
        val userId = intent.getStringExtra("UserId")
        val postId = intent.getStringExtra("PostId")
        Picasso.get().load(intent.getStringExtra("ProfileImage")).into(userImg1)
        Picasso.get().load(intent.getStringExtra("PostPhoto")).into(picture1)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        query = FirebaseDatabase.getInstance().getReference("Like").child(postId!!).child(currentUserID)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    love1.setImageResource(R.drawable.ic_003_heart_clicked)
                    removeLove()
                    getCountLike()
                    getCommentCount()
                }else{
                    love1.setImageResource(R.drawable.ic_002_heart)
                    loveClick()
                    getCountLike()
                    getCommentCount()
                }
            }
        })

        back1.setOnClickListener {
            this.finish()
        }

        comment1.setOnClickListener {
            comment()
        }
    }

    private fun comment(){
        val layoutInflater = LayoutInflater.from(this).inflate(R.layout.comment,null,true)
        val dialog = BottomSheetDialog(this)
        val reComment = layoutInflater.findViewById<RecyclerView>(R.id.reComment)
        val send = layoutInflater.findViewById<ImageView>(R.id.send)
        val commContent = layoutInflater.findViewById<EditText>(R.id.writecomment)
        val userId = intent.getStringExtra("UserId")

        layoutInflater.send.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(layoutInflater)
        dialog.show()

        getCommentCount()

        send.setOnClickListener {
            //Toast.makeText(send.context,"Comment Fail",Toast.LENGTH_LONG).show()

            if (commContent.text.isEmpty()){
                Toast.makeText(commContent.context,"Comment cannot be empty!",Toast.LENGTH_LONG).show()
            }else{
                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
                val postId = intent.getStringExtra("PostId")

                ref4 = FirebaseDatabase.getInstance().getReference("Comment")
                ref5 = FirebaseDatabase.getInstance().getReference("Notification")
                ref6 = FirebaseDatabase.getInstance().getReference("CommentNotification")
                val commentId =ref4.push().key
                val notify = ref5.push().key
                val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID)

                usersRef.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            if(!(currentUserID.equals(userId))){
                                val user =snapshot.getValue(Users::class.java)!!
                                val message = user.username + " comment on your post"
                                val storeNotify = Notification(notify!!,getTime(),userId,currentUserID,message,"false",postId)

                                ref5.child(notify).setValue(storeNotify)
                                ref6.child(postId).child(currentUserID).setValue(notify!!)
                            }
                        }
                    }
                })

                val mapComment = Comment(commentId!!,postId!!,currentUserID,commContent.text.toString(),getTime())

                ref4.child(commentId).setValue(mapComment).addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        commContent.setText("")
                        Toast.makeText(reComment.context,"Comment Successful!!!",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(reComment.context,"Comment Fail",Toast.LENGTH_SHORT).show()
                    }
                }
                getCommentCount()
            }
        }
        val img = layoutInflater.findViewById<ImageView>(R.id.img)
        val nocomm = layoutInflater.findViewById<TextView>(R.id.nocomment)
        commentList = mutableListOf()
        val postId = intent.getStringExtra("PostId")
        query3 = FirebaseDatabase.getInstance().getReference("Comment").orderByChild("postId").equalTo(postId)

        query3.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    img.isVisible = false
                    nocomm.isVisible = false
                    commentList.clear()

                    for(h in snapshot.children){
                        val comment = h.getValue(Comment::class.java)
                        commentList.add(comment!!)
                        CountOrder.number = commentList.size
                        commentCount1.text = CountOrder.number.toString() + " comments"
                    }

                    val mLayoutManager = LinearLayoutManager(reComment.context)
                    mLayoutManager.reverseLayout = true

                    reComment.layoutManager = mLayoutManager
                    reComment.scrollToPosition(commentList.size-1)
                    reComment.adapter = CommentAdapter(commentList)
                }else{
                    commentCount1.text = "no comment..."
                }
            }
        })
    }

    private fun removeLove(){
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val postId = intent.getStringExtra("PostId")

        love1.setOnClickListener {
            query3 = FirebaseDatabase.getInstance().getReference("LikeNotification")
                .child(postId!!)
                .child(currentUserID)

            //get the fcking notifcation ID first
            query3.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        likeNotify.notificationID = p0.value.toString()

                        getKey.key=p0.value.toString()
                        Log.d("abcdddd", getKey.key)
                    }
                }
            })
            FirebaseDatabase.getInstance().getReference("Like").child(postId).child(currentUserID).removeValue()
            FirebaseDatabase.getInstance().getReference("Notification").child(getKey.key).removeValue()
            love1.setImageResource(R.drawable.ic_002_heart)
            getCountLike()
        }
    }

    private fun loveClick(){
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val postId = intent.getStringExtra("PostId")
        val userId = intent.getStringExtra("UserId")

        love1.setOnClickListener {
            if(currentUserID == null){
                Toast.makeText(this,"Please Login First!!!", Toast.LENGTH_LONG).show()
            }else{
                ref = FirebaseDatabase.getInstance().getReference("Like")
                ref2 = FirebaseDatabase.getInstance().getReference("Notification")
                ref3 = FirebaseDatabase.getInstance().getReference("LikeNotification")

                val notify = ref2.push().key
                var currentUser=FirebaseAuth.getInstance().currentUser!!.uid
                val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser)

                usersRef.addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            if(!(currentUserID.equals(userId))){
                                val user =snapshot.getValue(Users::class.java)!!
                                val message = user.username + " like your post"
                                val storeNotify = Notification(notify!!,getTime(),userId,currentUserID,message,"false",postId)

                                ref2.child(notify).setValue(storeNotify)
                                ref3.child(postId).child(currentUserID).setValue(notify!!)
                            }
                        }
                    }
                })

                CountOrder.total = 0

                ref.child(postId).child(currentUserID).setValue(true)
                love1.setImageResource(R.drawable.ic_003_heart_clicked)
                getCountLike()
            }
        }
    }

    private fun getCountLike(){
        val postId = intent.getStringExtra("PostId")
        query2 = FirebaseDatabase.getInstance().getReference("Like").child(postId!!)

        query2.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    CountOrder.total = (p0.childrenCount.toString()).toInt()
                    likeCount1.text = " " + CountOrder.total.toString() + " likes"
                }
                else{
                    likeCount1.text = " Be the first to like this"
                }
            }
        })
    }

    private fun getCommentCount(){
        val postId = intent.getStringExtra("PostId")
        query3 = FirebaseDatabase.getInstance().getReference("Comment").orderByChild("postId").equalTo(postId)

        query3.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    CountOrder.total = (p0.childrenCount.toString()).toInt()
                    commentCount1.text = CountOrder.total.toString() + " comments"
                }
                else{
                    commentCount1.text = "no comment..."
                }
            }
        })
    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }
}