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
import com.elmohands.engine_agency_clients.adapter.TaxAdapter
import com.elmohands.engine_agency_clients.model.Tax
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_clients.floatingActionButton
import kotlinx.android.synthetic.main.fragment_tax.*
import java.text.SimpleDateFormat
import java.util.*
class TaxFragments : Fragment() {
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
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        list = ArrayList()
        myCalendar = Calendar.getInstance()
        firebaseAuth= FirebaseAuth.getInstance()
        currentUserId=firebaseAuth!!.currentUser?.uid.toString()
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
                if(value?.documents!!.size==0){
                    list.clear()
                    itemCount=0
                    adapter.setTaxes(list)
                }
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
            dialog.setContentView(R.layout.dialog_tax)
            val window = dialog.window
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            textDate = dialog.findViewById<TextView>(R.id.taxDate)
            val textPrice = dialog.findViewById<EditText>(R.id.taxPrice)
            val textNote = dialog.findViewById<EditText>(R.id.taxNote)
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
            btnSave.setOnClickListener {
                val idDocument=collectionTaxes.document().id;
                val client= Tax(
                    (itemCount+1),
                    idDocument,
                    textPrice.text.toString(),
                    textNote.text.toString(),
                    textDate.text.toString()
                )
                //val map=HashMap<String, String>()
                collectionTaxes.document(idDocument).set(client).addOnCompleteListener {
                    list.add(client)
                    adapter.setTaxes(list.reversed())
                    dialog.cancel()
                    itemCount++
                }.addOnFailureListener { dialog.cancel() }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,   container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myContext= activity!!
        viewTaxes=inflater.inflate(R.layout.fragment_tax, container, false)
        return viewTaxes
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        textDate.text = sdf.format(myCalendar.time)
    }
}
