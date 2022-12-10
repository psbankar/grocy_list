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
    var editMode: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_shopping_list)

        if (intent.hasExtra("name")) {
            editMode = true
        }

        val name: EditText = findViewById(R.id.item_name_edittext)
        val qty: EditText = findViewById(R.id.item_qty_edittext)
        var quantityMetric: String = ""
        val submitFAB: ExtendedFloatingActionButton = findViewById(R.id.save_fab)
        val db = Firebase.firestore
        quantity_spinner = findViewById(R.id.quantity_spinner)


        supportActionBar?.title = "Add To Shopping List"

        val aa = ArrayAdapter.createFromResource(
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

        if (editMode) {
            name.setText(intent.getStringExtra("name"))
            qty.setText(intent.getStringExtra("amount"))
            quantity_spinner.setSelection(aa.getPosition(intent.getStringExtra("qty")))
        }
        submitFAB.setOnClickListener {

            if (editMode) {
                if (name.text.trim().toString().length != 0 && qty.text.toString().length != 0 &&
                    quantityMetric.length != 0
                ) {
                    db.collection("shopping_list").document(intent.getStringExtra("documentID")!!)
                        .update(
                            hashMapOf(
                                "name" to name.text.trim().toString(),
                                "amount" to qty.text.toString(),
                                "qty" to quantityMetric,
                                "checked" to false
                            ) as Map<String, Any>
                        )
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } else {
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
                } else {
                    Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}