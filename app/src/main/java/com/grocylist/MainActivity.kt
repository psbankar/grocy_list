package com.grocylist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


//todo toolbar
//todo menus(sort)
//todo animations
//todo Persistance
//todo dialogs

class MainActivity : AppCompatActivity() {

    private var overdue: Int = 0
    private var count: Int = 0
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    var auth: FirebaseAuth = Firebase.auth
    var currentUser: FirebaseUser? = auth.currentUser
    lateinit var stockOverviewCard: CardView
    lateinit var shoppingListCard: CardView
    lateinit var db: FirebaseFirestore
    var value: Int = 0
    lateinit var text1: TextView
    lateinit var text2: TextView
    lateinit var text3: TextView
    var expCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        startActivity(Intent(this, StockOverviewActivity::class.java))
        val user = Firebase.auth.currentUser

        db = Firebase.firestore
        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        text3 = findViewById(R.id.text3)


        db.collection("stock").addSnapshotListener { value, error ->

            count = value!!.size()
            value.forEach {
                Log.d("rgd", it.data.toString())
                try {
        //                    Log.d("hdth",it["price"].toString())
                    this.value += it["price"].toString().toInt()
                    val tempDate = (it["expiry_date"] as Timestamp).toDate().time - Date().time
                    val seconds = tempDate / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24

                    if(days<0){
                        overdue += 1
                    }
                    else if(days in 1..6){
                        expCount += 1
                    }

                } catch (e: Exception){
        //                    Log.d("hdth",it["name"].toString())
                            Log.d("hdth",it.id.toString())
                }

            }
            Log.d("vvvv", value.size().toString())

            text1.text = "• ${count.toString()} products in stock with value of \$${this.value}"
            text2.text = "• ${expCount} Products are due within next 7 days and ${overdue} products are overdue"
        }

        db.collection("shopping_list").addSnapshotListener { value, error ->

            text3.text = "• You have ${value?.size()} products in your shopping list"
        }

        Log.d("nynf", count.toString())


        stockOverviewCard = findViewById(R.id.stock_overview_card)
        shoppingListCard = findViewById(R.id.shopping_list_card)

        stockOverviewCard.setOnClickListener{
            startActivity(Intent(this, StockOverviewActivity::class.java))
        }

        shoppingListCard.setOnClickListener{
            startActivity(Intent(this, ShoppingListActivity::class.java))
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