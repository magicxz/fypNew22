package com.example.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.home.*

class order_detail : AppCompatActivity() {

    lateinit var ref :DatabaseReference
    lateinit var ref1 :DatabaseReference
    lateinit var ref2 :DatabaseReference
    lateinit var addressList:MutableList<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)


        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        ref = FirebaseDatabase.getInstance().getReference("Orders")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userId").getValue().toString().equals(currentUser)){
                            orderid.text = h.child("orderId").getValue().toString()
                            paymentMethod.text = h.child("paymentMethod").getValue().toString()
                            status.text = h.child("status").getValue().toString()
                            subtot.text = h.child("subtotal").getValue().toString()
                            delifee.text = h.child("deliveryfee").getValue().toString()
                            total.text = h.child("totalAmount").getValue().toString()
                        }
                    }
                }
            }
        })

        addressList = mutableListOf()

        ref2 = FirebaseDatabase.getInstance().getReference().child("Address")
        ref1 = FirebaseDatabase.getInstance().getReference().child("Users")

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
                            for (j in snapshot.children) {
                                if(j.child("userId").getValue().toString().equals(currentUser)){
                                    currentAddr.text = j.child("addressLine").getValue().toString()
                                }
                            }
                        }
                    }
                })
            }
        })
    }
}