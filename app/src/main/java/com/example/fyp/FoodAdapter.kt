package com.example.fyp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FoodAdapter (var foodDetail: MutableList<Food>):
    RecyclerView.Adapter<FoodAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.food_detail_layout,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name1.text = foodDetail[position].name
        holder.des1.text = foodDetail[position].description
        holder.price1.text = foodDetail[position].price.toString()

        Picasso.get().load(foodDetail[position].image).into(holder.image1)

        holder.image1.setOnClickListener{
            val intent = Intent(holder.name1.context,AddToCart::class.java)
            intent.putExtra("DetailId",foodDetail[position].DetailId)
            intent.putExtra("Name",foodDetail[position].name)
            intent.putExtra("Price",foodDetail[position].price.toString())
            intent.putExtra("Image",foodDetail[position].image)

            holder.name1.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return foodDetail.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val name1: TextView = itemView.findViewById(R.id.name1)
        val des1: TextView = itemView.findViewById(R.id.desc1)
        val price1: TextView = itemView.findViewById(R.id.price)
        val image1: ImageView = itemView.findViewById((R.id.image1))


    }


}