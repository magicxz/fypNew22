package com.example.fyp

import android.app.AlertDialog
import android.content.Intent
import android.media.Image
import android.os.Bundle
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
import kotlinx.android.synthetic.main.load_post.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Handler

class CommunityAdapter(val posts : MutableList<Post>): RecyclerView.Adapter<CommunityAdapter.MyViewHolder>() {

    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var query: Query
    lateinit var query1: Query
    lateinit var query2: Query
    lateinit var query3: Query
    lateinit var commentList: MutableList<Comment>

    inner class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var userImg : CircleImageView = itemView.findViewById(R.id.userImg)
        var username : TextView = itemView.findViewById(R.id.name)
        var datetime : TextView = itemView.findViewById(R.id.datetime)
        var picture : ImageView = itemView.findViewById(R.id.picture)
        var content : TextView = itemView.findViewById(R.id.content)
        var love : ImageView = itemView.findViewById(R.id.love)
        var likeCount : TextView = itemView.findViewById(R.id.likeCount)
        var comment : TextView = itemView.findViewById(R.id.comment)
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
        val userId = posts[position].userId
        val picture = posts[position].postImage
        val postId = posts[position].postId

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
                            }
                            else{
                                holder.likeCount.text = "0 likes"
                            }
                        }
                    })

                    holder.love.setOnClickListener {
                        FirebaseDatabase.getInstance().getReference("Like").child(postId).child(currentUserID).removeValue()
                        holder.love.setImageResource(R.drawable.ic_002_heart)
                    }
                }else{
                    holder.love.setOnClickListener {
                        if(currentUserID == null){
                            Toast.makeText(holder.love.context,"Please Login First!!!",Toast.LENGTH_LONG).show()
                        }

                        ref = FirebaseDatabase.getInstance().getReference("Like")
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
                                    holder.likeCount.text = "0 likes"
                                }
                            }
                        })
                        ref.child(postId).child(currentUserID).setValue(true)
                        holder.love.setImageResource(R.drawable.ic_003_heart_clicked)
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
                    holder.likeCount.text = " Be the first to like this!"
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
                    val commentId =ref1.push().key

                    val mapComment = Comment(commentId!!,postId!!,currentUserID,commContent.text.toString(),getTime())

                    //val mapComment = HashMap<String,Any>()
                    //mapComment["commentId"] = commentId
                    //mapComment["postId"] = postId
                    //mapComment["userId"] = currentUserID
                    //mapComment["commentContent"] = commContent
                    //mapComment["datetime"] = getTime()

                    ref1.child(commentId).setValue(mapComment).addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            commContent.setText("")
                            Toast.makeText(reComment.context,"Comment Successful!!!",Toast.LENGTH_LONG).show()
                            //val intent = Intent(reComment.context, Home::class.java)
                            //reComment.context.startActivity(intent)
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
                        }

                        val mLayoutManager = LinearLayoutManager(reComment.context)
                        mLayoutManager.reverseLayout = true

                        reComment.layoutManager = mLayoutManager
                        reComment.scrollToPosition(commentList.size-1)
                        reComment.adapter = CommentAdapter(commentList)
                    }
                }
            })
        }
    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }
}