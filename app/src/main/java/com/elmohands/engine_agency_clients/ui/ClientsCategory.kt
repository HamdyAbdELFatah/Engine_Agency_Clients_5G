package com.elmohands.engine_agency_clients.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.ClientsCategoryAdapter
import com.elmohands.engine_agency_clients.model.Clients
import com.elmohands.engine_agency_clients.model.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_clients_category.*
import java.util.*

class ClientsCategory : AppCompatActivity() {
    private val TAG = "ClientsCategory"
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var itemCount=0
    lateinit var list :ArrayList<Clients>
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients_category)
        supportActionBar?.title = "Engine agency"
        val intent =intent
        val categoryName=intent.getStringExtra("categoryName")
        val idDocument=intent.getStringExtra("idDocument")
        list = ArrayList()
        firebaseAuth= FirebaseAuth.getInstance()
        val sharedPreferences = getSharedPreferences("engine_agency_clients", MODE_PRIVATE)
        currentUserId= if(sharedPreferences.getString("type", "").equals("user"))
            firebaseAuth!!.currentUser?.uid.toString()
        else
            Constant.userId
        val categoryReference =collectionReference.document(currentUserId!!).collection("Category")
            .document(idDocument.toString()).
            collection(categoryName.toString())
        categoryListClients.layoutManager = LinearLayoutManager(this)
        val adapter = ClientsCategoryAdapter()
        categoryListClients.adapter = adapter
        val dialog =  Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()
        categoryReference.orderBy("id").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    list.add(document.toObject(Clients::class.java))
                }
                itemCount=list.size
                adapter.setClients(list.reversed())
                dialog.cancel()
                Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }
        }.addOnFailureListener { dialog.cancel() }
    }
}