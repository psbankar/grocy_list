package com.grocylist

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
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

//TODO Fix location permission request
//


class FindStoresActivity : AppCompatActivity() {
    private val TAG: String? = "FindStoresActivityLogTag"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var mapFragment: SupportMapFragment
    private var map: GoogleMap? = null
    private var currentLocation: Location? = null
    private var places: MutableList<Place>? = null
    private var placeType: List<String> = listOf(
//        "bakery",
//        "bicycle_store",
//        "book_store",
//        "clothing_store",
//        "convenience_store",
//        "department_store",
//        "drugstore",
//        "electronics_store",
//        "furniture_store",
//        "hardware_store",
//        "home_goods_store",
//        "jewelry_store",
//        "liquor_store",
//        "shoe_store",
//        "shopping_mall",
//        "store",
        "supermarket"
    )


    private lateinit var placesService: PlacesService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_stores)

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
//                fusedLocationClient.getCurrentLocation(
//                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
//                    object : CancellationToken() {
//                        override fun onCanceledRequested(p0: OnTokenCanceledListener) =
//                            CancellationTokenSource().token
//
//                        override fun isCancellationRequested() = false
//                    })
//                    .addOnSuccessListener { location: Location? ->
//                        if (location == null)
//                            Toast.makeText(parent, "Cannot get location.", Toast.LENGTH_SHORT).show()
//                        else {
//                            lat = location.latitude
//                            lon = location.longitude
//                            mapFragment.getMapAsync {
//                                it.isMyLocationEnabled = true
//                                it.setOnMapLoadedCallback {
//                                    it.moveCamera(
//                                        CameraUpdateFactory.newLatLngZoom(
//
//                                            LatLng(
//                                                lat,
//                                                lon
//                                            ), 12.0f
//                                        )
//                                    )
////                            it.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))
//                                }
//                            }
//                        }
//
//                    }
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                TODO("Not yet implemented")
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                TODO("Not yet implemented")
            }

        }).check()

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
//            googleMap.setOnMarkerClickListener { marker ->
//                val tag = marker.tag
//                if (tag !is Place) {
//                    return@setOnMarkerClickListener false
//                }
//                showInfoWindow(tag)
//                return@setOnMarkerClickListener true
//            }
            map = googleMap
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {
            Log.e(TAG, "Could not get location")
        }
    }

    private fun getNearbyPlaces(location: Location) {
//        val apiKey = this.getString(R.string.google_maps_key)
        placeType.forEach {
            placesService.nearbyPlaces(
                apiKey = BuildConfig.GOOGLE_MAPS_API_KEY,
                location = "${location.latitude},${location.longitude}",
                radiusInMeters = 2000,
                placeType = "supermarket"
            ).enqueue(
                object : Callback<NearbyPlacesResponse> {
                    override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                        Log.e(TAG, "Failed to get nearby places", t)
                    }

                    override fun onResponse(
                        call: Call<NearbyPlacesResponse>,
                        response: Response<NearbyPlacesResponse>
                    ) {
                        if (!response.isSuccessful) {
                            Log.e(TAG, "Failed to get nearby places")
                            return
                        }

                        val places = response.body()?.results ?: emptyList()
                        this@FindStoresActivity.places?.addAll(places)


                    }
                }
            )
        }

        Log.d(TAG,places.toString())
        addMarkers()

    }

    private fun addMarkers() {
        places?.forEach { place ->
            val marker = map?.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.geometry.location.latLng)
            )
        }
    }


}



