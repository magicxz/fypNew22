package com.example.fyp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class cartAdapter (var cart: MutableList<Cart>):
    RecyclerView.Adapter<cartAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.cart_list_layout,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name1.text = cart[position].name
        holder.price1.text = cart[position].price.toString()
        holder.quantity.text = cart[position].cartQuantity.toString()

        Picasso.get().load(cart[position].image).into(holder.image1)

        /* holder.image1.setOnClickListener{
             val intent = Intent(holder.name1.context, payment::class.java)
             intent.putExtra("Name",cart[position].name)
             intent.putExtra("Price",cart[position].price.toString())
             intent.putExtra("quantity",cart[position].cartQuantity)
             intent.putExtra("Image",cart[position].image)

             holder.name1.context.startActivity(intent)
         }*/
    }

    override fun getItemCount(): Int {
        return cart.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name1: TextView = itemView.findViewById(R.id.name2)
        val quantity: TextView = itemView.findViewById(R.id.textView3)
        val price1: TextView = itemView.findViewById(R.id.price1)
        val image1: ImageView = itemView.findViewById((R.id.image2))

    }
}