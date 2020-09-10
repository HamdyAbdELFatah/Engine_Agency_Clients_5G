package com.elmohands.engine_agency_clients.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.model.Constant
import com.elmohands.engine_agency_clients.model.Tax
import com.elmohands.engine_agency_clients.model.User
import com.elmohands.engine_agency_clients.ui.AdminControl
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.Holder>() {
    private var users: List<User>? = null
    lateinit var  myContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_details, parent, false)
        myContext=parent.context
        return Holder(itemView)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = users?.get(position)
        holder.userNameAdminHome.text= user?.userName
        if(user?.image.isNullOrEmpty())
            holder.userImageAdminHome.setImageResource(R.drawable.photo)
        else
            Glide.with(myContext).load(user?.image).circleCrop().placeholder(R.drawable.photo).error(R.drawable.photo).into(holder.userImageAdminHome)

        holder.cardUser.setOnClickListener {
            val intent=Intent(myContext,AdminControl::class.java)
            //intent.putExtra("userId", user?.userId)
            Constant.userId=user?.userId
            //Toast.makeText(myContext, Constant.userId+"", Toast.LENGTH_SHORT).show()
            myContext.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        if(users!=null)
            return users!!.size
        return 0
    }

    fun setUsers(user: List<User>) {
        this.users = user
        notifyDataSetChanged()
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameAdminHome= itemView.findViewById<TextView>(R.id.userNameAdminHome)!!
        val userImageAdminHome= itemView.findViewById<CircleImageView>(R.id.userImageAdminHome)!!
        val cardUser= itemView.findViewById<CardView>(R.id.cardUser)!!
    }
}