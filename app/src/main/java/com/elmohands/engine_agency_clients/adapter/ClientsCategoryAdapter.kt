package com.elmohands.engine_agency_clients.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.model.Clients
import com.elmohands.engine_agency_clients.model.Constant
import com.elmohands.engine_agency_clients.my_interface.CallBack

class ClientsCategoryAdapter() : RecyclerView.Adapter<ClientsCategoryAdapter.Holder>() {
    private var clients: List<Clients>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.client_item, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val client = clients?.get(position)
        holder.clientName.text= client?.name
        holder.clientNumber.text= client?.number
        holder.clientNote.text= client?.note
        holder.clientDate.text= client?.date
    }

    override fun getItemCount(): Int {
        if(clients!=null)
            return clients!!.size
        return 0
    }

    fun setClients(client: List<Clients>) {
        this.clients = client
        notifyDataSetChanged()
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName= itemView.findViewById<TextView>(R.id.taxName)!!
        val clientNumber= itemView.findViewById<TextView>(R.id.clientNumber)!!
        val clientDate= itemView.findViewById<TextView>(R.id.taxDateItem)!!
        val clientNote= itemView.findViewById<TextView>(R.id.taxNoteItem)!!
    }
}