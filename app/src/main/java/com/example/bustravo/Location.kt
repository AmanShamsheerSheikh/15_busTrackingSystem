package com.example.bustravo

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import org.apache.commons.lang3.time.StopWatch
import android.content.pm.PackageManager

import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Location.newInstance] factory method to
 * create an instance of this fragment.
 */
class Location : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var locationRequest : LocationRequest
    private lateinit var dbReference : DatabaseReference
    private lateinit var location: TextView
    private var handler : Handler = Handler()
    private var runnable : Runnable? = null
    private val delay = 1000
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
        val view =  inflater.inflate(R.layout.fragment_location, container, false)
        location = view.findViewById(R.id.locationL)
        locationRequest = LocationRequest.create()
        dbReference = FirebaseDatabase.getInstance().getReference("drivers")
        locationRequest.priority = PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
        activity?.title = "BUSTRAVO"
        return view
    }

    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!,delay.toLong())
            getCurrentLocation()
        }.also { runnable = it },delay.toLong())
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    private fun getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        ACCESS_FINE_LOCATION
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                if (isGPSEnabled()) {
                    requireContext().applicationContext?.let {
                        LocationServices.getFusedLocationProviderClient(it)
                            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    super.onLocationResult(locationResult)
                                    LocationServices.getFusedLocationProviderClient(it)
                                        .removeLocationUpdates(this)
                                    if (locationResult.locations.size > 0) {
                                        val data = arguments
                                        val name = data?.get("username").toString()
                                        val index = locationResult.locations.size - 1
                                        val latitude = locationResult.locations[index].latitude
                                        val longitude = locationResult.locations[index].longitude
                                        val hashMap = HashMap<String,Double>()
                                        hashMap["longitude"] = longitude
                                        hashMap["latitude"] = latitude
                                        dbReference.child(name).updateChildren(hashMap as Map<String, Any>)
                                        Log.d("log and lat", "onLocationResult:  $latitude $longitude ")
                                    }
                                }
                            }, Looper.myLooper())
                    }
                } else {
                    turnOnGPS()
                }
            } else {
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 1)
            }

        }
    }
    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> = context?.let {
            LocationServices.getSettingsClient(
                it
            )
                .checkLocationSettings(builder.build())
        } as Task<LocationSettingsResponse>
        result.addOnCompleteListener(OnCompleteListener<LocationSettingsResponse?> { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                Toast.makeText(requireContext().applicationContext, "GPS is already tured on", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(requireContext().applicationContext as Activity, 2)
                    } catch (ex: IntentSender.SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        })
    }

    private fun isGPSEnabled(): Boolean {
        var locationManager: LocationManager? = null
        var isEnabled = false
        if (locationManager == null) {
            locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isEnabled
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Location.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Location().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}