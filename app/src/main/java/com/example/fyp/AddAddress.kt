package com.example.fyp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_address.*
import java.util.*
import kotlin.collections.HashMap

class AddAddress : AppCompatActivity(){

    lateinit var usersRef: DatabaseReference
    lateinit  var addressList : MutableList<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_address)

        val search = findViewById<EditText>(R.id.searchAddress)

        Places.initialize(application,"AIzaSyCuM194Wot9yEMDanGPFzJvUGlSo5byW2I")

        search.setOnClickListener {
            var fieldList : List<Place.Field>  = Arrays.asList(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fieldList
            ).build(this)

            startActivityForResult(intent, 100)
        }

        back1.setOnClickListener {
            startActivity(Intent(this, LoadAddress::class.java))
            this.finish()
        }

        save.setOnClickListener {
            addAddress()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)

            searchAddress.setText(place.name)

            addressline.setText(place.name)

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            val status = Autocomplete.getStatusFromIntent(data!!)

            Toast.makeText(this,status.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAddress(){
        val alertbox = AlertDialog.Builder(this)
        alertbox.setTitle("Error")
        alertbox.setIcon(R.mipmap.icon)

        val progressDialog = ProgressDialog(this)

        usersRef = FirebaseDatabase.getInstance().getReference("Address")

        alertbox.setNegativeButton("Close"){dialog, which ->
            dialog.dismiss()
        }

        if(addressType.text.isEmpty()){
            addressType.setError("Address Type cannot be empty")
            addressType.requestFocus()
        }else if(addressline.text.isEmpty()){
            addressline.setError("Address Line cannot be empty")
            addressline.requestFocus()
        }else if(addressline2.text.isEmpty()){
            addressline.setError("Address Line 2 cannot be empty")
            addressline.requestFocus()
        }else if(state.text.isEmpty()){
            state.setError("State cannot be empty")
            state.requestFocus()
        }else if(city.text.isEmpty()) {
            city.setError("State cannot be empty")
            city.requestFocus()
        }else if(postcode.text.isEmpty()) {
            postcode.setError("State cannot be empty")
            postcode.requestFocus()
        }else if(postcode.text.length > 5) {
            postcode.setError("Postcode cannot be over 5 digit!")
            postcode.requestFocus()
        }else{
            val addressType = findViewById<EditText>(R.id.addressType)
            val addressLine = findViewById<EditText>(R.id.addressline)
            val addressLine2 = findViewById<EditText>(R.id.addressline2)
            val city = findViewById<EditText>(R.id.city)
            val state = findViewById<EditText>(R.id.state)
            val postcode = findViewById<EditText>(R.id.postcode)
            var addressId = usersRef.push().key.toString()
            val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

            val mapAddress = Address(addressId,addressType.text.toString(),addressLine.text.toString(),addressLine2.text.toString(),city.text.toString(),state.text.toString(),postcode.text.toString(),currentUserID)

            usersRef.child(addressId).setValue(mapAddress).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Add Successful!!!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoadAddress::class.java))
                    this.finish()
                }else{
                    Toast.makeText(this,"Add Fail...",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}