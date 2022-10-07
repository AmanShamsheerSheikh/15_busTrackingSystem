package com.example.bustravo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class SignIn : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var adhaarN: EditText
    private lateinit var numberPlate: EditText
    private lateinit var register: Button
    private lateinit var logInDirect: Button
    private lateinit var dbReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var done : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        adhaarN = findViewById(R.id.adhaarCard)
        numberPlate = findViewById(R.id.busPlate)
        register = findViewById(R.id.re)
        logInDirect = findViewById(R.id.logInDirect)
        dbReference = Firebase.database.reference
        logInDirect.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        })

        register.setOnClickListener(View.OnClickListener {
            saveUser()
        })
    }

    fun saveUser(){
        val name = username.text.toString()
        val email = email.text.toString()
        val pass = password.text.toString()
        val adhaarN = adhaarN.text.toString()
        val np = numberPlate.text.toString()
        if(name.isEmpty() || email.isEmpty() || pass.isEmpty() || adhaarN.isEmpty() || np.isEmpty()){
            Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show()
        }else{

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.hasChild(name)){
                        Toast.makeText(this@SignIn,"Username Already taken",Toast.LENGTH_SHORT).show()
                    }else{
//                        val progressDialog = ProgressDialog(this@SignIn)
//                        progressDialog.setMessage("Uploading Image ...")
//                        progressDialog.setCancelable(false)
//                        progressDialog.show()
                        val formatter = SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault())
                        val now = Date()
                        var i = String()
                        val fileName = formatter.format(now)
                                val user = Driver(name,email,pass,adhaarN,np,null,null)
                                dbReference.child("drivers").child(name).setValue(user)
                                Toast.makeText(this@SignIn,"Sign In Successful",Toast.LENGTH_SHORT).show()
                                val bundle = Bundle()
                                bundle.putString("name",name)
                                val intent = Intent(this@SignIn,MainActivity::class.java)
                                intent.putExtras(bundle)
                                Log.d("sign in", "onDataChange: ${user.name} $name")
                                startActivity(intent)
                                finish()

                        }

                    }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            dbReference.addListenerForSingleValueEvent(valueEventListener)
            }

        }
    }
