package com.elmohands.engine_agency_clients.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elmohands.engine_agency_clients.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.email
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.toggle
import kotlinx.android.synthetic.main.activity_register.*

private var firebaseAuth: FirebaseAuth? = null
private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
private val collectionReference: CollectionReference = db.collection("Users")
var sharedPreferences: SharedPreferences? = null

class Register : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        firebaseAuth=FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("engine_agency_clients", MODE_PRIVATE)

        toggle.setOnTouchListener(View.OnTouchListener { v, event ->
            if (!TextUtils.isEmpty(password.text.toString().trim())) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        password.inputType = InputType.TYPE_CLASS_TEXT
                        toggle.setImageResource(R.drawable.ic_eye)
                    }
                    MotionEvent.ACTION_UP -> {
                        password.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        toggle.setImageResource(R.drawable.ic_hide)
                    }
                }
            }
            true
        })
    }

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.imageButton -> {
                val name: String = userName.getText().toString().trim()
                val email: String = email.getText().toString().trim()
                val password: String = password.getText().toString().trim()
                if (validation(name, email, password)) {
                    createUser(name, email, password)
                }
            }
            R.id.Signin -> {
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }
    }

    private fun createUser(name: String, email: String, password: String)
    {
        val dialog =  Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show();

        firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if(it.isSuccessful){
                val currentUser=firebaseAuth?.currentUser?.uid.toString()
                val map: MutableMap<String, String> = HashMap()
                map["userId"] =currentUser
                map["userName"] = name
                collectionReference.document(currentUser).set(map).addOnCompleteListener {
                    val intent=Intent(this@Register,UserHome::class.java)
                    startActivity(intent)
                    Toast.makeText(this@Register, "اتم الانشاء بنجاح", Toast.LENGTH_SHORT).show()
                    val editor= sharedPreferences?.edit()
                    editor?.putBoolean("login",true)
                    editor?.putString("type","user")
                    editor?.apply()
                    dialog.cancel()
                    finish()
                }
            }
        }?.addOnFailureListener {
            Toast.makeText(this@Register, "فشل", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
    }

    private fun validation(mail: String, pass: String, name: String): Boolean {
        var valid = true
        if (name.isEmpty()) {
            valid = false
            userName.error = "please enter your name"
            userName.requestFocus()


        } else {
            userName.error = null
        }
        if (mail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            valid = false
            email.error = "Please Enter your email."
        } else {
            email.error = null
        }
        if (pass.isEmpty()) {
            valid = false
            password.error = "Please Enter your password"
        } else {
            password.error = null
        }
        return valid
    }
}