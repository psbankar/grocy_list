package com.grocylist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

class StockOverviewActivity : AppCompatActivity() {

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: StockOverviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_overview)


        supportActionBar?.title = "Stock Overview"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val recyclerView: RecyclerView = findViewById(R.id.stock_recyclerview)
        val fab: ExtendedFloatingActionButton = findViewById(R.id.add_to_stock_fab)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    adapter.notifyDataSetChanged()
                }
            }

        fab.setOnClickListener {
            resultLauncher.launch(Intent(this, AddToStockActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(this, fab, getString(R.string.add_to_stock)))
        }

        adapter = StockOverviewAdapter(this)
        recyclerView.adapter = ScaleInAnimationAdapter(adapter).apply {
            setDuration(500)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.sort_by_title ->{
                adapter.sortByTitle()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}