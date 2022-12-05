package com.grocylist

import com.google.gson.annotations.SerializedName

data class NearbyPlacesResponse(
    @SerializedName("results") val results: List<Place>
)