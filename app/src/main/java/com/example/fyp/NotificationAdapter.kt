package com.example.fyp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(var notification : MutableList<Notification>) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>(){

    lateinit var query: Query

    inner class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var msg = itemView.findViewById<TextView>(R.id.message)
        var datetime = itemView.findViewById<TextView>(R.id.notitime)
        var notiback = itemView.findViewById<CardView>(R.id.notiBackground)
        var senderImg = itemView.findViewById<CircleImageView>(R.id.proImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.load_notification,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.msg.text = notification[position].type
        holder.datetime.text = notification[position].date
        val sender = notification[position].sender

        if(notification[position].status.equals("false")){
            holder.notiback.setCardBackgroundColor(Color.rgb(135,206,250))
        }

        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(sender)

        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists()){

                    for(h in p0.children){

                        val targetUser = h.getValue(Users::class.java)
                        //Toast.makeText(holder.content.context, "WTF is this " + user, Toast.LENGTH_SHORT).show()
                        val profilePhoto = targetUser!!.image

                        Picasso.get().load(profilePhoto).into(holder.senderImg)
                    }
                }
            }
        })
    }
}