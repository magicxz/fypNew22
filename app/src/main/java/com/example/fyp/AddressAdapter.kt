package com.example.fyp

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_address.*
import kotlinx.android.synthetic.main.edit_address.view.*
import kotlinx.android.synthetic.main.home.*

class AddressAdapter(var address : MutableList<Address>): RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {

    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    var addre = Address()
    lateinit var query : Query
    lateinit var addressList : MutableList<Address>

    inner class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var addressType = itemView.findViewById<TextView>(R.id.addressType)
        var addressLine = itemView.findViewById<TextView>(R.id.addressline)
        var addressLine2 = itemView.findViewById<TextView>(R.id.addressline2)
        var state = itemView.findViewById<TextView>(R.id.state)
        var city = itemView.findViewById<TextView>(R.id.city)
        var postcode = itemView.findViewById<TextView>(R.id.postcode)
        var editAddress = itemView.findViewById<ImageView>(R.id.editAddress)
        var deleteAddress = itemView.findViewById<ImageView>(R.id.deleteAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.load_address,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return address.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.addressType.text = address[position].addressType
        holder.addressLine.text = address[position].addressLine
        holder.addressLine2.text = address[position].addressLine2
        holder.city.text = address[position].city
        holder.state.text = address[position].state
        holder.postcode.text = address[position].postcode
        //val userId = address[position].userId
        //val addressId = address[position].addressId

        holder.editAddress.setOnClickListener {
            val layoutInflater = LayoutInflater.from(holder.editAddress.context).inflate(R.layout.edit_address,null,true)
            val dialogbuilder = AlertDialog.Builder(holder.editAddress.context)
            dialogbuilder.setView(layoutInflater)
            val dialog = dialogbuilder.create()
            dialog.show()

            val atype = layoutInflater.findViewById<EditText>(R.id.editType)
            val aLine = layoutInflater.findViewById<EditText>(R.id.editLine)
            val aLine2 = layoutInflater.findViewById<EditText>(R.id.editLine2)
            val aState = layoutInflater.findViewById<EditText>(R.id.editState)
            val aPostcode = layoutInflater.findViewById<EditText>(R.id.editPostcode)
            val aCity = layoutInflater.findViewById<EditText>(R.id.editCity)
            val save = layoutInflater.findViewById<Button>(R.id.saveEdit)
            val close = layoutInflater.findViewById<CircleImageView>(R.id.closePopup)

            atype.setText(holder.addressType.text)
            aLine.setText(holder.addressLine.text)
            aLine2.setText(holder.addressLine2.text)
            aState.setText(holder.state.text)
            aPostcode.setText(holder.postcode.text)
            aCity.setText(holder.city.text)

            addressList = mutableListOf()

            close.setOnClickListener {
                addressList.clear()
                dialog.dismiss()
            }

            save.setOnClickListener {
                if(atype.text.isEmpty()){
                    atype.setError("Address Type cannot be empty")
                    atype.requestFocus()
                }else if(aLine.text.isEmpty()){
                    aLine.setError("Address Line cannot be empty")
                    aLine.requestFocus()
                }else if(aLine2.text.isEmpty()){
                    aLine2.setError("Address Line 2 cannot be empty")
                    aLine2.requestFocus()
                }else if(aState.text.isEmpty()){
                    aState.setError("State cannot be empty")
                    aState.requestFocus()
                }else if(aCity.text.isEmpty()) {
                    aCity.setError("State cannot be empty")
                    aCity.requestFocus()
                }else if(aPostcode.text.isEmpty()) {
                    aPostcode.setError("State cannot be empty")
                    aPostcode.requestFocus()
                }else if(aPostcode.text.length > 5) {
                    aPostcode.setError("Postcode cannot be over 5 digit!")
                    aPostcode.requestFocus()
                }else if(addre.addressType.equals(atype.text.toString()) &&
                    addre.addressLine.equals(aLine.text.toString()) &&
                    addre.addressLine2.equals(aLine2.text.toString()) &&
                    addre.state.equals(aState.text.toString()) &&
                    addre.city.equals(aCity.text.toString()) &&
                    addre.postcode.equals(aPostcode.text.toString())){
                    val intent = Intent(layoutInflater.context, LoadAddress::class.java)
                    layoutInflater.context.startActivity(intent)
                    Toast.makeText(layoutInflater.context,"nothing",Toast.LENGTH_SHORT).show()
                }else if(!(addre.addressType.equals(atype.text.toString())) ||
                    !(addre.addressLine.equals(aLine.text.toString())) ||
                    !(addre.addressLine2.equals(aLine2.text.toString())) ||
                    !(addre.state.equals(aState.text.toString())) ||
                    !(addre.city.equals(aCity.text.toString())) ||
                    !(addre.postcode.equals(aPostcode.text.toString()))){
                    val newType = atype.text.toString()
                    val newLine = aLine.text.toString()
                    val newLine2 = aLine2.text.toString()
                    val newCity = aCity.text.toString()
                    val newState = aState.text.toString()
                    val newPostcode = aPostcode.text.toString()
                    val addressId = address[position].addressId

                    var currentUser= FirebaseAuth.getInstance().currentUser!!.uid
                    ref = FirebaseDatabase.getInstance().getReference("Address").child(addressId)

                    val addre = HashMap<String,Any>()
                    addre["addressType"] = newType
                    addre["city"] = newCity
                    addre["postcode"] = newPostcode
                    addre["state"] = newState
                    addre["addressLine"] = newLine
                    addre["addressLine2"] = newLine2

                    ref.updateChildren(addre)
                    Toast.makeText(layoutInflater.context,"Update Successful!!!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(layoutInflater.context, LoadAddress::class.java)
                    layoutInflater.context.startActivity(intent)
                    addressList.clear()
                }else{
                    Toast.makeText(layoutInflater.context,"nothing",Toast.LENGTH_SHORT)
                }
            }
        }

        holder.deleteAddress.setOnClickListener {
            val addressId = address[position].addressId
            val dialogBuilder = AlertDialog.Builder(holder.deleteAddress.context)
                .setTitle("Remove Address").setIcon(R.drawable.icon).setPositiveButton("Yes"){_, _ ->
                    FirebaseDatabase.getInstance().getReference("Address").child(addressId).removeValue()
                    addressList.clear()
                }
                .setNegativeButton("No"){_, _ ->
                    //val intent = Intent(holder.deleteAddress.context, LoadAddress::class.java)
                    //holder.deleteAddress.context.startActivity(intent)
                }.create()
            dialogBuilder.show()
        }


    }
}