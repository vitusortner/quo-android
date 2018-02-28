package com.android.quo.view.createplace.event

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.android.quo.R
import com.android.quo.dataclass.DateTime
import com.android.quo.util.Constants
import com.android.quo.util.Constants.DEFAULT_IMG
import com.android.quo.util.Constants.IMAGE_DIR
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_CAMERA
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_EXTERNAL_STORAGE
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_GPS
import com.android.quo.util.Constants.Request.REQUEST_CAMERA
import com.android.quo.util.Constants.Request.REQUEST_GALLERY
import com.android.quo.util.CreatePlace
import com.android.quo.util.extension.addTo
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.view.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import id.zelory.compressor.Compressor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_create_event.descriptionEditText
import kotlinx.android.synthetic.main.fragment_create_event.eventNameEditText
import kotlinx.android.synthetic.main.fragment_create_event.eventScrollView
import kotlinx.android.synthetic.main.fragment_create_event.expirationCheckBox
import kotlinx.android.synthetic.main.fragment_create_event.fromDateEditText
import kotlinx.android.synthetic.main.fragment_create_event.fromTimeEditText
import kotlinx.android.synthetic.main.fragment_create_event.galleryButton
import kotlinx.android.synthetic.main.fragment_create_event.headerImageView
import kotlinx.android.synthetic.main.fragment_create_event.locationEditText
import kotlinx.android.synthetic.main.fragment_create_event.locationProgressBar
import kotlinx.android.synthetic.main.fragment_create_event.toDateEditText
import kotlinx.android.synthetic.main.fragment_create_event.toTimeEditText
import kotlinx.android.synthetic.main.layout_bottom_sheet_select_picture.view.cameraLayout
import kotlinx.android.synthetic.main.layout_bottom_sheet_select_picture.view.defaultImageListView
import kotlinx.android.synthetic.main.layout_bottom_sheet_select_picture.view.photosLayout
import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jung on 27.11.17.
 */
// TODO wtf too big
class CreateEventFragment : BaseFragment(R.layout.fragment_create_event) {

    private val calendar = Calendar.getInstance()
    private val dateFormat = "E, MMM dd yyyy"
    private val timeFormat = "h:mm a"
    private val timestampDateFormat = "yyyy-MM-dd"
    private val timestampTimeFormat = "HH:mm:ss"

    private var foundLocation = false
    private var startDateTime = DateTime()
    private var endDateTime = DateTime()

