package com.example.bustravo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*


class LogIn : AppCompatActivity() {
    private lateinit var user : EditText
    private lateinit var password : EditText
    private lateinit var logIn : Button
    private lateinit var signInRedirect : Button
    private lateinit var dbReference : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        user = findViewById(R.id.usernameL)
        password = findViewById(R.id.passwordL)
        logIn = findViewById(R.id.logInButton)
        signInRedirect = findViewById(R.id.signInDirect)
        dbReference = FirebaseDatabase.getInstance().getReference("drivers")
        signInRedirect.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,SignIn::class.java)
            startActivity(intent)
            finish()
        })

        logIn.setOnClickListener(View.OnClickListener {
            var user = user.text.toString()
            val p = password.text.toString()

            if(user.isEmpty() || p.isEmpty()){
                Toast.makeText(this,"Username Already taken", Toast.LENGTH_SHORT).show()
            }else{
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        val u = user.replace("@gmail.com","")
                        if(dataSnapshot.hasChild(user)){
                            val getPassword = dataSnapshot.child(user).child("password").getValue(true)
                            if (getPassword != null) {
                                if(getPassword.equals(p)){
                                    Toast.makeText(this@LogIn,"Log In Successful", Toast.LENGTH_SHORT).show()
                                    val bundle = Bundle()
                                    bundle.putString("name",user)
                                    val intent = Intent(this@LogIn,MainActivity::class.java)
                                    intent.putExtras(bundle)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this@LogIn,"Enter Correct Password",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(this@LogIn,"User Doesn't exist", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                }
                dbReference.addListenerForSingleValueEvent(valueEventListener)
            }
        })

    }
}