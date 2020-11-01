package com.example.fyp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.add_community.*
import kotlinx.android.synthetic.main.add_community.view.*
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.android.synthetic.main.fragment_community.view.*
import kotlinx.android.synthetic.main.load_post.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class CommunityFragment : Fragment(){

    lateinit var ref : DatabaseReference
    lateinit var postlist : MutableList<Post>
    lateinit var query : Query
    var stop : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_community, container, false)

        postlist = mutableListOf()


        addToList(root)

        return root
    }

    private fun addToList(root:View){
        ref = FirebaseDatabase.getInstance().getReference("Posts")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    postlist.clear()

                    for(h in snapshot.children){
                        val post =h.getValue(Post::class.java)
                        postlist.add(post!!)
                    }

                    val mLayoutManager = LinearLayoutManager(activity)
                    mLayoutManager.reverseLayout = true

                    root.reCommunity.layoutManager = mLayoutManager
                    root.reCommunity.scrollToPosition(postlist.size-1)
                    root.reCommunity.adapter = CommunityAdapter(postlist)
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addCommunity.setOnClickListener {
            val intent =Intent(activity,AddCommunity::class.java)
            startActivity(intent)
        }


    }
}