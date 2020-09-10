package com.elmohands.engine_agency_clients.ui

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.adapter.CategoryAdapter
import com.elmohands.engine_agency_clients.adapter.UsersViewPager
import com.elmohands.engine_agency_clients.model.Category
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_user_home.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class UserHome : AppCompatActivity(){
    private val TAG = "UserHome"
    private val GALLERY_CODE = 1
    private var firebaseAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    private var storageReference: StorageReference? = null
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    private lateinit var collectionCategory : CollectionReference
    var sharedPreferences: SharedPreferences? = null
    var imageProfile: CircleImageView? = null
    var imageBackground: ImageView? = null
    private var userName: TextView? = null
    private var drawerRecyclerView: RecyclerView? = null
    private var imageUri: Uri? = null
    lateinit var list : ArrayList<Category>
    var itemCount=0
    var itemMessagesBadgeTextView: TextView? = null
    var badgeLayout: View? = null
    var itemMessages: MenuItem? = null

    var iconButtonMessages: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        firebaseAuth=FirebaseAuth.getInstance()
        storageReference= FirebaseStorage.getInstance().reference
        currentUserId=firebaseAuth!!.currentUser?.uid.toString()
        list = ArrayList()

        sharedPreferences = getSharedPreferences("engine_agency_clients", MODE_PRIVATE)
        collectionCategory =db.collection("Category")
        setSupportActionBar(toolbar)
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
        drawerRecyclerView?.layoutManager = LinearLayoutManager(this)
        val categoryAdapter = CategoryAdapter()
        drawerRecyclerView?.adapter = categoryAdapter

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
            collectionCategory.whereGreaterThan("id",list.size).addSnapshotListener { value, error ->
                for(i in value?.documents!!){
                    list.add(i.toObject(Category::class.java)!!)
                }
                categoryAdapter.setCategory(list.distinct())
            }
        }.addOnFailureListener { dialog.cancel() }

        collectionReference.document(currentUserId!!).get().addOnCompleteListener {
            val map=it.result
            userName?.text= map?.get("userName") as CharSequence?
            Glide.with(this@UserHome).load(map?.get("image").toString()).circleCrop().placeholder(R.drawable.photo)
                .error(R.drawable.photo).into(imageProfile!!)
            Glide.with(this@UserHome).load(map?.get("image").toString()).optionalCenterCrop().placeholder(R.drawable.photo)
                .error(R.drawable.photo).into(
                    imageBackground!!
                )
        }

        imageProfile?.setOnClickListener {
            //get image from gallery/phone
//            val galleryIntent = Intent(Intent.ACTION_PICK)
//            galleryIntent.type = "image/*"
//            startActivityForResult(galleryIntent, GALLERY_CODE)
            CropImage.activity().start(this@UserHome)

        }

    }

    private fun initViewPager2WithFragments() {
        val viewPager2: ViewPager2 = findViewById(R.id.ViewPager)
        val adapter = UsersViewPager(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val names:Array<String> = arrayOf("العملاء", "المدفوعات", "المصروفات")
        TabLayoutMediator(tabLayout, viewPager2){ tab, position ->
            tab.text = names[position]
        }.attach()

    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.drawerImageProfile -> {

            }
        }
    }

    private fun setupDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Logout -> {
                    val intent = Intent(this, Login::class.java)
                    FirebaseAuth.getInstance().signOut()
                    val editor = sharedPreferences?.edit()
                    editor?.putBoolean("login", false)
                    editor?.putString("type", "user")
                    editor?.apply()
                    startActivity(intent)
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
                        Glide.with(this@UserHome).load(urlImage).circleCrop().placeholder(R.drawable.photo)
                            .error(R.drawable.photo).into(imageProfile!!)
                        Glide.with(this@UserHome).load(urlImage).optionalCenterCrop()
                            .placeholder(R.drawable.photo)
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
            }
        }
    }


    var countNotification=0
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_messages, menu)
        itemMessages = menu.findItem(R.id.menu_messages)
        badgeLayout = itemMessages?.actionView
        itemMessagesBadgeTextView = badgeLayout?.findViewById(R.id.badge_textView) as TextView
        itemMessagesBadgeTextView?.visibility = View.GONE // initially hidden
        iconButtonMessages = badgeLayout?.findViewById(R.id.badge_icon_button) as ImageView
        iconButtonMessages?.setOnClickListener{
            val i = Intent(
                this,
                NotificationActivity::class.java

            )
            countNotification=0
            itemMessagesBadgeTextView?.visibility = View.GONE // initially hidden
            startActivity(i)
        }
        return true
    }
    override fun onStart() {
        super.onStart()
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show()
        val messagesReference = collectionReference.document(currentUserId!!).collection("Messages")
        messagesReference.orderBy("id").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    if (document["seen"]?.equals("no")!!)
                        countNotification++
                }
                if (countNotification > 0) {
                    itemMessagesBadgeTextView?.visibility = View.VISIBLE
                    itemMessagesBadgeTextView?.text = "$countNotification"
                }
                dialog.cancel()
                //Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", it.exception)
            }
        }.addOnFailureListener { dialog.cancel() }
        var count = 0
        messagesReference.whereEqualTo("seen", "no").addSnapshotListener{ value, error ->
            if (error != null)
                return@addSnapshotListener
            itemMessagesBadgeTextView?.visibility = View.VISIBLE
            itemMessagesBadgeTextView?.text = "${value?.size()}"
            if(value?.size()==0)
                itemMessagesBadgeTextView?.visibility = View.GONE

        }
    }

}