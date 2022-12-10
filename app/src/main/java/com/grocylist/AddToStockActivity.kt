package com.grocylist

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import javax.xml.datatype.DatatypeConstants.MONTHS


class AddToStockActivity : AppCompatActivity() {
    var editMode: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_stock)
        lateinit var quantity_spinner: AppCompatSpinner
        val name: TextInputEditText = findViewById(R.id.item_name_edittext)
        val qty: TextInputEditText = findViewById(R.id.item_qty_edittext)
        val price: TextInputEditText = findViewById(R.id.item_price_edittext)
        val purDate: TextInputEditText = findViewById(R.id.item_pur_date_edittext)
        val expDate: TextInputEditText = findViewById(R.id.item_exp_edittext)
        val submitFAB: ExtendedFloatingActionButton = findViewById(R.id.save_fab)
        val db = Firebase.firestore
        var quantityMetric: String = ""

        supportActionBar?.title = "Add To Shopping List"


        if (intent.hasExtra("name")) {
            editMode = true
        }
        quantity_spinner = findViewById(R.id.quantity_spinner)

        //
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                purDate.setText(dayOfMonth.toString() + "-" + monthOfYear + "-" + year)

            },
            year,
            month,
            day
        )
        dpd.setOnCancelListener {
            purDate.setText("")
        }

        val dpd2 = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                expDate.setText(dayOfMonth.toString() + "-" + monthOfYear + "-" + year)

            },
            year,
            month,
            day
        )

        dpd2.setOnCancelListener {
            expDate.setText("")
        }

//        dpd.show()
        //
        val aa = ArrayAdapter.createFromResource(
            this,
            R.array.quantity_list,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            quantity_spinner.adapter = it
        }
        quantity_spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
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

        purDate.setOnClickListener {
            dpd.show()
        }

        expDate.setOnClickListener {
            dpd2.show()
        }

        if (editMode) {
            name.setText(intent.getStringExtra("name"))
            qty.setText(intent.getStringExtra("amount"))
            price.setText(intent.getStringExtra("price"))

            quantity_spinner.setSelection(aa.getPosition(intent.getStringExtra("qty")))
        }

        submitFAB.setOnClickListener {

            val expD = Calendar.getInstance()
            expD.set(dpd2.datePicker.year, dpd2.datePicker.month, dpd2.datePicker.dayOfMonth)
//
            val purD = Calendar.getInstance()
            purD.set(dpd.datePicker.year, dpd.datePicker.month, dpd.datePicker.dayOfMonth)

            if (editMode) {
                if (name.text?.trim().toString().isNotEmpty() && qty.text.toString().isNotEmpty() &&
                    quantityMetric.isNotEmpty()
                ) {
                    val data = hashMapOf<String, Any>(
                        "name" to name.text?.trim().toString(),
                        "amount" to qty.text.toString(),
                        "price" to price.text.toString(),
                        "qty" to quantityMetric
                    )

                    if (purDate.text?.length != 0) {
                        data["date_purchased"] = purD.time

                    }
                    if (expDate.text?.length != 0) {
                        data["expiry_date"] = purD.time

                    }
                    db.collection("stock").document(intent.getStringExtra("documentID")!!)
                        .update(
                            data
                        ).addOnSuccessListener {
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }

                } else {
                    Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (name.text?.trim()
                        .toString().isNotEmpty() && qty.text.toString()
                        .isNotEmpty() && price.text.toString()
                        .isNotEmpty()

                ) {
                    val data = hashMapOf<String, Any>(
                        "name" to name.text?.trim().toString(),
                        "amount" to qty.text.toString(),
                        "price" to price.text.toString(),
                        "qty" to quantityMetric
                    )

                    if (purDate.text?.length != 0) {
                        data.put("date_purchased", purD.time)

                    }
                    if (expDate.text?.length != 0) {
                        data.put("expiry_date", purD.time)

                    }
                    db.collection("stock").add(data).addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Successfully added ${name.text?.trim()}",
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