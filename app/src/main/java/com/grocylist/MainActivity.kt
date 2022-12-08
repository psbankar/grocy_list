package com.grocylist

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


//todo toolbar
//todo menus(sort)
//todo animations
//todo Persistance
//todo dialogs

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    var auth: FirebaseAuth = Firebase.auth
    var currentUser: FirebaseUser? = auth.currentUser
    lateinit var stockOverviewCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        startActivity(Intent(this, StockOverviewActivity::class.java))
        val user = Firebase.auth.currentUser

        stockOverviewCard = findViewById(R.id.stock_overview_card)

        stockOverviewCard.setOnClickListener{
            startActivity(Intent(this, StockOverviewActivity::class.java))
        }


        if (user == null)
            startActivity(Intent(this, LoginActivity::class.java))


        navView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, 0,0)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.find_stores -> {
                    startActivity(Intent(this, FindStoresActivity::class.java))


                }
                R.id.sign_out -> {
                    Firebase.auth.signOut().runCatching {

                    }.onSuccess {
                        currentUser = null
                        updateUI(currentUser) }

                }

            }
            true
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user==null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}