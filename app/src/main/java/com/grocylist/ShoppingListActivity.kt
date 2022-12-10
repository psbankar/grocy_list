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

class ShoppingListActivity : AppCompatActivity() {
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        supportActionBar?.title = "Shopping List"

        val fab: ExtendedFloatingActionButton = findViewById(R.id.shopping_list_fab)
        recyclerView = findViewById(R.id.shopping_recyclerview)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    adapter.notifyDataSetChanged()
                }
            }

        fab.setOnClickListener {
            resultLauncher.launch(Intent(this, AddToShoppingListActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(this, fab, getString(R.string.add_new)))
        }

        adapter = ShoppingListAdapter(this)
        recyclerView.adapter = ScaleInAnimationAdapter(adapter).apply {
            setDuration(500)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shopping_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                finish()
            }
            R.id.clear_checked ->{
                adapter.clearChecked()

            }
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()

    }
}