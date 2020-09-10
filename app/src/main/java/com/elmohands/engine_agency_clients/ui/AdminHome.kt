package com.elmohands.engine_agency_clients.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.UserListAdapter
import com.elmohands.engine_agency_clients.model.Clients
import com.elmohands.engine_agency_clients.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_home.*
import java.util.*

class AdminHome : AppCompatActivity() {
    private val TAG = "AdminHome"
    private lateinit var myCalendar: Calendar
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var itemCount=0
    lateinit var list :ArrayList<User>
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        supportActionBar?.title = "Engine agency"
        list = ArrayList()
        myCalendar = Calendar.getInstance()
        firebaseAuth= FirebaseAuth.getInstance()
        currentUserId=firebaseAuth!!.currentUser?.uid.toString()
        userListRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = UserListAdapter()
        userListRecyclerView.adapter = adapter
        val dialog =  Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()
        collectionReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    list.add(document.toObject(User::class.java))
                }
                itemCount=list.size
                adapter.setUsers(list)
                dialog.cancel()
                Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }
        }.addOnFailureListener { dialog.cancel() }

        floatingActionButton.setOnClickListener {
            if(list.size>0)
                startActivity(Intent(this@AdminHome,MessageActivity::class.java)
                    .putParcelableArrayListExtra("listUser", list))
            else
                Toast.makeText(this, "لا يوجد اعضاء في الفريق", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout_menu, menu)
        val itemMessages = menu.findItem(R.id.signout)
        val sharedPreferences = getSharedPreferences("engine_agency_clients", MODE_PRIVATE)

        itemMessages?.setOnMenuItemClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            FirebaseAuth.getInstance().signOut()
            val editor = sharedPreferences?.edit()
            editor?.putBoolean("login", false)
            editor?.putString("type", "admin")
            editor?.apply()
            finish()
            true
        }
        return true
    }
}