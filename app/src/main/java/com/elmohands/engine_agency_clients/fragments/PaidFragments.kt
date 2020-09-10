package com.elmohands.engine_agency_clients.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.ClientsAdapter
import com.elmohands.engine_agency_clients.adapter.PaidAdapter
import com.elmohands.engine_agency_clients.model.Clients
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_paid.*
import java.text.SimpleDateFormat
import java.util.*
class PaidFragments : Fragment() {
    private val TAG = "PaidFragments"
    private lateinit var myCalendar: Calendar
    private lateinit var viewClients: View
    private lateinit var myContext: Context
    private lateinit var textDate: TextView
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var itemCount=0
    lateinit var list : ArrayList<Clients>
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    private lateinit var collectionClients : CollectionReference
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        list = ArrayList()
        myCalendar = Calendar.getInstance()
        firebaseAuth= FirebaseAuth.getInstance()
        currentUserId=firebaseAuth!!.currentUser?.uid.toString()
        collectionClients = collectionReference.document(currentUserId!!).collection("Paid")
        paidRecyclerView.layoutManager = LinearLayoutManager(myContext)
        val adapter = PaidAdapter()
        paidRecyclerView.adapter = adapter
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
        }.addOnFailureListener { dialog.cancel() }

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        floatingActionButton.setOnClickListener {
            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_paid)
            val window = dialog.window
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            textDate = dialog.findViewById<TextView>(R.id.taxDate)
            val clientName = dialog.findViewById<EditText>(R.id.taxName)
            val phoneNumber = dialog.findViewById<EditText>(R.id.phoneNumber)
            val textNote = dialog.findViewById<EditText>(R.id.taxNote)
            val taxPrice = dialog.findViewById<EditText>(R.id.taxPrice)
            val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
            val btnSave = dialog.findViewById<Button>(R.id.btnSave)
            dialog.show()
            btnCancel.setOnClickListener {dialog.cancel()}
            textDate.setOnClickListener {
                DatePickerDialog(
                    context!!, date, myCalendar.get(Calendar.YEAR), myCalendar.get(
                        Calendar.MONTH
                    ), myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            val idDocument=collectionClients.document().id
            btnSave.setOnClickListener {
                val client=Clients(
                    (itemCount+1),
                    idDocument,
                    clientName.text.toString(),
                    phoneNumber.text.toString(),
                    taxPrice.text.toString(),
                    textNote.text.toString(),
                    textDate.text.toString(), "new"
                )
                //val map=HashMap<String, String>()
                collectionClients.document(idDocument).set(client).addOnSuccessListener {
                    list.add(client)
                    adapter.setClients(list.reversed())
                    dialog.cancel()
                    itemCount++
                }.addOnFailureListener { dialog.cancel() }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,   container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myContext= activity!!
        viewClients=inflater.inflate(R.layout.fragment_paid, container, false)
        return viewClients
    }
    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        textDate.text = sdf.format(myCalendar.time)
    }
}
