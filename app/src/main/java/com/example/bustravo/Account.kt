package com.example.bustravo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var dbReference: DatabaseReference
    private lateinit var emailT: TextView
    private lateinit var nameT: TextView
    private lateinit var adhaarT: TextView
    private lateinit var numT: TextView
    private lateinit var signOut: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        nameT = view.findViewById(R.id.usernameA)
        emailT = view.findViewById(R.id.emailA)
        adhaarT = view.findViewById(R.id.adhaarA)
        numT = view.findViewById(R.id.numberPlateA)
        signOut = view.findViewById(R.id.signOut)
        signOut.setOnClickListener {
            startActivity(Intent(context, SignIn::class.java))
        }
            // Name, email address, and profile photo Url
            val data = arguments
            val name = data!!.getString("username").toString()

            dbReference = FirebaseDatabase.getInstance().getReference("drivers")

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(name)) {
                        emailT.text =
                            dataSnapshot.child(name).child("email").getValue(true)
                                .toString()
                        adhaarT.text =
                            dataSnapshot.child(name).child("adhaar").getValue(true)
                                .toString()
                        numT.text =
                            dataSnapshot.child(name).child("numberPlate").getValue(true)
                                .toString()
                        nameT.text =
                            dataSnapshot.child(name).child("name").getValue(true)
                                .toString()
                    } else {
                        Toast.makeText(context, "User Doesn't exist", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            dbReference.addListenerForSingleValueEvent(valueEventListener)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Account.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

