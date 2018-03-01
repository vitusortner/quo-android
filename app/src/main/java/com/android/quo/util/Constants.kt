package com.android.quo.util

/**
 * Created by vitusortner on 04.01.18.
 */
object Constants {

    const val TOKEN_KEY = "token"
    const val LOCATION_DISTANCE = 500 // Location distance in meters
    const val BASE_URL = "http://ec2-52-57-50-127.eu-central-1.compute.amazonaws.com/"
    const val IMAGE_DIR = "/Quo"
    const val GALLERY_COLUMN_COUNT = 3
    const val QR_CODE_DIM = 1024
    const val QR_CODE_URI = "quo://"
    const val MAX_IMG_DIM = 640
    const val IMG_QUALITY = 75
    const val HTTP = "http"
    const val DEFAULT_IMG = "quo_default_"

    object Extra {
        const val PLACE_EXTRA = "place_extra"
        const val PLACE_ID_EXTRA = "place_id_extra"
        const val PICTURE_URL_EXTRA = "picture_url_extra"
        const val PICTURE_LIST_EXTRA = "picture_list_extra"
        const val PICTURE_POSITION_EXTRA = "picture_position_extra"
    }

    object FragmentTag {
        const val HOME_FRAGMENT = "home_fragment"
        const val MY_PLACES_FRAGMENT = "my_places_fragment"
        const val PLACE_FRAGMENT = "place_fragment"
        const val CREATE_PLACE_FRAGMENT = "create_place_fragment"
        const val QR_CODE_FRAGMENT = "qr_code_fragment"
    }

    object Request {
        const val PERMISSION_REQUEST_GPS = 1
        const val PERMISSION_REQUEST_EXTERNAL_STORAGE = 2
        const val PERMISSION_REQUEST_CAMERA = 3
        const val PERMISSION_REQUEST_MULTIPLE = 4

        const val REQUEST_CAMERA = 5
        const val REQUEST_GALLERY = 6

        const val CREATE_PAGE_REQUEST_GALLERY = 7
    }

    object Date {
        const val MONGO_DB_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
}