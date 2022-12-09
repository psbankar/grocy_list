package com.grocylist

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShoppingListAdapter(data: MutableList<DocumentSnapshot>) :
    RecyclerView.Adapter<ShoppingListAdapter.ShoppingListVH>() {

    var list: MutableList<DocumentSnapshot>

    init {
        list = data
    }

    inner class ShoppingListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.item_name)
        val qty: TextView = itemView.findViewById(R.id.item_qty)
        val expiry: TextView = itemView.findViewById(R.id.item_expiry)
        val card: MaterialCardView = itemView.findViewById(R.id.item_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_recyclerview_item, parent, false)
        return ShoppingListVH(view)
    }

    override fun onBindViewHolder(holder: ShoppingListVH, position: Int) {
        holder.name.text = list[position].data?.get("name").toString()
        holder.qty.text = "${list[position].data?.get("amount").toString()} ${
            list[position].data?.get("qty").toString()
        }"

        if (list[position].data?.get("checked") == true) {
            holder.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.qty.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        } else {
            holder.name.paintFlags = 0
            holder.qty.paintFlags = 0
        }
        holder.card.setOnLongClickListener {
            if (list[position].data?.get("checked") == false) {
                holder.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                holder.qty.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                Firebase.firestore.collection("shopping_list").document(list[position].id).update(
                    mapOf( "checked" to true )
                )
            } else {
                holder.name.paintFlags = 0
                holder.qty.paintFlags = 0
                Firebase.firestore.collection("shopping_list").document(list[position].id).update(
                    mapOf( "checked" to false ))
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}