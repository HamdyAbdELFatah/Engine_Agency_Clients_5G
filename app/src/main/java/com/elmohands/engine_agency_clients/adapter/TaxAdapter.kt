package com.elmohands.engine_agency_clients.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.model.Tax

class TaxAdapter : RecyclerView.Adapter<TaxAdapter.Holder>() {
    private var taxs: List<Tax>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.tax_item, parent, false)
        return Holder(itemView)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val client = taxs?.get(position)
        holder.taxPriceItem.text= client?.price
        holder.taxNoteItem.text= client?.note
        holder.taxDateItem.text= client?.date
    }
    override fun getItemCount(): Int {
        if(taxs!=null)
            return taxs!!.size
        return 0
    }
    fun setTaxes(tax: List<Tax>) {
        this.taxs = tax
        notifyDataSetChanged()
    }
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taxPriceItem= itemView.findViewById<TextView>(R.id.taxPriceItem)!!
        val taxDateItem= itemView.findViewById<TextView>(R.id.taxDateItem)!!
        val taxNoteItem= itemView.findViewById<TextView>(R.id.taxNoteItem)!!
    }
}