package com.grocylist

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import java.util.*


class MainActivity : AppCompatActivity() {

    private var overdue: Int = 0
    private var count: Int = 0
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private var auth: FirebaseAuth = Firebase.auth
    private var currentUser: FirebaseUser? = auth.currentUser
    private lateinit var stockOverviewCard: CardView
    private lateinit var shoppingListCard: CardView
    private lateinit var db: FirebaseFirestore
    private var value: Double = 0.0
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView
    private var expCount = 0
    private var tts: TextToSpeech? = null
    private var btnSpeak: ImageView? = null
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.navView)
        btnSpeak = findViewById(R.id.volumebutton)
        db = Firebase.firestore
        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        text3 = findViewById(R.id.text3)
        stockOverviewCard = findViewById(R.id.stock_overview_card)
        shoppingListCard = findViewById(R.id.shopping_list_card)

        supportActionBar?.title = "Home Page"

        loadSummary()
        loadDrawer()



        stockOverviewCard.setOnClickListener {
            startActivity(Intent(this, StockOverviewActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(this, stockOverviewCard, getString(R.string.stock_overview)).toBundle())
        }

        shoppingListCard.setOnClickListener {
            startActivity(Intent(this, ShoppingListActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(this, shoppingListCard, getString(R.string.shopping_list)).toBundle())
        }

        ttsImplementation()

    }

    private fun ttsImplementation() {
        tts = TextToSpeech(
            applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale.UK
            }

            btnSpeak!!.setOnClickListener { speakout() }
        }
    }

    private fun loadDrawer() {
        val header = navView.getHeaderView(0)
        val profileImg = header.findViewById<CircularImageView>(R.id.profileimg)
        val username = header.findViewById<TextView>(R.id.username)
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Picasso.get()
            .load(user?.photoUrl)
            .into(profileImg)
        username.text = user?.displayName

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.find_stores -> {
                    startActivity(Intent(this, FindStoresActivity::class.java))
                }
                R.id.sign_out -> {
                    alertBox()
                }
            }
            true
        }
    }

    private fun loadSummary() {
        db.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("stock").addSnapshotListener { value, _ ->
            count = value!!.size()
            this.value = 0.0
            expCount = 0
            overdue = 0
            value.forEach {
                try {
                    this.value += it["price"].toString().toDouble()
                    val tempDate = (it["expiry_date"] as Timestamp).toDate().time - Date().time
                    val seconds = tempDate / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24


                    if (days < 0) {
                        overdue += 1
                    } else if (days in 1..6) {
                        expCount += 1
                    }

                } catch (_: Exception) {
                }

            }
            text1.text = getString(R.string.text1, count, this.value)
            text2.text = getString(R.string.text2, expCount, overdue)
        }

        db.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("shopping_list").addSnapshotListener { value, error ->

            text3.text = getString(R.string.text3, value?.size())
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.currentUser
        updateUI(currentUser)
        loadSummary()
    }

    private fun speakout() {
        val text = text1.text.toString()
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun alertBox() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout Alert")
            .setMessage("Are you sure, you want to Logout ?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, which ->
                Firebase.auth.signOut().runCatching {
                }.onSuccess {
                    updateUI(null)
                }
            }
            .setNegativeButton(
                "No"
            ) { dialog, which ->
                drawerLayout.closeDrawer(GravityCompat.START)

            }
        //Creating dialog box
        val dialog = builder.create()
        dialog.show()
    }

}