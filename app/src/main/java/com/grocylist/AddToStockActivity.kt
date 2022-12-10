package com.grocylist

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class AddToStockActivity : AppCompatActivity() {
    private var editMode: Boolean = false
    private lateinit var quantitySpinner: AppCompatSpinner
    lateinit var name: TextInputEditText
    private lateinit var qty: TextInputEditText
    private lateinit var price: TextInputEditText
    private lateinit var purDate: TextInputEditText
    private lateinit var expDate: TextInputEditText
    private lateinit var submitFAB: ExtendedFloatingActionButton
    private lateinit var db: FirebaseFirestore
    private lateinit var dpd: DatePickerDialog
    private lateinit var dpd2: DatePickerDialog
    lateinit var quantityMetric: String
    private lateinit var c: Calendar
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private lateinit var arrayAdapter: ArrayAdapter<CharSequence>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_stock)

        name = findViewById(R.id.item_name_edittext)
        qty = findViewById(R.id.item_qty_edittext)
        price = findViewById(R.id.item_price_edittext)
        purDate = findViewById(R.id.item_pur_date_edittext)
        expDate = findViewById(R.id.item_exp_edittext)
        submitFAB = findViewById(R.id.save_fab)
        db = Firebase.firestore
        quantitySpinner = findViewById(R.id.quantity_spinner)
        quantityMetric = ""
        c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        supportActionBar?.title = "Add To Shopping List"

        if (intent.hasExtra("name")) {
            editMode = true
        }

        initDatePickers()
        initSpinner()


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
            quantitySpinner.setSelection(arrayAdapter.getPosition(intent.getStringExtra("qty")))
        }

        submitFAB.setOnClickListener {

            val expD = Calendar.getInstance()
            expD.set(dpd2.datePicker.year, dpd2.datePicker.month, dpd2.datePicker.dayOfMonth)
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
                        data["expiry_date"] = expD.time

                    }
                    db.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("stock").document(intent.getStringExtra("documentID")!!)
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
                        data["date_purchased"] = purD.time

                    }
                    if (expDate.text?.length != 0) {
                        data["expiry_date"] = expD.time

                    }
                    db.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("stock").add(data).addOnSuccessListener {
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

    private fun initSpinner() {
        arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.quantity_list,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            quantitySpinner.adapter = it
        }
        quantitySpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
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

    private fun initDatePickers() {

        dpd = DatePickerDialog(
            this,
            { _, y, monthOfYear, dayOfMonth ->
                purDate.setText(getString(R.string.date_format, dayOfMonth, monthOfYear, y))


            },
            year,
            month,
            day
        )
        dpd.setOnCancelListener {
            purDate.setText("")
        }


        dpd2 = DatePickerDialog(
            this,
            { _, y, monthOfYear, dayOfMonth ->

                expDate.setText(getString(R.string.date_format, dayOfMonth, monthOfYear, y))

            },
            year,
            month,
            day
        )

        dpd2.setOnCancelListener {
            expDate.setText("")
        }

    }
}