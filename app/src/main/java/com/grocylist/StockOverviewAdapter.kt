package com.grocylist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.internal.bind.util.ISO8601Utils.format
import java.lang.String.format
import java.text.DateFormat
import java.text.MessageFormat.format
import java.text.SimpleDateFormat
import java.util.Date

class StockOverviewAdapter(
    data: MutableList<DocumentSnapshot>,
    stockOverviewActivity: StockOverviewActivity
) : RecyclerView.Adapter<StockOverviewAdapter.StockOverviewViewHolder>() {
    var list: MutableList<DocumentSnapshot>
    var context = stockOverviewActivity

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

    @SuppressLint("MissingInflatedId")
    override fun onBindViewHolder(holder: StockOverviewViewHolder, position: Int) {
        holder.name.text = list[position].data?.get("name").toString()
        holder.qty.text = "${list[position].data?.get("amount").toString()} ${
            list[position].data?.get("qty").toString()
        }"
        if (list[position].data?.get("expiry_date") != null)
            holder.expiry.text =
                (list[position].data?.get("expiry_date") as Timestamp).toDate().toString()

        holder.card.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.stock_bottomsheet_layout, null)
            dialog.setContentView(view)
            dialog.show()
            view.findViewById<TextView>(R.id.title).text =
                list[position].data?.get("name").toString()
            view.findViewById<TextView>(R.id.amount).text =
                list[position].data?.get("price").toString()
            if (list[position].data?.get("expiry_date") != null) {
                val date = (list[position].data?.get("expiry_date") as Timestamp).toDate()
                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val newdate = sdf.format(date)
                view.findViewById<TextView>(R.id.expiry_date).text =
                    newdate.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}

class ModalBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.stock_bottomsheet_layout, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}