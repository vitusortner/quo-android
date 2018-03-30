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
import com.android.quo.util.Constants.DATA
import com.android.quo.util.Constants.DEFAULT_IMG
import com.android.quo.util.Constants.Date.DISPLAY_DATE_FORMAT
import com.android.quo.util.Constants.Date.DISPLAY_TIME_FORMAT
import com.android.quo.util.Constants.IMAGE_DIR
import com.android.quo.util.Constants.IMG_QUALITY
import com.android.quo.util.Constants.MAX_IMG_DIM
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_CAMERA
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_EXTERNAL_STORAGE
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_GPS
import com.android.quo.util.Constants.Request.REQUEST_CAMERA
import com.android.quo.util.Constants.Request.REQUEST_GALLERY
import com.android.quo.util.CreatePlace
import com.android.quo.util.DefaultImages
import com.android.quo.util.extension.compressImage
import com.android.quo.util.extension.now
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.util.extension.subscribeOnComputation
import com.android.quo.view.BaseFragment
import com.android.quo.view.createplace.event.CreateEventFragment.Label.DATE
import com.android.quo.view.createplace.event.CreateEventFragment.Label.TIME
import com.android.quo.view.createplace.event.CreateEventFragment.Time.END
import com.android.quo.view.createplace.event.CreateEventFragment.Time.START
import com.google.android.gms.location.LocationServices
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
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
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jung on 27.11.17.
 */
// TODO wtf way too big!
class CreateEventFragment : BaseFragment(R.layout.fragment_create_event) {

    private enum class Label { TIME, DATE }
    private enum class Time { START, END }

    private var dateTime = DateTime.now()

    private lateinit var currentEditText: EditText
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val defaultImages by lazy {
        DefaultImages.get(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLabelDefaultValues()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        setHeaderImage()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_GPS -> {
                requireContext()
                    .permissionsGranted(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .takeIf { it }
                    ?.run {
                        locationProgressBar.visibility = VISIBLE
                        locationEditText.clearFocus()
                        hideKeyboard(requireActivity())
                        checkForLastLocation {
                            persistAddressFromLocation(it)
                        }
                    }
            }
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                requireContext()
                    .permissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .takeIf { it }
                    ?.run { openPhoneGallery() }
            }
            PERMISSION_REQUEST_CAMERA -> {
                requireContext()
                    .permissionsGranted(Manifest.permission.CAMERA)
                    .takeIf { it }
                    ?.run { openPhoneCamera() }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                when (requestCode) {
                    REQUEST_GALLERY -> setHeaderImageFromGallery(data.data)
                    REQUEST_CAMERA -> setHeaderImageFromCamera(data.extras)
                }
            }
        } catch (e: Exception) {
            log.e("Error while selecting image.", e)
        }
    }

    private fun setupButtons() {
        RxTextView.afterTextChangeEvents(eventNameEditText)
            .subscribe { CreatePlace.place.title = eventNameEditText.text.toString() }
            .addTo(compositeDisposable)

        val calendar = Calendar.getInstance()

        fromDateEditText.setOnClickListener {
            currentEditText = fromDateEditText
            createDatePickerDialog(START, calendar).show()
        }

        toDateEditText.setOnClickListener {
            currentEditText = toDateEditText
            createDatePickerDialog(END, calendar).show()
        }

        fromTimeEditText.setOnClickListener {
            currentEditText = fromTimeEditText
            createTimePickerDialog(START, calendar).show()
        }

        toTimeEditText.setOnClickListener {
            currentEditText = toTimeEditText
            createTimePickerDialog(END, calendar).show()
        }

        expirationCheckBox.setOnClickListener {
            if (expirationCheckBox.isChecked) {
                toDateEditText.isEnabled = false
                toTimeEditText.isEnabled = false
                CreatePlace.place.endDate = null
            } else {
                toDateEditText.isEnabled = true
                toTimeEditText.isEnabled = true
                CreatePlace.place.endDate = DateTime.now().toMongoTimestamp()
            }
        }

        galleryButton.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(requireContext())

            // TODO nicer please
            val view = requireActivity()
                .layoutInflater
                .inflate(R.layout.layout_bottom_sheet_select_picture, null)

            val adapter = EventDefaultImagesAdapter { drawable, position ->
                onClick(drawable, position)
            }
            view.defaultImageListView.adapter = adapter
            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.orientation = LinearLayout.HORIZONTAL
            view.defaultImageListView?.layoutManager = layoutManager

            adapter.setItems(defaultImages)

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            view.photosLayout.setOnClickListener {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_REQUEST_EXTERNAL_STORAGE
                )
            }

