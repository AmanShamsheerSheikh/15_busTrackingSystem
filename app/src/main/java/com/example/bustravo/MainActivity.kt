package com.example.bustravo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation : BottomNavigationView
    private var name = String()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val location = Location()
        val account = Account()
        val bundle = intent.extras
        name = bundle?.getString("name", "Default").toString()
        bundle?.putString("username",name)
        Log.d("main name", "onCreate: $name")
        bottomNavigation = findViewById(R.id.bottomNavigation)
        location.arguments = bundle
        currentFragment(location)
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.location -> {
                    location.arguments = bundle
                    currentFragment(location)
                }
                R.id.account -> {
                    account.arguments = bundle
                    currentFragment(account)
                }
            }
            true
        }
    }

    fun currentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.frameLayout,fragment)
            commit()
        }
}