package com.task.ui


import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.task.LocationListData
import com.task.R
import com.task.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {
    var map: GoogleMap? = null
    private var currentLocation= LatLng(0.0,0.0)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    lateinit var binding:FragmentMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(requireActivity())
        if (isLocationEnabled())
            fetchLocation()
        else {
            locationDialog()
        }
        requireActivity().registerReceiver(gpsReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    private val gpsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("QWQWQWQWw","Location changed")
            if (intent.action!! == LocationManager.PROVIDERS_CHANGED_ACTION) {
                Log.i("QWQWQWQWw","Location changed2")
                fetchLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 2
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationProviderClient!!.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation= LatLng(location.latitude,location.longitude)
                    try {
                        Log.i("QWQWQWQWw","Location is: ${currentLocation.latitude}")
                        Toast.makeText(requireContext(), currentLocation.latitude.toString() + "" +
                                currentLocation.longitude, Toast.LENGTH_SHORT).show()
                        val supportMapFragment = (childFragmentManager.findFragmentById(R.id.frMap) as
                                SupportMapFragment?)!!
                        supportMapFragment.getMapAsync(this)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                } else {
                    fusedLocationProviderClient?.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper()!!)
                }
            }

    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location? = locationResult.lastLocation
            currentLocation = LatLng(location!!.latitude, location!!.longitude)
            Log.i("QWQWQWQWw", "Location is: ${currentLocation.latitude}")
            Toast.makeText(
                requireContext(), currentLocation.latitude.toString() + "" +
                        currentLocation.longitude, Toast.LENGTH_SHORT
            ).show()
            val supportMapFragment = (childFragmentManager.findFragmentById(R.id.frMap) as
                    SupportMapFragment?)!!
            supportMapFragment.getMapAsync(this@MapFragment)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map=p0
        addMarker(currentLocation.latitude, currentLocation.longitude)
        Log.i("QWQWQWQWw","MapReady")
        map?.setOnMapClickListener { p0 ->
            addMarker(p0.latitude,p0.longitude)
            var list=(requireActivity() as MainActivity).locationList
            var id=1
            if (!list.isNullOrEmpty())
                id=list[list.size-1].id+1
            var data= LocationListData(id,p0.latitude,p0.longitude)
            (requireActivity() as MainActivity).locationList.add(data)
            (requireActivity() as MainActivity).mainViewModal?.addLocation((requireActivity() as MainActivity).locationList)
        }
    }

    private fun addMarker(lat:Double,long:Double){
        val latLng = LatLng(lat,long)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        map?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
        map?.addMarker(markerOptions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==permissionCode && (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED))
            fetchLocation()
    }
    private fun locationDialog(){
        val builder= AlertDialog.Builder(requireContext())
            .setTitle("Location")
            .setMessage("Please turn on location")
            .setPositiveButton("Ok"){dialog,_->
                dialog.dismiss()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
            }
        val alert = builder.create()
        alert.show()
    }
}