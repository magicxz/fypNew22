package com.example.fyp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home.view.*
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.profile.*

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var user = Users()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val currentUser = FirebaseAuth.getInstance().currentUser

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //setSupportActionBar(toolbar)
        navBar.bringToFront()
        navBar.setNavigationItemSelectedListener(this)

        navMenu.setOnClickListener {
            if(currentUser != null){
                show.isVisible = true
                show1.isVisible = false
            }else{
                show.isVisible = false
                show1.isVisible = true
            }
            drawerLayout.openDrawer(GravityCompat.START)
        }

        viewPager.adapter = PageAdapter(supportFragmentManager)
        tab.setupWithViewPager(viewPager)


        val menu = navBar.menu
        if(currentUser == null) {
            menu.findItem(R.id.navProfile).setVisible(false)
            menu.findItem(R.id.navOrder).setVisible(false)
            menu.findItem(R.id.navAddress).setVisible(false)
            menu.findItem(R.id.navNotification).setVisible(false)
            menu.findItem(R.id.navVoucher).setVisible(false)
            menu.findItem(R.id.navLogout).setVisible(false)
        }else{
            displayName()
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId) {
            R.id.navHome -> {
                startActivity(Intent(this, Home::class.java))
            }
            R.id.navProfile -> {
                startActivity(Intent(this, Profile::class.java))
            }
            R.id.navOrder -> {
            }
            R.id.navAddress -> {
            }
            R.id.navNotification -> {
            }
            R.id.navVoucher -> {
            }
            R.id.navLogout -> {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this,"Logout successful!!!",Toast.LENGTH_LONG).show()
                startActivity(Intent(this, Login::class.java))
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    fun onLabelClick(view: View){
        startActivity(Intent(this,Login::class.java))
    }

    private fun displayName(){
        var currentUser=FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser)

        usersRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    user =snapshot.getValue<Users>(Users::class.java)!!
                    username.text = user!!.username
                    Picasso.get().load(user!!.image).into(userImage)
                }
            }
        })
    }
}

