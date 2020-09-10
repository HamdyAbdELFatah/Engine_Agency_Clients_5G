package com.elmohands.engine_agency_clients.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.AdminViewPager
import com.elmohands.engine_agency_clients.adapter.CategoryAdapter
import com.elmohands.engine_agency_clients.model.Category
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_admin_control.*
import java.util.*
import kotlin.collections.HashMap


class AdminControl : AppCompatActivity() {
    private val TAG = "AdminControl"
    private val GALLERY_CODE = 1
    private var firebaseAuth: FirebaseAuth? = null
    private val user: FirebaseUser? = null
    var currentUserId: String? = null
    private var storageReference: StorageReference? = null
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Admin")
    private lateinit var collectionCategory : CollectionReference
    var imageProfile: CircleImageView? = null
    var imageBackground: ImageView? = null
    var userName: TextView? = null
    private var imageUri: Uri? = null
    private var drawerRecyclerView: RecyclerView? = null
    lateinit var list : ArrayList<Category>
    var itemCount=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_control)
        firebaseAuth=FirebaseAuth.getInstance()
        storageReference= FirebaseStorage.getInstance().reference
        currentUserId=firebaseAuth!!.currentUser?.uid.toString()
        collectionCategory =db.collection("Category")
        setSupportActionBar(toolbar)
        list = ArrayList()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawsvg)
        setupDrawer()
        initViewPager2WithFragments()
        val hView = navigationView.getHeaderView(0)
        userName = hView.findViewById(R.id.drawerUserName) as TextView
        imageProfile = hView.findViewById(R.id.drawerImageProfile)
        imageBackground = hView.findViewById(R.id.drawerBackgroundImage)
        drawerRecyclerView = hView.findViewById(R.id.drawerRecyclerView)
        val categoryHead = hView.findViewById(R.id.categoryHead) as TextView
        drawerRecyclerView?.layoutManager = LinearLayoutManager(this)
        val categoryAdapter = CategoryAdapter()
        drawerRecyclerView?.adapter = categoryAdapter
        val drawable = ContextCompat.getDrawable(this, R.drawable.add_category)
        categoryHead.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)


        categoryHead.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_add_category)
            val window = dialog.window
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val newCategoryName = dialog.findViewById<EditText>(R.id.newCategoryName)
            val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
            val btnSave = dialog.findViewById<Button>(R.id.btnSave)
            dialog.show()
            btnCancel.setOnClickListener {dialog.cancel()}
            val idDocument=collectionCategory.document().id
            btnSave.setOnClickListener {
                if(newCategoryName.text.toString().trim().isNotEmpty()){
                    val category= Category(
                        (itemCount + 1),
                        idDocument,
                        newCategoryName.text.toString()
                    )
                    collectionCategory.document(idDocument).set(category).addOnSuccessListener {
                        list.add(category)
                        categoryAdapter.setCategory(list)
                        dialog.cancel()
                        itemCount++
                    }.addOnFailureListener { dialog.cancel() }
                }else{
                    newCategoryName.error="من فضلك ادخل اسم التصنيف"
                }
            }
        }



        val dialog =  Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()
        collectionCategory.orderBy("id").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    list.add(document.toObject(Category::class.java))
                }
                itemCount=list.size
                categoryAdapter.setCategory(list)
                dialog.cancel()
                //Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }
        }.addOnFailureListener { dialog.cancel() }

        collectionReference.document(currentUserId!!).get().addOnCompleteListener {
            val map=it.result
            userName?.text= map?.get("userName") as CharSequence?
            Glide.with(this@AdminControl).load(map?.get("image").toString()).circleCrop().placeholder(
                R.drawable.photo
            )
                .error(R.drawable.photo).into(imageProfile!!)
            Glide.with(this@AdminControl).load(map?.get("image").toString()).optionalCenterCrop().placeholder(
                R.drawable.photo
            )
                .error(R.drawable.photo).into(
                    imageBackground!!
                )
        }

        imageProfile?.setOnClickListener {
            //get image from gallery/phone
//            val galleryIntent = Intent(Intent.ACTION_PICK)
//            galleryIntent.type = "image/*"
//            startActivityForResult(galleryIntent, GALLERY_CODE)
            CropImage.activity().start(this@AdminControl)

        }
    }

    private fun initViewPager2WithFragments() {
        val viewPager2: ViewPager2 = findViewById(R.id.ViewPager)
        val adapter = AdminViewPager(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tablayout)

        val names:Array<String> = arrayOf("العملاء", "المدفوعات", "المصروفات")
        TabLayoutMediator(tabLayout, viewPager2){ tab, position ->
            tab.text = names[position]
        }.attach()

    }



    private fun setupDrawer() {
        //drawerLayout = findViewById<View>(R.id.drawer_layout)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                //R.id.Dental_history -> startActivity(Intent(this, DentalPatientDetail::class.java))
                //R.id.Update -> startActivity(Intent(this, PatientProfile::class.java))
                //R.id.about -> startActivity(Intent(this, About::class.java))
                R.id.Logout -> {
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    FirebaseAuth.getInstance().signOut()
                    val editor = sharedPreferences?.edit()
                    editor?.putBoolean("login", false)
                    editor?.putString("type", "user")
                    editor?.apply()
                    finish()
                }

            }
            true
        }
        val actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        ) {
        }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) { val path = storageReference?.child("profile_images")
                ?.child("engine_agency_clients_" + Timestamp.now().seconds)
                val result = CropImage.getActivityResult(data)
                imageUri = result.uri
                val dialog =  Dialog(this)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setContentView(R.layout.loading_bar)
                dialog.show()
                var urlImage=""
                path?.putFile(imageUri!!)?.addOnSuccessListener {
                    path.downloadUrl.addOnSuccessListener {
                        urlImage=it.toString()
                        Glide.with(this@AdminControl).load(urlImage).circleCrop().placeholder(R.drawable.photo)
                            .error(R.drawable.photo).into(imageProfile!!)
                        Glide.with(this@AdminControl).load(urlImage).optionalCenterCrop().placeholder(R.drawable.photo)
                            .error(R.drawable.photo).into(
                                imageBackground!!
                            )
                    }.addOnCompleteListener {
                        val map=HashMap<String, String>()
                        map["image"]=urlImage
                        collectionReference.document(currentUserId!!).update(map as Map<String, Any>)
                        dialog.cancel()
                    }.addOnFailureListener { dialog.cancel() }
                }

               // Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
