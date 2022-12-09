package com.grocylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AddToShoppingListActivity : AppCompatActivity() {

    lateinit var quantity_spinner: AppCompatSpinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_shopping_list)


        val name: EditText = findViewById(R.id.item_name_edittext)
        val qty: EditText = findViewById(R.id.item_qty_edittext)
        var quantityMetric: String = ""
        val submitFAB: ExtendedFloatingActionButton = findViewById(R.id.save_fab)
        val db = Firebase.firestore
        quantity_spinner = findViewById(R.id.quantity_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.quantity_list,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            quantity_spinner.adapter = it
        }


        quantity_spinner.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                quantityMetric = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        })
        submitFAB.setOnClickListener {

            if (name.text.trim().toString().length != 0 && qty.text.toString().length != 0 &&
                quantityMetric.length != 0
            ) {
                val data = hashMapOf(
                    "name" to name.text.trim().toString(),
                    "amount" to qty.text.toString(),
                    "qty" to quantityMetric,
                    "checked" to false
                )
                db.collection("shopping_list").add(data).addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Successfully added ${name.text.trim()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

        }
    }
}