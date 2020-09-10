package com.elmohands.engine_agency_clients.fragments
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.TaxAdapter
import com.elmohands.engine_agency_clients.model.Constant
import com.elmohands.engine_agency_clients.model.Tax
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_clients.floatingActionButton
import kotlinx.android.synthetic.main.fragment_tax.*
import java.util.*


class AdminTaxFragments : Fragment() {
    private val TAG = "TaxFragments"
    private lateinit var myCalendar: Calendar
    private lateinit var viewTaxes: View
    private lateinit var myContext: Context
    private lateinit var textDate: TextView
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var itemCount=0
    lateinit var list : ArrayList<Tax>
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    private lateinit var collectionTaxes : CollectionReference
    private var delete: Boolean=false
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        list = ArrayList()
        myCalendar = Calendar.getInstance()
        firebaseAuth= FirebaseAuth.getInstance()
        currentUserId= Constant.userId
        collectionTaxes = collectionReference.document(currentUserId!!).collection("Taxes")
        taxRecyclerView.layoutManager = LinearLayoutManager(myContext)
        val adapter = TaxAdapter()
        taxRecyclerView.adapter = adapter
        val dialog =  Dialog(myContext)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()

        collectionTaxes.orderBy("id").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    list.add(document.toObject(Tax::class.java))
                }
                itemCount=list.size
                adapter.setTaxes(list.reversed())
                dialog.cancel()
                //Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }
            collectionTaxes.addSnapshotListener { value, error ->
                if(!delete){
                    for(i in value?.documents!!){
                        list.add(i.toObject(Tax::class.java)!!)
                        println(i.toObject(Tax::class.java)!!)

                    }
                    adapter.setTaxes(list.distinct().reversed())
                }
            }
        }.addOnFailureListener { dialog.cancel() }

        floatingActionButton.setOnClickListener {

            AlertDialog.Builder(context)
                .setTitle("حذف")
                .setMessage("هل انت متاكد انك تريد حذف جميع المصروفات؟")
                .setPositiveButton(
                    "نعم"
                ) { dialog, which ->
                    val dialog1 =  Dialog(myContext)
                    dialog1.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog1.setContentView(R.layout.loading_bar)
                    dialog1.show()
                    var last: Tax? =null
                    delete=true
                    if(list.isNotEmpty()) {
                        last = list.last()
                    }else{
                        dialog1.cancel()
                        delete=false
                    }
                    for(i in list) {
                        collectionTaxes.document(i.idDocument).delete().addOnCompleteListener {
                            if(i==last){
                                list.clear()
                                adapter.setTaxes(list)
                                dialog1.cancel()
                                delete=false
                            }
                        }.addOnFailureListener{dialog1.cancel()
                            delete=false
                        }
                    }

                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("لا", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext= activity!!
        viewTaxes=inflater.inflate(R.layout.fragment_admin_tax, container, false)
        return viewTaxes
    }

}
