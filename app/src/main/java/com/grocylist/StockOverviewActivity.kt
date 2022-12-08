package com.grocylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StockOverviewActivity : AppCompatActivity() {

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_overview)

        val db = Firebase.firestore
        val recyclerView: RecyclerView = findViewById(R.id.stock_recyclerview)
        val fab: ExtendedFloatingActionButton = findViewById(R.id.add_to_stock_fab)
        lateinit var adapter: StockOverviewAdapter
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
//                    openAddToStockActivity()
                }
            }

        fab.setOnClickListener {

            resultLauncher.launch(Intent(this, AddToStockActivity::class.java))
        }

        db.collection("stock").addSnapshotListener { value, error ->
            val data = value!!.documents

            adapter = StockOverviewAdapter(data)
            recyclerView.adapter = adapter

            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        db.collection("stock").get().addOnSuccessListener {



        }
    }

}