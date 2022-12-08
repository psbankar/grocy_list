package com.grocylist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar


class AddToStockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_stock)

        val name: EditText = findViewById(R.id.item_name_edittext)
        val qty: EditText = findViewById(R.id.item_qty_edittext)
        val price: EditText = findViewById(R.id.item_price_edittext)
        val purDate: DatePicker = findViewById(R.id.item_pur_date_picker)
        val expDate: DatePicker = findViewById(R.id.item_exp_date_picker)
        val submitFAB: ExtendedFloatingActionButton = findViewById(R.id.save_fab)
        val db = Firebase.firestore

        submitFAB.setOnClickListener {
            val expD = Calendar.getInstance()
            expD.set(expDate.year, expDate.month, expDate.dayOfMonth)

            val purD = Calendar.getInstance()
            purD.set(purDate.year, purDate.month, purDate.dayOfMonth)
            val data = hashMapOf(
                "name" to name.text.trim().toString(),
                "amount" to qty.text.toString(),
                "price" to price.text.toString(),
                "qty" to "Pack",
                "date_purchased" to purD.time,
                "expiry_date" to expD.time
            )
            db.collection("stock").add(data).addOnSuccessListener {
                Toast.makeText(this, "Successfully added ${name.text.trim()}", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }
    }
}