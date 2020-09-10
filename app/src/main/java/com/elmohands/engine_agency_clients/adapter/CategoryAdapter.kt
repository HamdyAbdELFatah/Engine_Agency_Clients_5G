package com.elmohands.engine_agency_clients.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.model.Category
import com.elmohands.engine_agency_clients.ui.ClientsCategory

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.Holder>() {
    private var categories: List<Category>? = null
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context=parent.context
        val itemView: View = LayoutInflater.from(context)
            .inflate(R.layout.category_item, parent, false)
        return Holder(itemView)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val category = categories?.get(position)
        holder.categoryItem.text= category?.categoryName
        holder.categoryContainer.setOnClickListener {
            val intent=Intent(context,ClientsCategory::class.java)
            intent.apply {
                putExtra("categoryName",category?.categoryName)
                putExtra("idDocument",category?.idDocument)
            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        if(categories!=null)
            return categories!!.size
        return 0
    }
    fun setCategory(category: List<Category>) {
        this.categories = category
        notifyDataSetChanged()
    }
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryItem= itemView.findViewById<TextView>(R.id.categoryItem)!!
        val categoryContainer= itemView.findViewById<ConstraintLayout>(R.id.categoryContainer)!!
    }
}