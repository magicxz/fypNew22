package com.example.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_to_cart.*

class AddToCart : AppCompatActivity() {

    lateinit var foodName: TextView
    lateinit var foodPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_cart)

        var intent = intent

        val id = intent.getStringArrayExtra("DetailId")
        val name = intent.getStringExtra("Name")
        val price = intent.getStringExtra("Price")
        val image = intent.getStringExtra("Image")

        foodName = findViewById(R.id.textView)
        foodName.text = name
        foodPrice = findViewById(R.id.price3)
        foodPrice.text = price

        Picasso.get().load(image).into(imageView4)

        placeOrder.setOnClickListener{

            placeOrder(name,price,image)

        }

        number.text = "1"

        totalAmount1.text = price.toString()

        minus.setOnClickListener{
            if(!(number.text.toString().toInt().equals(1))){
                number.text = (number.text.toString().toInt()-1).toString()

                totalAmount1.text = (number.text.toString().toInt()*price.toDouble()).toString()

            }

        }
        plus.setOnClickListener{
            number.text = (number.text.toString().toInt()+1).toString()
            totalAmount1.text = (number.text.toString().toInt()*price.toDouble()).toString()

        }
    }

    private fun placeOrder(name :String, price: String, image:String) {
        val ref = FirebaseDatabase.getInstance().getReference("Carts")

        val cartId = ref.push().key

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val cartItem = Cart(cartId.toString(),currentUser,number.text.toString().toInt(),name,price.toDouble(),image)

        ref.child(cartId.toString()).setValue(cartItem)

    }

}
