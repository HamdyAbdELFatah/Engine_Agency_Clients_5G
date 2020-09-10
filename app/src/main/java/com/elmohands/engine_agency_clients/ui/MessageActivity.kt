package com.elmohands.engine_agency_clients.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.elmohands.engine_agency_clients.R
import com.elmohands.engine_agency_clients.model.Constant
import com.elmohands.engine_agency_clients.model.Message
import com.elmohands.engine_agency_clients.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_message.*
import java.util.ArrayList

class MessageActivity : AppCompatActivity() {
    private val TAG = "MessageActivity"
    private val GALLERY_CODE = 1
    private var storageReference: StorageReference? = null
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")
    var itemCount=0
    var urlImage=""
    lateinit var list : ArrayList<Message>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        storageReference= FirebaseStorage.getInstance().reference
        val intent = intent
        val users = intent.getParcelableArrayListExtra<User>("listUser")
        if(users?.size!! >0) {
            val dialog = Dialog(this)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.loading_bar)
            dialog.show()
            list = ArrayList()
            collectionReference.document(users[0].userId).collection("Messages").
            get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        list.add(document.toObject(Message::class.java))
                    }
                    itemCount = list.size
                    dialog.cancel()
                    // Log.d(TAG, list.toString())
                } else {
                    Log.d(TAG, "Error getting documents: ", it.exception)
                }
            }.addOnFailureListener { dialog.cancel() }

            imageView?.setOnClickListener {
                //get image from gallery/phone
                CropImage.activity().start(this@MessageActivity)
                list = ArrayList()
            }
            val idDocument = collectionReference.document(users[0].userId).collection("Messages").document().id
            sendButton.setOnClickListener {
                if (editTextTextMultiLine.text.toString().trim().isNotEmpty()) {
                    val message = Message(
                        (itemCount + 1),
                        idDocument,
                        urlImage,
                        "no",
                        editTextTextMultiLine.text.toString()
                    )
                    for (i in users)
                        collectionReference.document(i.userId).collection("Messages").document(idDocument)
                            .set(message).addOnSuccessListener {
                             if(i==users.last())   {
                                dialog.cancel()
                                finish()
                                Toast.makeText(this@MessageActivity, "تم الارسال بنجاح", Toast.LENGTH_SHORT).show()
                             }
                        }.addOnFailureListener { dialog.cancel() }
                    } else {
                        editTextTextMultiLine.error = "من فضلك ادخل نص الرسالة"
                    }
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) { val path = storageReference?.child("message_images")
                ?.child("engine_agency_clients_" + Timestamp.now().seconds)
                val result = CropImage.getActivityResult(data)
                val imageUri = result.uri
                try {
                    Glide.with(this).load(imageUri).into(imageView)

                    //imageView?.setImageURI(imageUri)
                } catch (e: Exception) {
                    Log.i("TAG", "Some exception $e")
                }
                val dialog =  Dialog(this)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setContentView(R.layout.loading_bar)
                dialog.show()
                path?.putFile(imageUri!!)?.addOnSuccessListener {
                    path.downloadUrl.addOnSuccessListener {
                        urlImage=it.toString()
                    }.addOnCompleteListener {
                        dialog.cancel()
                    }
                }?.addOnFailureListener{dialog.cancel()}
            }
        }
    }
}