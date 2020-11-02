package com.example.fyp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(val comments : MutableList<Comment>) : RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {

    lateinit var ref: DatabaseReference
    lateinit var query: Query

    inner class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var image : CircleImageView = itemView.findViewById(R.id.commUser)
        var name : TextView = itemView.findViewById(R.id.commName)
        var datetime : TextView = itemView.findViewById(R.id.commDate)
        var content : TextView = itemView.findViewById(R.id.commContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.load_comment,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        holder.datetime.text = comments[position].date
        holder.content.text = comments[position].commentContent
        val userId = comments[position].userId
        val postId = comments[position].postId

        query= FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(comments[position].userId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        val targetUser = h.getValue(Users::class.java)
                        val user = targetUser!!.username
                        val pic = targetUser!!.image

                        holder.name.text = user
                        Picasso.get().load(pic).into(holder.image)
                    }
                }
            }
        })
    }
}