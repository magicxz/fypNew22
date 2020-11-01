package com.example.fyp

import android.app.AlertDialog
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.content.res.ComplexColorCompat.inflate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comment.view.*
import kotlinx.android.synthetic.main.load_post.*
import java.util.logging.Handler

class CommunityAdapter(val posts : MutableList<Post>): RecyclerView.Adapter<CommunityAdapter.MyViewHolder>() {

    lateinit var ref: DatabaseReference
    lateinit var query: Query
    lateinit var query1: Query
    lateinit var query2: Query
    lateinit var query3: Query

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

            layoutInflater.send.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(layoutInflater)
            dialog.show()

            val a = layoutInflater.findViewById<RecyclerView>(R.id.reComment)

            //a.isVisible = true
            //val intent = Intent(holder.comment.context, AddComment::class.java)
            //holder.comment.context.startActivity(intent)
        }
    }
}