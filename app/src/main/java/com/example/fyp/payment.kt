package com.example.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.home.*

class payment : AppCompatActivity() {

    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var addressList: MutableList<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)


        var currentUser= FirebaseAuth.getInstance().currentUser!!.uid

        ref2 = FirebaseDatabase.getInstance().getReference().child("Address")
        ref1 = FirebaseDatabase.getInstance().getReference().child("Users")

        addressList= mutableListOf()

        ref1.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    addressList.clear()
                    for (h in snapshot.children) {
                        val u = h.getValue(Address::class.java)
                        addressList.add(u!!)
                    }
                }
                ref2.addValueEventListener(object:ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            addressList.clear()
                            //userList.clear()
                            for (j in snapshot.children) {
                                if(j.child("userId").getValue().toString().equals(currentUser)){
                                    textView11.text = j.child("addressLine").getValue().toString()
                                }
                            }
                        }
                    }
                })
            }
        })


        placeOrder.setOnClickListener{

            val ref = FirebaseDatabase.getInstance().getReference("Orders")

            val orderId = ref.push().key


        }


    }
}