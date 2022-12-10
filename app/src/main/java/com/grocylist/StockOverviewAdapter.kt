package com.grocylist

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


class StockOverviewAdapter(
    stockOverviewActivity: StockOverviewActivity
) : RecyclerView.Adapter<StockOverviewAdapter.StockOverviewViewHolder>() {
    lateinit var list: MutableList<DocumentSnapshot>
    var context = stockOverviewActivity
    private val db: CollectionReference = Firebase.firestore.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("stock")

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOverviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_recyclerview_item, parent, false)
        return StockOverviewViewHolder(view)
    }


    override fun onBindViewHolder(holder: StockOverviewViewHolder, position: Int) {
        holder.name.text = list[position].data?.get("name").toString()
        holder.qty.text = context.getString(R.string.item_quantity,list[position].data?.get("amount").toString(), list[position].data?.get("qty").toString() )
        try {
            val tempDate =
                (list[position].data?.get("expiry_date") as Timestamp).toDate().time - SimpleDateFormat("dd/MM/yyyy").parse(SimpleDateFormat("dd/MM/yyyy").format(Date())).time
            val seconds = tempDate / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            if (days < 0) {
                holder.expiry.text = context.getString(R.string.overdue, days.absoluteValue)
                holder.expiry.visibility = View.VISIBLE
                holder.expiry.setTextColor(Color.RED)
            } else if (days in 0..6) {
                holder.expiry.text = context.getString(R.string.expiring, days)
                holder.expiry.visibility = View.VISIBLE
                holder.expiry.setTextColor(ContextCompat.getColor(context, R.color.orange))

            } else {
                holder.expiry.visibility = View.INVISIBLE
            }
        } catch (_: Exception) {
        }

        holder.card.setOnClickListener {
            openBottomDialog(position)
        }
    }

    private fun openBottomDialog(position: Int) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.stock_bottomsheet_layout, null)

        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val editButton: Button = view.findViewById(R.id.edit_button)
        val addToShoppingListButton: Button = view.findViewById(R.id.add_to_sl_button)
        val consume: Button = view.findViewById(R.id.consume)
        deleteButton.setOnClickListener {
            deleteItem(position)
            dialog.hide()
        }
        editButton.setOnClickListener {
            val i = Intent(context, AddToStockActivity::class.java)
            i.putExtra("name", list[position].data?.get("name").toString())
            i.putExtra("amount", list[position].data?.get("amount").toString())
            i.putExtra("qty", list[position].data?.get("qty").toString())
            i.putExtra("price", list[position].data?.get("price").toString())
            i.putExtra("documentID", list[position].id)
            context.resultLauncher.launch(i)
            dialog.hide()
        }

        addToShoppingListButton.setOnClickListener {
            addToSL(position)
            dialog.hide()
        }
        consume.setOnClickListener {
            consumeQuantity(position)
            dialog.hide()
        }

        view.findViewById<TextView>(R.id.title).text =
            list[position].data?.get("name").toString()
        view.findViewById<TextView>(R.id.amount).text = context.getString(R.string.item_quantity, list[position].data?.get("amount").toString(),list[position].data?.get("qty").toString() )
        if (list[position].data?.get("expiry_date") != null) {
            val date = (list[position].data?.get("expiry_date") as Timestamp).toDate()
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val newdate = sdf.format(date)
            view.findViewById<TextView>(R.id.expiry_date).text =
                newdate.toString()
        }
        view.findViewById<TextView>(R.id.price).text = context.getString(R.string.price_format, list[position].data?.get("price").toString())
        if (list[position].data?.get("date_purchased") != null) {
            val date = (list[position].data?.get("date_purchased") as Timestamp).toDate()
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val purchasedate = sdf.format(date)
            view.findViewById<TextView>(R.id.datPurchased).text =
                purchasedate.toString()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun addToSL(position: Int) {
        val tempName = list[position].data?.get("name").toString()
        val data = hashMapOf(
            "name" to tempName,
            "amount" to list[position].data?.get("amount").toString(),
            "qty" to list[position].data?.get("qty").toString(),
            "checked" to false
        )
        Firebase.firestore.collection("data").document(Firebase.auth.currentUser?.uid.toString()).collection("shopping_list").add(data).addOnSuccessListener {
            Toast.makeText(
                context,
                "Successfully added $tempName to shopping list",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun deleteItem(position: Int) {
        list[position].reference.delete()
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    private fun consumeQuantity(position: Int) {

        val builder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        val dialoglayout = inflater.inflate(R.layout.update_quantity_dialog, null)


        dialoglayout.findViewById<TextView>(R.id.consume_quantity).text =
            list[position].data?.get("amount").toString()
        dialoglayout.findViewById<TextView>(R.id.quantity_spinner).text =
            list[position].data?.get("qty").toString()
        val editText = dialoglayout.findViewById<TextView>(R.id.consume_quantity)
        builder.setView(dialoglayout)
        builder.setTitle("Consume")
            .setMessage("Update the Quantity")
            .setCancelable(false)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            val q =
                list[position].data?.get("amount").toString().toDouble() - editText.text.toString()
                    .toDouble()

            if (q >= 0)
                db.document(list[position].id).update("amount", q).addOnSuccessListener {
                    notifyDataSetChanged()
                }
            else
                Toast.makeText(context, "Enter quantity less than stock", Toast.LENGTH_SHORT).show()

            notifyDataSetChanged()
        }

        val dialog = builder.create()
        dialog.show()

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
