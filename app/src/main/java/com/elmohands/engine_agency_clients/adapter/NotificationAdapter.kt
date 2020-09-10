package com.elmohands.engine_agency_clients.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.model.Message
import com.elmohands.engine_agency_clients.model.User


class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.Holder>() {
    private var messages: List<Message>? = null
    lateinit var  myContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)
        myContext=parent.context
        return Holder(itemView)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val message = messages?.get(position)
        holder.messageText.text= message?.messageText
        if(message?.image.isNullOrEmpty())
            holder.messageImage.setImageResource(R.drawable.photo)
        else
            Glide.with(myContext).load(message?.image).placeholder(R.drawable.photo).error(R.drawable.photo).into(holder.messageImage)
    }
    override fun getItemCount(): Int {
        if(messages!=null)
            return messages!!.size
        return 0
    }

    fun setMessages(messages: List<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageImage= itemView.findViewById<ImageView>(R.id.messageImage)!!
        val messageText= itemView.findViewById<TextView>(R.id.messageText)!!
    }
}