package com.android.quo.util

/**
 * Created by vitusortner on 04.01.18.
 */
object Constants {

    const val TOKEN_KEY = "token"
    // Location distance in meters
    const val LOCATION_DISTANCE = 500
    const val BASE_URL = "http://ec2-52-57-50-127.eu-central-1.compute.amazonaws.com/"
    const val IMAGE_DIR = "/Quo"

    object Extra {
        const val PLACE_EXTRA = "place_extra"
    }

    object FragmentTag {
        const val HOME_FRAGMENT = "home_fragment"
        const val MY_PLACES_FRAGMENT = "my_places_fragment"
        const val PLACE_FRAGMENT = "place_fragment"
        const val CREATE_PLACE_FRAGMENT = "create_place_fragment"
    }
}