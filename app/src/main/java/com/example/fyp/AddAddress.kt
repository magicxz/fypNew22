package com.example.fyp

import android.app.Activity
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
import kotlinx.android.synthetic.main.add_address.*
import java.util.*

class AddAddress : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_address)

        val search = findViewById<EditText>(R.id.searchAddress)
        val State = findViewById<TextView>(R.id.state)

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)

            searchAddress.setText(place.address)
            state.setText("Locality Name :" + place.name)
        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            val status = Autocomplete.getStatusFromIntent(data!!)

            Toast.makeText(this,status.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }
}