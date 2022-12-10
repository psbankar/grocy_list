package com.grocylist

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShoppingListAdapter(shoppingListActivity: ShoppingListActivity) :
    RecyclerView.Adapter<ShoppingListAdapter.ShoppingListVH>() {


    val db: CollectionReference = Firebase.firestore.collection("shopping_list")
    lateinit var list: MutableList<DocumentSnapshot>
    val shoppingListActivity: ShoppingListActivity
    init {
        loadDB()
        this.shoppingListActivity = shoppingListActivity
    }

    fun loadDB() {
        db.addSnapshotListener { value, error ->
            list = value!!.documents
        }
    }

    inner class ShoppingListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.item_name)
        val qty: TextView = itemView.findViewById(R.id.item_qty)
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
        holder.card.setOnClickListener {
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

        holder.card.setOnLongClickListener {
            val i = Intent(shoppingListActivity, AddToShoppingListActivity::class.java)
            i.putExtra("name", list[position].data?.get("name").toString())
            i.putExtra("amount", list[position].data?.get("amount").toString())
            i.putExtra("qty", list[position].data?.get("qty").toString())
            i.putExtra("documentID", list[position].id)
            shoppingListActivity.resultLauncher.launch(i)
//            shoppingListActivity.startActivity(i)
            true

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun clearChecked() {

        val temp = list.iterator()
        var c = -1
        while (temp.hasNext()){
            val item = temp.next()
            c+=1
            if(item.data?.get("checked") == true){
                temp.remove()
                item.reference.delete()
                notifyDataSetChanged()
//                notifyItemRemoved(c)
            }

        }

    }

}
