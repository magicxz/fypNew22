package com.example.fyp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.notification.*
import java.util.*

class LoadNotification : AppCompatActivity() {

    lateinit var notificationList : MutableList<Notification>
    lateinit var ref : DatabaseReference
    lateinit var query : Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification)

        notificationList = mutableListOf()

        var currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        ref = FirebaseDatabase.getInstance().getReference("Notification")
        query = ref.orderByChild("receiver").equalTo(currentUser)

        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    notificationList.clear()

                    for(h in p0.children){
                        val notification = h.getValue<Notification>(Notification::class.java)
                        notificationList.add(notification!!)
                    }

                    val adapter = NotificationAdapter(notificationList)

                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    mLayoutManager.reverseLayout = true

                    reNotification.layoutManager = mLayoutManager
                    reNotification.scrollToPosition(notificationList.size-1)
                    reNotification.adapter = adapter

                }
            }
        })

        back.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }
    }
}