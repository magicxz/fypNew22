package com.example.fyp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.content.res.ComplexColorCompat.inflate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.android.synthetic.main.fragment_community.view.*
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.load_post.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Handler

class CommunityAdapter(val posts : MutableList<Post>): RecyclerView.Adapter<CommunityAdapter.MyViewHolder>() {

    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var ref4: DatabaseReference
    lateinit var query: Query
    lateinit var query1: Query
    lateinit var query2: Query
    lateinit var query3: Query
    lateinit var query4: Query
    lateinit var query5: Query
    lateinit var commentList: MutableList<Comment>
    lateinit var postList: MutableList<Post>
    var likeNotify = LikeNotification()
    var commNotify = CommentNotification()

    inner class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var userImg : CircleImageView = itemView.findViewById(R.id.userImg)
        var username : TextView = itemView.findViewById(R.id.name)
        var datetime : TextView = itemView.findViewById(R.id.datetime)
        var picture : ImageView = itemView.findViewById(R.id.picture)
        var content : TextView = itemView.findViewById(R.id.content)
        var love : ImageView = itemView.findViewById(R.id.love)
        var likeCount : TextView = itemView.findViewById(R.id.likeCount)
        var comment : TextView = itemView.findViewById(R.id.comment)
        var commentCount : TextView = itemView.findViewById(R.id.commentCount)
        var postDetail : RelativeLayout = itemView.findViewById(R.id.postdetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.load_post,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.datetime.text = posts[position].datetime
        holder.content.text = posts[position].content
        var userId = posts[position].userId
        val picture = posts[position].postImage
        var postId = posts[position].postId

        val intent = Intent(holder.postDetail.context, PostDetails::class.java)

        query= FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(userId)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        val targetUser = h.getValue(Users::class.java)
                        val user = targetUser!!.username
                        val pic = targetUser!!.image

                        holder.username.text = user
                        Picasso.get().load(pic).into(holder.userImg)

                        intent.putExtra("Username",user)
                        intent.putExtra("ProfileImage",pic)
                    }
                }
            }
        })
        Picasso.get().load(picture).into(holder.picture)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        query1 = FirebaseDatabase.getInstance().getReference("Like").child(postId!!).child(currentUserID)

        query1.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    holder.love.setImageResource(R.drawable.ic_003_heart_clicked)

                    query2 = FirebaseDatabase.getInstance().getReference("Like").child(postId!!)

                    query2.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                CountOrder.total = (p0.childrenCount.toString()).toInt()
                                holder.likeCount.text = " " + CountOrder.total.toString() + " likes"
                                intent.putExtra("LikeCount"," " + CountOrder.total.toString() + " likes")
                            }
                            else{
                                holder.likeCount.text = "Be the first to like this"
                            }
                        }
                    })

                    holder.love.setOnClickListener {
                        query4 = FirebaseDatabase.getInstance().getReference("LikeNotification")
                            .child(postId!!)
                            .child(currentUserID)

                        //get the fcking notifcation ID first
                        query4.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.exists()){

                                    likeNotify.notificationID = p0.value.toString()
                                    //likeNotificationList.add(likeNot!!)

                                    getKey.key=p0.value.toString()
                                    Log.d("abcdddd", getKey.key)

                                }
                            }
                        })
                        FirebaseDatabase.getInstance().getReference("Like").child(postId).child(currentUserID).removeValue()
                        FirebaseDatabase.getInstance().getReference("Notification").child(getKey.key).removeValue()
                        holder.love.setImageResource(R.drawable.ic_002_heart)
                    }
                }else{
                    holder.love.setOnClickListener {
                        if(currentUserID == null){
                            Toast.makeText(holder.love.context,"Please Login First!!!",Toast.LENGTH_LONG).show()
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

                            query2 = FirebaseDatabase.getInstance().getReference("Like").child(postId!!)

                            query2.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if (p0.exists()) {
                                        CountOrder.total = (p0.childrenCount.toString()).toInt()
                                        holder.likeCount.text = " " + CountOrder.total.toString() + " likes"
                                    }
                                    else{
                                        holder.likeCount.text = "Be the first to like this"
                                    }
                                }
                            })
                            ref.child(postId).child(currentUserID).setValue(true)
                            holder.love.setImageResource(R.drawable.ic_003_heart_clicked)
                        }
                    }
                }
            }
        })

        query2 = FirebaseDatabase.getInstance().getReference("Like").child(postId!!)

        query2.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    CountOrder.total = (p0.childrenCount.toString()).toInt()
                    holder.likeCount.text = " " + CountOrder.total.toString() + " likes"
                }
                else{
                    holder.likeCount.text = " Be the first to like this"
                }
            }
        })

        query3 = FirebaseDatabase.getInstance().getReference("Comment").orderByChild("postId").equalTo(postId)

        query3.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    CountOrder.total = (p0.childrenCount.toString()).toInt()
                    holder.commentCount.text = CountOrder.total.toString() + " comments"
                    intent.putExtra("CommentCount"," " + CountOrder.total.toString() + " comments")
                }
                else{
                    holder.commentCount.text = "no comment..."
                }
            }
        })

        holder.comment.setOnClickListener {
            val layoutInflater = LayoutInflater.from(holder.comment.context).inflate(R.layout.comment,null,true)
            val dialog = BottomSheetDialog(holder.comment.context)
            val reComment = layoutInflater.findViewById<RecyclerView>(R.id.reComment)

            layoutInflater.send.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(layoutInflater)
            dialog.show()

            val send = layoutInflater.findViewById<ImageView>(R.id.send)
            val commContent = layoutInflater.findViewById<EditText>(R.id.writecomment)

            send.setOnClickListener {
                //Toast.makeText(send.context,"Comment Fail",Toast.LENGTH_LONG).show()

                if (commContent.text.isEmpty()){
                    Toast.makeText(commContent.context,"Comment cannot be empty!",Toast.LENGTH_LONG).show()
                }else{
                    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
                    val postId = posts[position].postId

                    ref1 = FirebaseDatabase.getInstance().getReference("Comment")
                    ref2 = FirebaseDatabase.getInstance().getReference("Notification")
                    ref4 = FirebaseDatabase.getInstance().getReference("CommentNotification")
                    val commentId =ref1.push().key
                    val notify = ref2.push().key
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

                                    ref2.child(notify).setValue(storeNotify)
                                    ref4.child(postId).child(currentUserID).setValue(notify!!)
                                }
                            }
                        }
                    })

                    val mapComment = Comment(commentId!!,postId!!,currentUserID,commContent.text.toString(),getTime())

                    ref1.child(commentId).setValue(mapComment).addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            commContent.setText("")
                            Toast.makeText(reComment.context,"Comment Successful!!!",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(reComment.context,"Comment Fail",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            val img = layoutInflater.findViewById<ImageView>(R.id.img)
            val nocomm = layoutInflater.findViewById<TextView>(R.id.nocomment)
            commentList = mutableListOf()
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
                            holder.commentCount.text = CountOrder.number.toString() + " comments"
                        }

                        val mLayoutManager = LinearLayoutManager(reComment.context)
                        mLayoutManager.reverseLayout = true

                        reComment.layoutManager = mLayoutManager
                        reComment.scrollToPosition(commentList.size-1)
                        reComment.adapter = CommentAdapter(commentList)
                    }else{
                        holder.commentCount.text = "no comment..."
                    }
                }
            })
        }
        holder.postDetail.setOnClickListener{
            intent.putExtra("DateTime",posts[position].datetime)
            intent.putExtra("PostPhoto",posts[position].postImage)
            intent.putExtra("Content",posts[position].content)
            intent.putExtra("UserId",posts[position].userId)
            intent.putExtra("PostId",posts[position].postId)

            holder.postDetail.context.startActivity(intent)
        }
    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }


}