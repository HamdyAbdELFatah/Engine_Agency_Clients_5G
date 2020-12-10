package com.elmohands.engine_agency_clients.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.model.Clients
import com.elmohands.engine_agency_clients.model.Constant
import com.elmohands.engine_agency_clients.my_interface.CallBack


class ClientsAdapter(callBack: CallBack) : RecyclerView.Adapter<ClientsAdapter.Holder>() {
    var myCallBack=callBack
    private var clients: List<Clients>? = null
    lateinit var mContext : Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mContext=parent.context
        val itemView: View = LayoutInflater.from(mContext)
            .inflate(R.layout.client_item, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val client = clients?.get(position)
        holder.clientName.text= client?.name
        holder.clientNumber.text= client?.number
        holder.clientNote.text= client?.note
        if( !client?.image.isNullOrEmpty()) {
            holder.clientPhoto.text = "اضغط لعرض الصورة"
        }else
            holder.clientPhoto.text = "لا يوجد صورة"
        holder.clientDate.text= client?.date
        holder.clientCard.setOnClickListener {
            if(Constant.statusSelectItem) {
                client?.checked = !(client?.checked)!!
                holder.checkBoxClients.isChecked = client.checked
                myCallBack.onClick(client.checked, client)
            }
        }
        holder.clientPhoto.setOnClickListener {
            if( !client?.image.equals("")) {
                val dialog= Dialog(mContext)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.image_viewer_dialog)
                val window = dialog.window
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                window.setGravity(Gravity.CENTER)
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(true)
                val imageViewer = dialog.findViewById<ImageView>(R.id.imageViewer)
                val imageViewerProgress = dialog.findViewById<ProgressBar>(R.id.imageViewerProgress)
                dialog.setOnShowListener {
                    Glide.with(mContext).load(client?.image).listener(
                        object : RequestListener<Drawable> {
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                imageViewerProgress.visibility = View.GONE
                                return false
                            }

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                imageViewerProgress.visibility = View.GONE
                                Toast.makeText(mContext, "فشل التحميل", Toast.LENGTH_SHORT).show()
                                return false
                            }
                        }
                    ).optionalCenterCrop()
                        .error(R.drawable.photo).into(imageViewer)
                }
                dialog.show()
            }else{
                Toast.makeText(mContext, "لا يوجد صورة", Toast.LENGTH_SHORT).show()
            }
        }
        holder.checkBoxClients.setOnClickListener {
            client?.checked = !(client?.checked)!!
            holder.checkBoxClients.isChecked = client.checked
            myCallBack.onClick(client.checked, client)
        }
        holder.clientCard.setOnLongClickListener {
            client?.checked = !(client?.checked)!!
            holder.checkBoxClients.isChecked = client.checked
            myCallBack.onLongClick(client.checked, client)
            false
        }
        //holder.checkBoxClients.visibility = View.VISIBLE
        if(Constant.statusSelectItem){
            holder.checkBoxClients.isChecked = client?.checked!!
            holder.checkBoxClients.visibility = View.VISIBLE
        }else{
            holder.checkBoxClients.visibility = View.GONE
        }
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
        val clientCard= itemView.findViewById<CardView>(R.id.clientCard)!!
        val checkBoxClients= itemView.findViewById<CheckBox>(R.id.checkBoxClients)!!
        val clientName= itemView.findViewById<TextView>(R.id.taxName)!!
        val clientNumber= itemView.findViewById<TextView>(R.id.clientNumber)!!
        val clientDate= itemView.findViewById<TextView>(R.id.taxDateItem)!!
        val clientNote= itemView.findViewById<TextView>(R.id.taxNoteItem)!!
        val clientPhoto= itemView.findViewById<TextView>(R.id.clientPhoto)!!
    }
}