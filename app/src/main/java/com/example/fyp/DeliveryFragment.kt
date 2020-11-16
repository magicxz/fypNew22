package com.example.fyp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_delivery.*
import kotlinx.android.synthetic.main.fragment_delivery.view.*

class DeliveryFragment : Fragment() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var foodList: MutableList<ReFood>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root: View = inflater.inflate(R.layout.fragment_delivery, container, false)

        restaurantList(root);

        return root
    }

    private fun restaurantList(root: View) {

        foodList = mutableListOf()

        mRecyclerView = root.recyclerview

        mDatabase = FirebaseDatabase.getInstance().getReference("Restaurant")

        root.searchFood.addTextChangedListener {

            foodList.clear()

            mDatabase.addValueEventListener(object : ValueEventListener {


                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot!!.exists()){

                        foodList.clear()


                        for(h in dataSnapshot.children){

                            val restaurant =h.getValue(ReFood::class.java)


                            val restaurantName = h.child("name").getValue().toString().toLowerCase()
                            val sub = searchFood.text.toString().toLowerCase()


                            if(restaurantName.contains(sub,false)){
                                foodList.add(restaurant!!)
                            }

                        }


                        val adapter = ReFoodAdapter(foodList)

                        //mRecyclerView = findViewById(R.id.recyclerview)

                        mRecyclerView.setHasFixedSize(true)

                        //mRecyclerView.scrollToPosition(foodList.size-1)

                        mRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)

                        mRecyclerView.adapter =adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })
        }

        mDatabase.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot!!.exists()){

                    foodList.clear()


                    for(h in dataSnapshot.children){
                        val restaurant =h.getValue(ReFood::class.java)
                            foodList.add(restaurant!!)


                    }

                    val adapter = ReFoodAdapter(foodList)

                    //mRecyclerView = findViewById(R.id.recyclerview)

                    mRecyclerView.setHasFixedSize(true)

                    mRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)

                    mRecyclerView.adapter =adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })



    }


}