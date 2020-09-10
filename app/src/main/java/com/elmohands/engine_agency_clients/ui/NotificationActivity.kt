package com.elmohands.engine_agency_clients.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.NotificationAdapter
import com.elmohands.engine_agency_clients.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_notification.*
import java.util.*
import kotlin.collections.HashMap

class NotificationActivity : AppCompatActivity() {
    private val TAG = "NotificationActivity"
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    lateinit var list :ArrayList<Message>
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        supportActionBar?.title = "Engine agency"
        list = ArrayList()
        firebaseAuth= FirebaseAuth.getInstance()
        currentUserId=firebaseAuth!!.currentUser?.uid.toString()
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = NotificationAdapter()
        notificationRecyclerView.adapter = adapter
        val dialog =  Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()
        val messagesReference = collectionReference.document(currentUserId!!).collection("Messages")
        messagesReference.orderBy("id").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    list.add(document.toObject(Message::class.java))
                }
                adapter.setMessages(list.reversed())
                dialog.cancel()
                //Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }
            val m=HashMap<String,String>()
            m["seen"]="yes"
            for(i in list)
                messagesReference.document(i.idDocument).update(m as Map<String, Any>)
        }.addOnFailureListener { dialog.cancel() }
    }
}