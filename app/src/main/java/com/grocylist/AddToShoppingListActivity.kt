package com.grocylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AddToShoppingListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_shopping_list)


        val name: EditText = findViewById(R.id.item_name_edittext)
        val qty: EditText = findViewById(R.id.item_qty_edittext)
        val submitFAB: ExtendedFloatingActionButton = findViewById(R.id.save_fab)
        val db = Firebase.firestore

        submitFAB.setOnClickListener {
            val data = hashMapOf(
                "name" to name.text.trim().toString(),
                "amount" to qty.text.toString(),
                "qty" to "Pack",
                "checked" to false
            )
            db.collection("shopping_list").add(data).addOnSuccessListener {
                Toast.makeText(this, "Successfully added ${name.text.trim()}", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }
    }
}