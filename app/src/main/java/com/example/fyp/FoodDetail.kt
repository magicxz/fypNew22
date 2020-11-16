package com.example.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_food_detail.*

class FoodDetail : AppCompatActivity() {

    lateinit var restaurantName: TextView

    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var foodDetailList: MutableList<Food>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        var intent = intent

        val id = intent.getStringExtra("foodId")
        val title = intent.getStringExtra("Name")
        val image = intent.getStringExtra("Image")

        restaurantName = findViewById(R.id.title1)
        restaurantName.text = title

        Picasso.get().load(image).into(fullImage)

        foodDetailList = mutableListOf()

        mRecyclerView = recyclerView1

        mDatabase = FirebaseDatabase.getInstance().getReference("Food")

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot!!.exists()){

                    //foodList.clear()

                    for(h in dataSnapshot.children){


                        if(h.child("foodId").getValue().toString().equals(id)){
                            val foodDetail =h.getValue(Food::class.java)
                            foodDetailList.add(foodDetail!!)
                            //Toast.makeText(this@FoodDetail,id,Toast.LENGTH_SHORT).show()
                        }

                    }

                    val adapter = FoodAdapter(foodDetailList)

                    mRecyclerView.setHasFixedSize(true)

                    mRecyclerView.layoutManager = LinearLayoutManager(this@FoodDetail,RecyclerView.VERTICAL,false)

                    mRecyclerView = findViewById(R.id.recyclerView1)

                    mRecyclerView.adapter =adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }
}
