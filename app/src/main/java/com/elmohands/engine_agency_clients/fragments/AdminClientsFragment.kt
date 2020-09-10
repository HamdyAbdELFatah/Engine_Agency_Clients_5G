package com.elmohands.engine_agency_clients.fragments
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.ClientsAdapter
import com.elmohands.engine_agency_clients.adapter.ClientsCategoryAdapter
import com.elmohands.engine_agency_clients.adapter.UserListAdapter
import com.elmohands.engine_agency_clients.model.Category
import com.elmohands.engine_agency_clients.model.Clients
import com.elmohands.engine_agency_clients.model.Constant
import com.elmohands.engine_agency_clients.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_admin_clients.*
import java.util.*

class AdminClientsFragment : Fragment() {
    private val TAG = "AdminClientsFragment"
    private lateinit var myCalendar: Calendar
    private lateinit var viewClients: View
    private lateinit var myContext: Context
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var itemCount=0
    lateinit var list :ArrayList<Clients>
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    private lateinit var collectionClients :CollectionReference
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        list = ArrayList()
        myCalendar = Calendar.getInstance()
        firebaseAuth=FirebaseAuth.getInstance()
        currentUserId=Constant.userId
        collectionClients = collectionReference.document(currentUserId!!).collection("Clients")
        clientsRecyclerView.layoutManager = LinearLayoutManager(myContext)
        val adapter = ClientsCategoryAdapter()
        clientsRecyclerView.adapter = adapter
        val dialog =  Dialog(myContext)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()
        collectionClients.orderBy("id").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    list.add(document.toObject(Clients::class.java))
                }
                itemCount=list.size
                adapter.setClients(list.reversed())
                dialog.cancel()
                //Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }

            collectionClients.whereGreaterThan("id",list.size).addSnapshotListener { value, error ->
                for(i in value?.documents!!){
                    list.add(i.toObject(Clients::class.java)!!)
                }
                adapter.setClients(list.distinct().reversed())
            }
        }.addOnFailureListener { dialog.cancel() }

    }

    override fun onCreateView(inflater: LayoutInflater,   container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myContext= activity!!
        viewClients=inflater.inflate(R.layout.fragment_admin_clients, container, false)
        return viewClients
    }

}