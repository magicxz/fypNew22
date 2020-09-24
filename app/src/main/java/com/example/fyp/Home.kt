package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home.view.*

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //setSupportActionBar(toolbar)
        navBar.bringToFront()
        navBar.setNavigationItemSelectedListener(this)

        navMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        viewPager.adapter = PageAdapter(supportFragmentManager)
        tab.setupWithViewPager(viewPager)

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId){
            R.id.navHome -> {
                startActivity(Intent(this,Home::class.java))
            }
            R.id.navProfile -> {
                startActivity(Intent(this,Profile::class.java))
            }
            R.id.navOrder -> {}
            R.id.navAddress -> {}
            R.id.navNotification -> {}
            R.id.navVoucher -> {}
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onLabelClick(view: View){
        startActivity(Intent(this,Login::class.java))
    }

}