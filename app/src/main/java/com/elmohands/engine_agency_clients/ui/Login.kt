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
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elmohands.engine_agency_clients.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    var sharedPreferences: SharedPreferences? = null
    private var firebaseAuth: FirebaseAuth? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference: CollectionReference = db.collection("Users")
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("engine_agency_clients", MODE_PRIVATE)
        firebaseAuth=FirebaseAuth.getInstance();
        toggle.setOnTouchListener(OnTouchListener { v, event ->
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
                R.id.loginButton -> {
                    val email: String = email.text.toString().trim()
                    val password: String = password.text.toString().trim()
                    if (validation(email, password)) {
                        loginUser(email, password)
                    }
                }
                R.id.signup ->{
                    startActivity(Intent(this@Login, Register::class.java))
                    finish()
                }
            }
    }

    private fun loginUser(email: String, password: String)
    {
        val dialog =  Dialog(this@Login)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.loading_bar)
        dialog.show();

        val adminEmail = "engineagency@gmail.com"
        val adminPassword = "650640"
        if (email.equals( adminEmail )&& password.equals( adminPassword)) {
            firebaseAuth?.signInWithEmailAndPassword(email,password)?.addOnCompleteListener {
                if(it.isSuccessful){
                    val editor= sharedPreferences?.edit()
                    editor?.putBoolean("login",true)
                    editor?.putString("type","admin")
                    editor?.apply()
                    val intent=Intent(this@Login,AdminHome::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@Login, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@Login, "فشل", Toast.LENGTH_SHORT).show()
                }
                dialog.cancel()
            }?.addOnFailureListener {
                Toast.makeText(this@Login,it.localizedMessage , Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
        }else{
            firebaseAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this@Login, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                    val intent=Intent(this@Login,UserHome::class.java)
                    val editor= sharedPreferences?.edit()
                    editor?.putBoolean("login",true)
                    editor?.putString("type","user")
                    editor?.apply()
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this@Login, "فشل", Toast.LENGTH_SHORT).show()
                }
                dialog.cancel()
            }?.addOnFailureListener {
                Toast.makeText(this@Login, "فشل", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
        }
    }

    private fun validation(mail: String, pass: String): Boolean {
        var valid = true
        if (mail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            valid = false
            email.error = "من فضلك ادخل البريد"
        } else {
            email.error = null
        }
        if (pass.isEmpty()) {
            valid = false
            password.error = "من فضلك ادخل كلمة السر"
        } else {
            password.error = null
        }
        return valid
    }

}