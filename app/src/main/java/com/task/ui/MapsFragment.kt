package com.task.ui

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.task.LocationListData
import com.task.R
import com.task.databinding.EnableGpsLayoutBinding
import com.task.databinding.FragmentMapsBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class MapsFragment : Fragment() {
    lateinit var binding:FragmentMapsBinding
    lateinit var mapFragment:SupportMapFragment
    var map: GoogleMap? = null
    var dialog:Dialog?=null
    private var currentLocation= LatLng(0.0,0.0)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private val callback = OnMapReadyCallback { googleMap ->

        map=googleMap
        map?.clear()
        addMarker(currentLocation.latitude, currentLocation.longitude)
        var list=(requireActivity() as MainActivity).locationList
        if (!list.isNullOrEmpty()){
            for (i in 0..list.size-1){
                addListMarker(list[i].lat,list[i].lng)
            }
        }
        map?.setOnMapClickListener { p0 ->
            addListMarker(p0.latitude,p0.longitude)
            var list=(requireActivity() as MainActivity).locationList
            var id=1
            if (!list.isNullOrEmpty())
                id=list[list.size-1].id+1
            var data= LocationListData(id,p0.latitude,p0.longitude)
            (requireActivity() as MainActivity).locationList.add(data)
            (requireActivity() as MainActivity).mainViewModal?.addLocation((requireActivity() as MainActivity).locationList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMapsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(requireActivity())
        requireActivity().registerReceiver(gpsReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init(){
        if (isLocationEnabled())
            fetchLocation()
        else {
            locationDialog()
        }
    }

    private val gpsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!! == LocationManager.PROVIDERS_CHANGED_ACTION) {
                if (dialog!=null)
                    dialog!!.dismiss()
                init()
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
                        mapFragment.getMapAsync(callback)
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
            mapFragment.getMapAsync(callback)
        }
    }

    private fun addMarker(lat:Double,long:Double){
        val latLng = LatLng(lat,long)
        val markerOptions = MarkerOptions().position(latLng).title("")
        map?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
        map?.addMarker(markerOptions)
    }

    private fun addListMarker(lat:Double,long:Double){
        val latLng = LatLng(lat,long)
        val markerOptions = MarkerOptions().position(latLng).title("")
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
            init()
    }
    private fun locationDialog(){
        dialog = Dialog(requireActivity())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        var view=EnableGpsLayoutBinding.inflate(layoutInflater)
        val lp = WindowManager.LayoutParams()
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.attributes=lp
        dialog?.setContentView(view.root)
        dialog?.show()
    }
}