package com.grocylist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddToShoppingListActivity : AppCompatActivity() {

    private lateinit var quantitySpinner: AppCompatSpinner
    private var editMode: Boolean = false
    lateinit var name: EditText
    private lateinit var qty: EditText
    lateinit var quantityMetric: String
    private lateinit var submitFAB: ExtendedFloatingActionButton
    private lateinit var db: FirebaseFirestore
    private lateinit var arrayAdapter: ArrayAdapter<CharSequence>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_shopping_list)

        if (intent.hasExtra("name")) {
            editMode = true
        }

        supportActionBar?.title = "Add To Shopping List"

        name = findViewById(R.id.item_name_edittext)
        qty = findViewById(R.id.item_qty_edittext)
        submitFAB = findViewById(R.id.save_fab)
        quantitySpinner = findViewById(R.id.quantity_spinner)
        quantityMetric = ""
        db = Firebase.firestore


        initSpinner()


        if (editMode) {
            name.setText(intent.getStringExtra("name"))
            qty.setText(intent.getStringExtra("amount"))
            quantitySpinner.setSelection(arrayAdapter.getPosition(intent.getStringExtra("qty")))
        }

        submitFAB.setOnClickListener {

            if (editMode) {
                if (name.text.trim().toString().isNotEmpty() && qty.text.toString().isNotEmpty() &&
                    quantityMetric.isNotEmpty()
                ) {
                    db.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("shopping_list").document(intent.getStringExtra("documentID")!!)
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
                if (name.text.trim().toString().isNotEmpty() && qty.text.toString().isNotEmpty() &&
                    quantityMetric.isNotEmpty()
                ) {
                    val data = hashMapOf(
                        "name" to name.text.trim().toString(),
                        "amount" to qty.text.toString(),
                        "qty" to quantityMetric,
                        "checked" to false
                    )
                    db.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("shopping_list").add(data).addOnSuccessListener {
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

    private fun initSpinner() {
        arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.quantity_list,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            quantitySpinner.adapter = it
        }

        quantitySpinner.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                quantityMetric = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        })
    }
}