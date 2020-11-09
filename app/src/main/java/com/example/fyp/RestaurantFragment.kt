package com.example.fyp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class RestaurantFragment : Fragment() {

    lateinit var etSource: EditText
    lateinit var etDestination: EditText
    lateinit var btnTrack: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root: View = inflater.inflate(R.layout.fragment_restaurant, container, false)

        etSource = root.findViewById(R.id.text1)
        etDestination = root.findViewById(R.id.text2)
        btnTrack = root.findViewById(R.id.btn)

        btnTrack.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                //get value from edit text
                var sSource = etSource.text.toString().trim()
                var sDestination = etDestination.text.toString().trim()

                //check condition
                if(sSource == "" && sDestination == ""){
                    Toast.makeText(root.context,"Enter both location", Toast.LENGTH_SHORT).show()
                }else{
                    //when both value fill
                    //display track
                    DisplayTrack(sSource,sDestination)
                }
            }

        })

        return root
    }

    private fun DisplayTrack(sSource: String, sDestination: String) {
        try{
            //when google map is installed
            val uri = Uri.parse("https://www.google.co.in/maps/dir/$sSource/$sDestination")

            //Initialize intent with action view
            val intent = Intent(Intent.ACTION_VIEW,uri)

            //Set package
            intent.setPackage("com.google.android.apps.maps")

            //set flag
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            //Start activity
            startActivity(intent)
        }catch (e: ActivityNotFoundException){
            //when google map is not installed
            val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")

            val intent = Intent(Intent.ACTION_VIEW,uri)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
        }
    }
}