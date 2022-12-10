package com.grocylist


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FindStoresActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private var map: GoogleMap? = null
    private var currentLocation: Location? = null
    private var places: List<Place>? = null


    private lateinit var placesService: PlacesService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_stores)

        supportActionBar?.title = "Stores Near You"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        placesService = PlacesService.create()

        Dexter.withContext(this).withPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ).withListener(object : PermissionListener {
            @SuppressLint("MissingPermission")
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                setUpMaps()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@FindStoresActivity, "Location permission not granted", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {

                Toast.makeText(this@FindStoresActivity, "Location permission not granted", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

        }).check()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    @SuppressLint("MissingPermission")
    private fun setUpMaps() {
        mapFragment.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true

            getCurrentLocation {
                val pos = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 13f)
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos))
                getNearbyPlaces(it)
            }
            map = googleMap
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {
        }
    }

    private fun getNearbyPlaces(location: Location) {
        placesService.nearbyPlaces(
            apiKey = BuildConfig.GOOGLE_MAPS_API_KEY,
            location = "${location.latitude},${location.longitude}",
            radiusInMeters = 10000,
            placeType = "store"
        ).enqueue(
            object : Callback<NearbyPlacesResponse> {
                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<NearbyPlacesResponse>,
                    response: Response<NearbyPlacesResponse>
                ) {
                    if (!response.isSuccessful) {
                        return
                    }

                    places = response.body()!!.results
                    addMarkers()
                }
            }
        )
    }

    private fun addMarkers() {
        places?.forEach { place ->
            map?.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.geometry.location.latLng)
            )
        }
    }
}