    private lateinit var currentEditText: EditText
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefaultValuesOnStart()
        setupButtons()
    }

    private fun setupButtons() {
        RxTextView.afterTextChangeEvents(eventNameEditText)
            .subscribe { CreatePlace.place.title = eventNameEditText.text.toString() }
            .addTo(compositeDisposable)

        fromDateEditText.setOnClickListener {
            currentEditText = fromDateEditText
            showCalendarView(true)
        }

        toDateEditText.setOnClickListener {
            currentEditText = toDateEditText
            showCalendarView(false)
        }

        fromTimeEditText.setOnClickListener {
            currentEditText = fromTimeEditText
            showTimeView(true)
        }

        toTimeEditText.setOnClickListener {
            currentEditText = toTimeEditText
            showTimeView(false)
        }

        expirationCheckBox.setOnClickListener {
            if (expirationCheckBox.isChecked) {
                toDateEditText.isEnabled = false
                toTimeEditText.isEnabled = false
                CreatePlace.place.endDate = null
            } else {
                toDateEditText.isEnabled = true
                toTimeEditText.isEnabled = true
                CreatePlace.place.endDate =
                        Timestamp.valueOf("${endDateTime.date} ${endDateTime.time}").time.toString()
            }
        }

        galleryButton.setOnClickListener {
            context?.let { context ->
                bottomSheetDialog = BottomSheetDialog(context)
                val sheetView = activity?.layoutInflater?.inflate(
                    R.layout.layout_bottom_sheet_select_picture,
                    null
                )
                sheetView?.let {
                    val adapter = EventDefaultImagesAdapter { drawable, position ->
                        onClick(drawable, position)
                    }
                    it.defaultImageListView.adapter = adapter
                    val layoutManager = LinearLayoutManager(context)
                    layoutManager.orientation = LinearLayout.HORIZONTAL
                    it.defaultImageListView?.layoutManager = layoutManager

                    val defaultImages = getDefaultImageList()
                    adapter.setItems(defaultImages)

                    bottomSheetDialog.setContentView(sheetView)
                    bottomSheetDialog.show()

                    it.photosLayout.setOnClickListener {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            PERMISSION_REQUEST_EXTERNAL_STORAGE
                        )
                    }

                    it.cameraLayout.setOnClickListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.CAMERA),
                            PERMISSION_REQUEST_CAMERA
                        )
                    }
                }

            }
        }

        RxView.touches(locationEditText,
            { motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    val DRAWABLE_RIGHT = 2
                    val editTextWidth =
                        locationEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                    if (motionEvent.rawX >= (locationEditText.right - editTextWidth)) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            PERMISSION_REQUEST_GPS
                        )
                    }
                }
                false
            })
            .subscribe()
            .addTo(compositeDisposable)

        RxView.focusChanges(locationEditText)
            .filter { locationEditText.text.toString() != "" }
            .subscribe { getLocationFromAddress(locationEditText.text.toString()) }
            .addTo(compositeDisposable)

        RxView.touches(descriptionEditText, { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                eventScrollView.requestDisallowInterceptTouchEvent(true)
            }
            false
        })
            .subscribe()
            .addTo(compositeDisposable)

        RxTextView.afterTextChangeEvents(descriptionEditText)
            .subscribe { CreatePlace.place.description = it.view().text.toString() }
            .addTo(compositeDisposable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_GPS -> {
                context
                    ?.permissionsGranted(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    ?.takeIf { it }
                    ?.run {
                        locationProgressBar.visibility = VISIBLE
                        locationEditText.clearFocus()
                        foundLocation = false
                        getLocation()
                    }
            }
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                context
                    ?.permissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ?.takeIf { it }
                    ?.run { openPhoneGallery() }
            }
            PERMISSION_REQUEST_CAMERA -> {
                context
                    ?.permissionsGranted(Manifest.permission.CAMERA)
                    ?.takeIf { it }
                    ?.run { openPhoneCamera() }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        CreatePlace.place.titlePicture?.let { titlePicture ->
            if (titlePicture.startsWith(DEFAULT_IMG)) {
                //split string because we need the index of the default image to display it at the beginning
                // or after changing default image
                var splitString = titlePicture.split(DEFAULT_IMG)
                splitString = splitString[1].split(".")

                headerImageView.setImageDrawable(getDefaultImageList()[splitString[0].toInt() - 1])
            } else if (titlePicture.isNotEmpty()) {
                val uri = Uri.parse(titlePicture)
                val bitmap = BitmapFactory.decodeFile(uri.path)
                headerImageView.setImageBitmap(bitmap)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun getDefaultImageList(): ArrayList<Drawable> {
        val list = ArrayList<Drawable>()
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.default_event_image1)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image2)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image3)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image4)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image5)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image6)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image7)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image8)
                ?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image9)
                ?.let { list.add(it) }
        }
        CreatePlace.list = list
        return list
    }

    private fun hideKeyboard(activity: FragmentActivity?) =
        activity?.let { activity ->
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        hideKeyboard(activity)
        context?.let { context ->
            locationClient = LocationServices.getFusedLocationProviderClient(context).apply {
                lastLocation.addOnSuccessListener { it?.let { getAddressFromLocation(it) } }
            }
        }
    }

    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        val city = addresses[0].locality
        val postalCode = addresses[0].postalCode
        val street = addresses[0].thoroughfare
        val number = addresses[0].subThoroughfare

        locationProgressBar.visibility = GONE
        locationEditText.setText("$street $number $postalCode $city")

        CreatePlace.place.apply {
            address?.city = city
            address?.street = "${addresses[0].thoroughfare}  ${addresses[0].subThoroughfare}"
            address?.zipCode = postalCode.toInt()
            latitude = location.latitude
            longitude = location.longitude
        }
    }

    private fun getLocationFromAddress(address: String) {
        val geocoder = Geocoder(context)
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses.size > 0) {
            val location = Location("")
            location.latitude = addresses[0].latitude
            location.longitude = addresses[0].longitude
            getAddressFromLocation(location)
        } else {
            log.e("address not found")
        }
    }

    private fun openPhoneGallery() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).let {
            activity?.startActivityForResult(it, REQUEST_GALLERY)
        }

    private fun openPhoneCamera() =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let {
            activity?.startActivityForResult(it, REQUEST_CAMERA)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                when (requestCode) {
                    REQUEST_GALLERY -> {
                        val selectedImageUri = data?.data
                        val image = File(selectedImageUri?.let { getPath(it) })

                        compressImage(image)
                            .subscribeOn(Schedulers.computation())
                            .map {
                                CreatePlace.place.titlePicture = it.absolutePath
                                BitmapFactory.decodeFile(it.absolutePath)
                            }
                            .observeOnUi()
                            .subscribe(
                                {
                                    headerImageView.setImageBitmap(it)
                                    bottomSheetDialog.dismiss()
                                }, {
                                    log.e("Error while compressing image", it)
                                }
                            )
                    }
                    REQUEST_CAMERA -> {
                        val image = data?.extras?.get("data") as Bitmap
                        headerImageView.setImageBitmap(image)
                        CreatePlace.place.titlePicture = data.extras?.get("data") as String
                    }
                }
            }
        } catch (e: Exception) {
            log.e("Error while selecting image.", e)
        }
    }

    private fun getPath(uri: Uri): String {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.let { context ->
            context.contentResolver?.query(
                uri, mediaStoreData,
                null, null, null
            )
        }

        cursor?.let { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(mediaStoreData[0])
                result = cursor.getString(columnIndex)
                cursor.close()
            }
        }

        if (result == null) {
            result = getString(R.string.not_found)
        }
        return result as String
    }

    private fun setDefaultValuesOnStart() {
        val currentDate = SimpleDateFormat(dateFormat, Locale.US).format(calendar.time)
        val currentTime = SimpleDateFormat(timeFormat, Locale.US).format(calendar.time)

        fromDateEditText.hint = currentDate
        toDateEditText.hint = currentDate

        fromTimeEditText.hint = currentTime
        toTimeEditText.hint = currentTime

        val timestampFormatTime =
            SimpleDateFormat(timestampTimeFormat, Locale.US).format(calendar.time)
        startDateTime.time = timestampFormatTime
        endDateTime.time = timestampFormatTime

        val timestampFormatDate =
            SimpleDateFormat(timestampDateFormat, Locale.US).format(calendar.time)
        startDateTime.date = timestampFormatDate
        endDateTime.date = timestampFormatDate
    }

    private fun showCalendarView(isStartDate: Boolean) {
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateLabel(isStartDate, false)
        }

        DatePickerDialog(
            context,
            R.style.DatePickerDialogStyle,
            date, calendar
                .get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    private fun showTimeView(isStartDate: Boolean) {
        val time = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR, hour)
            calendar.set(Calendar.MINUTE, minute)
            updateLabel(isStartDate, true)
        }

        TimePickerDialog(
            context,
            R.style.TimePickerDialogStyle,
            time,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
            .show()
    }


    private fun updateLabel(isStartDate: Boolean, isTimeLabel: Boolean) {
        val sdf: SimpleDateFormat
        val timestampFormat: SimpleDateFormat
        val timestamp: String

        if (isTimeLabel) {
            sdf = SimpleDateFormat(timeFormat, Locale.US)
            timestampFormat = SimpleDateFormat(timestampTimeFormat, Locale.US)
            timestamp = timestampFormat.format(calendar.time)
            if (isStartDate) {
                startDateTime.time = timestamp
            } else {
                endDateTime.time = timestamp
            }
        } else {
            sdf = SimpleDateFormat(dateFormat, Locale.US)
            timestampFormat = SimpleDateFormat(timestampDateFormat, Locale.US)
            timestamp = timestampFormat.format(calendar.time)
            if (isStartDate) {
                startDateTime.date = timestamp
            } else {
                endDateTime.date = timestamp
            }
        }

        if (isStartDate) {
            CreatePlace.place.startDate =
                    Timestamp.valueOf("${startDateTime.date} ${startDateTime.time}")
                        .time.toString()
        } else {
            CreatePlace.place.endDate =
                    Timestamp.valueOf("${endDateTime.date} ${endDateTime.time}").time.toString()
        }
        currentEditText.setText(sdf.format(calendar.time))
    }

    private fun compressImage(file: File) =
        Compressor(context)
            .setMaxWidth(Constants.MAX_IMG_DIM)
            .setMaxHeight(Constants.MAX_IMG_DIM)
            .setQuality(Constants.IMG_QUALITY)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .setDestinationDirectoryPath(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .absolutePath + IMAGE_DIR
            )
            .compressToFileAsFlowable(file)

    private fun onClick(drawable: Drawable, position: Int) {
        CreatePlace.place.titlePicture = "quo_default_${position + 1}.png"
        headerImageView.setImageDrawable(drawable)
    }
}