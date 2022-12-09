package com.grocylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class StockOverviewAdapter(data: MutableList<DocumentSnapshot>) : RecyclerView.Adapter<StockOverviewAdapter.StockOverviewViewHolder>() {
    lateinit var list: MutableList<DocumentSnapshot>

    init {
        list = data
    }

    inner class StockOverviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.item_name)
        val qty: TextView = itemView.findViewById(R.id.item_qty)
        val expiry: TextView = itemView.findViewById(R.id.item_expiry)
        val card: MaterialCardView = itemView.findViewById(R.id.item_card)
        val con = itemView.context as StockOverviewActivity
    }

//    fun getActivity(){
//        con = activity
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOverviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_recyclerview_item, parent, false)
        return StockOverviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockOverviewViewHolder, position: Int) {
        holder.name.text = list[position].data?.get("name").toString()
        holder.qty.text = "${list[position].data?.get("amount").toString()} ${list[position].data?.get("qty").toString()}"
        if (list[position].data?.get("expiry_date") != null)
            holder.expiry.text = (list[position].data?.get("expiry_date") as Timestamp).toDate().toString()


    }

    override fun getItemCount(): Int {
        return list.size
    }
}