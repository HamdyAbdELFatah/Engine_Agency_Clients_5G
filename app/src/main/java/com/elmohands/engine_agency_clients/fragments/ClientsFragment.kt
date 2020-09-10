package com.elmohands.engine_agency_clients.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.ClientsAdapter
import com.elmohands.engine_agency_clients.model.Category
import com.elmohands.engine_agency_clients.model.Clients
import com.elmohands.engine_agency_clients.model.Constant
import com.elmohands.engine_agency_clients.my_interface.CallBack
import com.elmohands.engine_agency_clients.ui.UserHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_clients.*
import java.text.SimpleDateFormat
import java.util.*


class ClientsFragment : Fragment(),CallBack, Toolbar.OnMenuItemClickListener {
    private val TAG = "ClientsFragment"
    private lateinit var myCalendar: Calendar
    private lateinit var viewClients: View
    private lateinit var myContext: Context
    private lateinit var textDate: TextView
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    lateinit var toolbar: Toolbar
    var itemCount=0
    var itemSelectedCount=0
    lateinit var adapterClients : ClientsAdapter
    lateinit var listClients :ArrayList<Clients>
    lateinit var listSelectedClients :ArrayList<Clients>
    lateinit var listCategory :ArrayList<Category>
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    private lateinit var collectionCategory : CollectionReference
    private lateinit var collectionClients :CollectionReference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext=context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listClients = ArrayList()
        listSelectedClients = ArrayList()
        listCategory = ArrayList()
        myCalendar = Calendar.getInstance()
        firebaseAuth=FirebaseAuth.getInstance()
        currentUserId=firebaseAuth!!.currentUser?.uid.toString()
        collectionClients = collectionReference.document(currentUserId!!).collection("Clients")
        collectionCategory =db.collection("Category")

        clientsRecyclerView.layoutManager = LinearLayoutManager(myContext)
        adapterClients = ClientsAdapter(this@ClientsFragment)
        clientsRecyclerView.adapter = adapterClients
        val dialog =  Dialog(myContext)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()

        collectionClients.orderBy("id").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    listClients.add(document.toObject(Clients::class.java))
                }
                itemCount=listClients.size
                adapterClients.setClients(listClients.reversed())
                dialog.cancel()
                //Log.d(TAG, listClients.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }
            dialog.show()
            collectionCategory.orderBy("id").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        listCategory.add(document.toObject(Category::class.java))
                    }
                    dialog.cancel()
                } else {
                    Log.d(TAG, "Error getting documents: ", it.exception)
                }
                collectionCategory.whereGreaterThan("id",listCategory.size).addSnapshotListener { value, error ->
                    for(i in value?.documents!!){
                        listCategory.add(i.toObject(Category::class.java)!!)
                    }
                }
            }.addOnFailureListener { dialog.cancel() }
        }.addOnFailureListener { dialog.cancel() }

        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }
        floatingActionButton.setOnClickListener {
            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_clients)
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
                    (itemCount + 1),
                    idDocument,
                    clientName.text.toString(),
                    phoneNumber.text.toString(),
                    "0",
                    textNote.text.toString(),
                    textDate.text.toString(), "new"
                )
                //val map=HashMap<String, String>()
                collectionClients.document(idDocument).set(client).addOnSuccessListener {
                    listClients.add(client)
                    adapterClients.setClients(listClients.reversed())
                    dialog.cancel()
                    itemCount++
                }.addOnFailureListener { dialog.cancel() }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //myContext= activity!!
        toolbar = activity!!.findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setOnMenuItemClickListener(this)

        viewClients=inflater.inflate(R.layout.fragment_clients, container, false)
        return viewClients
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        textDate.text = sdf.format(myCalendar.time)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onPause() {
        super.onPause()
        Constant.statusSelectItem=false
        for(i in listClients.indices)
            listClients[i].checked=false
        adapterClients.setClients(listClients.reversed())
        adapterClients.notifyDataSetChanged()
        itemSelectedCount=0
        listSelectedClients.clear()
        toolbar.title = "Engine agency"
        //toolbar.menu.clear()
        toolbar.menu[0].isVisible = true
        toolbar.menu[1].isVisible = false
        //toolbar.inflateMenu(R.menu.menu_messages)
    }

    override fun onClick(check: Boolean,position:Clients) {
        itemSelectedCount=if(check)itemSelectedCount+1 else itemSelectedCount-1
        toolbar.title = " تم تحديد $itemSelectedCount"
        if(check)
            listSelectedClients.add(position)
        else
            listSelectedClients.remove(position)
    }

    override fun onLongClick(check: Boolean,position:Clients) {
        Constant.statusSelectItem=true
        itemSelectedCount=if(check)itemSelectedCount+1 else itemSelectedCount-1
        adapterClients.notifyDataSetChanged()
        (activity as UserHome).supportActionBar?.setHomeButtonEnabled(true)
        toolbar.title = " تم تحديد $itemSelectedCount"
        if(check)
            listSelectedClients.add(position)
        else
            listSelectedClients.remove(position)
//        toolbar.menu.clear()
//        toolbar.inflateMenu(R.menu.menu_selection_toolbar)
        toolbar.menu[0].isVisible = false
        toolbar.menu[1].isVisible = true

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_category)
        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ((resources.displayMetrics.heightPixels * 0.60).toInt())
            //ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val categoryRadioGroup = dialog.findViewById<RadioGroup>(R.id.categoryRadioGroup)
        //create radio buttons

        //create radio buttons
        for (i in 0 until listCategory.size) {
            val radioButton = RadioButton(myContext)
            radioButton.text = listCategory[i].categoryName
            categoryRadioGroup.addView(radioButton)
        }
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        dialog.show()

        btnCancel.setOnClickListener {dialog.cancel()}
        btnSave.setOnClickListener {
            var position =-1
            for(i in listCategory.indices){
                if((categoryRadioGroup[i] as RadioButton).isChecked)
                    position=i
            }
            if(position>=0){
                val categoryReference =collectionReference.document(currentUserId!!).collection("Category")
                    .document(listCategory[position].idDocument).
                    collection(listCategory[position].categoryName)

                for(i in listSelectedClients)
                    categoryReference.document(i.idDocument).set(i)
                Toast.makeText(myContext, " تم اضافة$itemSelectedCount", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(myContext, " من فضلك اختار تصنيف ", Toast.LENGTH_LONG).show()
            }
            dialog.cancel()
        }
        return false
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(Constant.statusSelectItem){
                Constant.statusSelectItem=false
                for(i in listClients.indices)
                    listClients[i].checked=false
                adapterClients.setClients(listClients.reversed())
                adapterClients.notifyDataSetChanged()
                itemSelectedCount=0
                toolbar.title = "Engine agency"
                //toolbar.menu.clear()
                listSelectedClients.clear()
                //toolbar.inflateMenu(R.menu.menu_messages)
                toolbar.menu[0].isVisible = true
                toolbar.menu[1].isVisible = false

            }else
                activity?.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Constant.statusSelectItem=false
        itemSelectedCount=0
        toolbar.title = "Engine agency"
        listSelectedClients.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        Constant.statusSelectItem=false
    }
}