            view.cameraLayout.setOnClickListener {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
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
            .map { locationEditText.text.toString() }
            .filter { it != "" }
            .subscribe { persistAddress(it) }
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

    private fun setHeaderImage() {
        CreatePlace.place.titlePicture?.let { titlePicture ->
            when {
                titlePicture.startsWith(DEFAULT_IMG) -> {
                    // split createDateOrTimeString because we need the index of the default image to display it at the beginning
                    // or after changing default image
                    val index = titlePicture.split(DEFAULT_IMG)[1].split(".")[0].toInt()
                    defaultImages[index - 1]
                        .let { headerImageView.setImageDrawable(it) }
                }
                else -> {
                    Uri.parse(titlePicture)
                        .let { BitmapFactory.decodeFile(it.path) }
                        .let { headerImageView.setImageBitmap(it) }
                }
            }
        }
    }

    private fun hideKeyboard(activity: FragmentActivity) {
        //Find the currently focused view, so we can grab the correct window token from it.
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        val view = activity.currentFocus ?: View(activity)
        (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("MissingPermission")
    private fun checkForLastLocation(callback: (Location) -> Unit) {
        locationClient.lastLocation.addOnSuccessListener { it?.let { callback(it) } }
    }

    private fun persistAddressFromLocation(location: Location) {
        val addresses = Geocoder(context, Locale.getDefault())
            .getFromLocation(location.latitude, location.longitude, 1)

        val city = addresses[0].locality
        val postalCode = addresses[0].postalCode
        val street = addresses[0].thoroughfare
        val number = addresses[0].subThoroughfare

        locationProgressBar.visibility = GONE
        locationEditText.setText("$street $number $postalCode $city")

        CreatePlace.place.apply {
            address?.city = city
            address?.street = "$street  $number"
            address?.zipCode = postalCode.toInt()
            latitude = location.latitude
            longitude = location.longitude
        }
    }

    private fun persistAddress(address: String) {
        val addresses = Geocoder(requireContext()).getFromLocationName(address, 1)
        if (addresses.size > 0) {
            val location = Location("")
            location.latitude = addresses[0].latitude
            location.longitude = addresses[0].longitude
            persistAddressFromLocation(location)
        } else {
            log.e("Address not found")
        }
    }

    private fun openPhoneGallery() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).let {
            requireActivity().startActivityForResult(it, REQUEST_GALLERY)
        }

    private fun openPhoneCamera() =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let {
            requireActivity().startActivityForResult(it, REQUEST_CAMERA)
        }

    private fun setHeaderImageFromGallery(uri: Uri) =
        getPath(uri)?.let {
            val image = File(it)
            val imageDir =
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .absolutePath + IMAGE_DIR

            imageCompressor
                .compressImage(
                    image,
                    MAX_IMG_DIM,
                    MAX_IMG_DIM,
                    IMG_QUALITY,
                    imageDir
                )
                .subscribeOnComputation()
                .map {
                    CreatePlace.place.titlePicture = it.absolutePath
                    BitmapFactory.decodeFile(it.absolutePath)
                }
                .observeOnUi()
                .subscribeBy(
                    onNext = {
                        headerImageView.setImageBitmap(it)
                        bottomSheetDialog.dismiss()
                    },
                    onError = {
                        log.e("Error while compressing image", it)
                    }
                )
        }

    // TODO use proper implementation for getting image from camera in full resolution
    private fun setHeaderImageFromCamera(extras: Bundle) {
        CreatePlace.place.titlePicture = extras.get(DATA) as String
        (extras.get(DATA) as Bitmap).let { headerImageView.setImageBitmap(it) }
    }

    private fun getPath(uri: Uri): String? {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)

        requireContext().contentResolver.query(uri, mediaStoreData, null, null, null).apply {
            if (moveToFirst()) {
                result = getColumnIndexOrThrow(mediaStoreData[0]).let { getString(it) }
                close()
            }
        }
        return result
    }

    private fun setLabelDefaultValues() {
        Date().apply {
            now(DISPLAY_DATE_FORMAT).let {
                fromDateEditText.hint = it
                toDateEditText.hint = it
            }
            now(DISPLAY_TIME_FORMAT).let {
                fromTimeEditText.hint = it
                toTimeEditText.hint = it
            }
        }
        persistDateTime(dateTime, START)
        persistDateTime(dateTime, END)
    }

    private fun createDateSetListener(time: Time, calendar: Calendar) =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            DateTime.DATE_FORMAT.format(calendar.time).let { dateTime.date = it }
            persistDateTime(dateTime, time)
            updateLabel(DATE, calendar.time)
        }

    private fun createDatePickerDialog(time: Time, calendar: Calendar) =
        DatePickerDialog(
            requireContext(),
            R.style.DatePickerDialogStyle,
            createDateSetListener(time, calendar),
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

    private fun createTimeSetListener(time: Time, calendar: Calendar) =
        TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.apply {
                set(Calendar.HOUR, hour)
                set(Calendar.MINUTE, minute)
            }
            DateTime.TIME_FORMAT.format(calendar.time).let { dateTime.time = it }
            persistDateTime(dateTime, time)
            updateLabel(TIME, calendar.time)
        }

    private fun createTimePickerDialog(time: Time, calendar: Calendar) =
        TimePickerDialog(
            requireContext(),
            R.style.TimePickerDialogStyle,
            createTimeSetListener(time, calendar),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

    private fun persistDateTime(dateTime: DateTime, time: Time) =
        when (time) {
            START -> CreatePlace.place.startDate = dateTime.toMongoTimestamp()
            END -> CreatePlace.place.endDate = dateTime.toMongoTimestamp()
        }

    private fun updateLabel(label: Label, date: Date) {
        val format = when (label) {
            TIME -> DISPLAY_TIME_FORMAT
            DATE -> DISPLAY_DATE_FORMAT
        }
        SimpleDateFormat(format, Locale.getDefault()).format(date)
            .let { currentEditText.setText(it) }
    }

    private fun onClick(drawable: Drawable, position: Int) {
        CreatePlace.place.titlePicture = "$DEFAULT_IMG${position + 1}.png"
        headerImageView.setImageDrawable(drawable)
    }

}