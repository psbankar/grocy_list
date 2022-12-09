package com.grocylist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

class StockOverviewActivity : AppCompatActivity() {

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_overview)


        supportActionBar?.title = "Stock Overview"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



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

            adapter = StockOverviewAdapter(data, this)
            recyclerView.adapter = ScaleInAnimationAdapter(adapter).apply {
                // Change the durations.
                setDuration(500)
                // Disable the first scroll mode.
//                setFirstOnly(false)
            }

            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

}