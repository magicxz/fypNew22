package com.example.fyp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ReFoodAdapter(var food: MutableList<ReFood>):
    RecyclerView.Adapter<ReFoodAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.delivery_layout,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = food[position].name
        holder.des.text = food[position].description
        holder.rating.text = food[position].rating.toString()


        Picasso.get().load(food[position].image).into(holder.image)

        holder.image.setOnClickListener{
            val intent = Intent(holder.name.context,FoodDetail::class.java)
            intent.putExtra("foodId",food[position].foodId)
            intent.putExtra("Name",food[position].name)
            intent.putExtra("Image",food[position].image)


            holder.name.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return food.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val name: TextView = itemView.findViewById(R.id.name)
        val des: TextView = itemView.findViewById(R.id.desc)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val image: ImageView = itemView.findViewById((R.id.image))


    }



}