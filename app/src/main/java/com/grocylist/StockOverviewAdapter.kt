package com.grocylist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.absoluteValue

class StockOverviewAdapter(stockOverviewActivity: StockOverviewActivity
) : RecyclerView.Adapter<StockOverviewAdapter.StockOverviewViewHolder>() {
    lateinit var list: MutableList<DocumentSnapshot>
    var context = stockOverviewActivity
    val db: CollectionReference = Firebase.firestore.collection("stock")
    init {
        loadDB()
    }

    private fun loadDB() {
        db.addSnapshotListener { value, error ->
            list = value!!.documents
        }
    }

    inner class StockOverviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.item_name)
        val qty: TextView = itemView.findViewById(R.id.item_qty)
        val expiry: TextView = itemView.findViewById(R.id.item_expiry)
        val card: MaterialCardView = itemView.findViewById(R.id.item_card)
    }

//    fun getActivity(){
//        con = activity
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOverviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_recyclerview_item, parent, false)
        return StockOverviewViewHolder(view)
    }

    @SuppressLint("MissingInflatedId")
    override fun onBindViewHolder(holder: StockOverviewViewHolder, position: Int) {
        holder.name.text = list[position].data?.get("name").toString()
        holder.qty.text = "${list[position].data?.get("amount").toString()} ${
            list[position].data?.get("qty").toString()
        }"
        try{
        val tempDate = (list[position].data?.get("expiry_date") as Timestamp).toDate().time - Date().time
        val seconds = tempDate / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        if(days<0){
            holder.expiry.text = " Item overdue ${days.absoluteValue} day(s) ago"
            holder.expiry.visibility =View.VISIBLE
            holder.expiry.setTextColor(Color.RED)
        }
        else if(days in 1..6){
            holder.expiry.text = " Item expiring in $days day(s)"
            holder.expiry.visibility =View.VISIBLE
            holder.expiry.setTextColor(ContextCompat.getColor(context, R.color.orange))

        }
            else{
            holder.expiry.visibility =View.INVISIBLE
        }
        }
        catch (_: Exception){}

        holder.card.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.stock_bottomsheet_layout, null)
            val deleteButton : Button = view.findViewById(R.id.delete_button)
            val addToShoppingListButton : Button = view.findViewById(R.id.add_to_sl_button)
            deleteButton.setOnClickListener {
                deleteItem(position)
                dialog.hide()
            }

            addToShoppingListButton.setOnClickListener {
                addToSL(position)
                dialog.hide()
            }
            dialog.setContentView(view)
            dialog.show()
            view.findViewById<TextView>(R.id.title).text =
                list[position].data?.get("name").toString()
            view.findViewById<TextView>(R.id.amount).text =
                list[position].data?.get("amount").toString() + " " + list[position].data?.get("qty").toString()
            if (list[position].data?.get("expiry_date") != null) {
                val date = (list[position].data?.get("expiry_date") as Timestamp).toDate()
                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val newdate = sdf.format(date)
                view.findViewById<TextView>(R.id.expiry_date).text =
                    newdate.toString()
            }
            view.findViewById<TextView>(R.id.price).text =
                list[position].data?.get("price").toString()
            if (list[position].data?.get("date_purchased") != null) {
                val date = (list[position].data?.get("date_purchased") as Timestamp).toDate()
                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val purchasedate = sdf.format(date)
                view.findViewById<TextView>(R.id.datPurchased).text =
                    purchasedate.toString()
            }

        }
    }

    private fun addToSL(position: Int) {
        val tempName =list[position].data?.get("name").toString()
        val data = hashMapOf(
            "name" to tempName,
            "amount" to list[position].data?.get("amount").toString(),
            "qty" to list[position].data?.get("qty").toString(),
            "checked" to false
        )
        Firebase.firestore.collection("shopping_list").add(data).addOnSuccessListener {
            Toast.makeText(
                context,
                "Successfully added ${tempName} to shopping list",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun deleteItem(position: Int) {
        list[position].reference.delete()
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun sortByTitle() {

        list.sortBy {
            it.data?.get("name").toString().lowercase()
        }
        notifyDataSetChanged()
    }


}
