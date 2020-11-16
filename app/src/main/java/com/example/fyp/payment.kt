package com.example.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.home.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class payment : AppCompatActivity() {

    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var addressList: MutableList<Address>
    lateinit var cartList : MutableList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)


        var currentUser= FirebaseAuth.getInstance().currentUser!!.uid

        ref2 = FirebaseDatabase.getInstance().getReference().child("Address")
        ref1 = FirebaseDatabase.getInstance().getReference().child("Users")

        addressList= mutableListOf()
        cartList= mutableListOf()

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
            var currentUser= FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("Orders")
            val ref1 = FirebaseDatabase.getInstance().getReference("Carts").orderByChild("userId").equalTo(currentUser)

            val orderId = ref.push().key

            if (radioButton3.isChecked) {


                ref1.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            for(h in snapshot.children){
                                val cart = h.getValue(Cart::class.java)
                                cartList.add(cart!!)
                                val qty = cart.cartQuantity
                                val foodName = cart.name
                                var subtotal = 0.0
                                subtotal += cart.price * cart.cartQuantity
                                var delivery = 0.0
                                delivery = subtotal*0.1
                                var total = 0.0
                                total = subtotal + delivery
                                Log.d("abcdddd", subtotal.toString())
                                Log.d("123", qty.toString())
                                Toast.makeText(applicationContext,total.toString(),Toast.LENGTH_LONG).show()
                                val storeOrder = Order(orderId!!,getTime(),foodName,qty,"pending",total,radioButton3.text.toString(),currentUser)
                                ref.child(orderId).setValue(storeOrder)
                                }


                    }
                    }
                })
                }

        }


    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }
}