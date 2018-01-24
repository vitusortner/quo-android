package com.android.quo.view.createplace

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.android.quo.R
import com.android.quo.dataclass.EventDates
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

class CreateEventFragment : Fragment(), LocationListener {

    private val PERMISSION_REQUEST_GPS = 101
    private val PERMISSION_REQUEST_EXTERNAL_STORAGE = 102
    private val PERMISSION_REQUEST_CAMERA = 103
    private val DRAWABLE_RIGHT = 2
    private val RESULT_GALLERY = 0
    private var RESULT_CAMERA = 1
    private lateinit var calendar: Calendar
    private lateinit var currentEditText: EditText
    private val dateFormat = "E, MMM dd yyyy"
    private val timeFormat = "h:mm a"
    private val timestampDateFormat = "yyyy-MM-dd"
    private val timestampTimeFormat = "HH:mm:ss"
    private var compositeDisposable = CompositeDisposable()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var foundLocation = false
    private var startDate = EventDates("0000-00-00", "00:00:00")
    private var endDate = EventDates("0000-00-00", "00:00:00")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_event, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        setDefaultValuesOnStart()
        setupRxBindingViews()
    }

    private fun setupRxBindingViews() {
        /**
         * focusChanges on EventName Edit Text
         */
        compositeDisposable.add(RxTextView.afterTextChangeEvents(eventNameEditText)
                .subscribe {
                    /**
                     * Save into DB-Object
                     */
                    CreatePlace.place.title = eventNameEditText.text.toString()
                })


        /**
         * will show the calendar view
         */
        compositeDisposable.add(RxView.clicks(fromDateEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentEditText = fromDateEditText
                    showCalendarView(true)
                })

        /**
         * will show the calendar view
         */
        compositeDisposable.add(RxView.clicks(toDateEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentEditText = toDateEditText
                    showCalendarView(false)
                })

        /**
         * will show the time view
         */
        compositeDisposable.add(RxView.clicks(fromTimeEditText)
                .observeOn((AndroidSchedulers.mainThread()))
                .subscribe {
                    currentEditText = fromTimeEditText
                    showTimeView(true)
                })

        /**
         * will show the time view
         */
        compositeDisposable.add(RxView.clicks(toTimeEditText)
                .observeOn((AndroidSchedulers.mainThread()))
                .subscribe {
                    currentEditText = toTimeEditText
                    showTimeView(false)
                })

        /**
         * enable or disable the expire edit boxen for date and time via checkbox
         */
        compositeDisposable.add(RxView.clicks(expirationCheckBox as View)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (expirationCheckBox.isChecked) {
                        toDateEditText.isEnabled = false
                        toTimeEditText.isEnabled = false
                        CreatePlace.place.endDate = null
                    } else {
                        toDateEditText.isEnabled = true
                        toTimeEditText.isEnabled = true
                        CreatePlace.place.endDate =
                                Timestamp.valueOf("${endDate.date} ${endDate.time}").time.toString()
                    }
                })

        /**
         * handle to open the gallery for the header image
         */
        compositeDisposable.add(RxView.clicks(galleryButton)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    context?.let { context ->
                        bottomSheetDialog = BottomSheetDialog(context)
                        val sheetView = activity?.layoutInflater?.inflate(R.layout.layout_bottom_sheet_select_picture, null)
                        sheetView?.let {
                            it.defaultImageListView.adapter = EventDefaultImagesAdapter(getDefaultImageList(), headerImageView)
                            val linearLayoutManager = LinearLayoutManager(this.context)
                            linearLayoutManager.orientation = LinearLayout.HORIZONTAL
                            it.defaultImageListView?.layoutManager = linearLayoutManager
                            bottomSheetDialog.setContentView(sheetView)
                            bottomSheetDialog.show()

                            compositeDisposable.add(RxView.clicks(it.photosLayout)
                                    .subscribe {
                                        requestPermissions(arrayOf(
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                                PERMISSION_REQUEST_EXTERNAL_STORAGE)
                                    })

                            compositeDisposable.add(RxView.clicks(it.cameraLayout)
                                    .subscribe {
                                        requestPermissions(arrayOf(
                                                Manifest.permission.CAMERA),
                                                PERMISSION_REQUEST_CAMERA)
                                    })
                        }

                    }
                })


        /**
         * handle click on location icon on the right side of the edit box
         */
        compositeDisposable.add(RxView.touches(locationEditText, { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (locationEditText.right - locationEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    requestPermissions(arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                            PERMISSION_REQUEST_GPS
                    )
                }
            }
            false
        })
                .subscribe())

        compositeDisposable.add(RxView.focusChanges(locationEditText)
                .subscribe {
                    if (locationEditText.text.toString() != "") {
                        getLocationFromAddress(locationEditText.text.toString())
                    }

                })

        /**
         * disable main scroller if user will scroll in description box
         */
        compositeDisposable.add(RxView.touches(descriptionEditText, { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                eventScrollView.requestDisallowInterceptTouchEvent(true)
            }
            false
        }).subscribe())

        /**
         * event called if description text changes
         */
        compositeDisposable.add(RxTextView.afterTextChangeEvents(descriptionEditText)
                .subscribe {
                    CreatePlace.place.description = it.view().text.toString()
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_GPS -> {
                this.context?.let {
                    val resultFineLocation = ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    val resultCoarseLocation = ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
                    if (resultFineLocation == PackageManager.PERMISSION_GRANTED && resultCoarseLocation == PackageManager.PERMISSION_GRANTED) {
                        locationProgressBar.visibility = VISIBLE
                        locationEditText.clearFocus()
                        foundLocation = false
                        getLocation()
                    }
                }
            }
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                this.context?.let {
                    val result = ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        openPhoneGallery()
                    }
                }
            }
            PERMISSION_REQUEST_CAMERA -> {
                this.context?.let {
                    val result = ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA)
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        openPhoneCamera()
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        CreatePlace.place.titlePicture?.let { titlePicture ->
            if (titlePicture.startsWith("quo_default_")) {
                //split string because we need the index of the default image to display it at the beginning
                // or after changing default image
                var splitString = titlePicture.split("quo_default_")
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
            ContextCompat.getDrawable(context, R.drawable.default_event_image1)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image2)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image3)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image4)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image5)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image6)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image7)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image8)?.let { list.add(it) }
            ContextCompat.getDrawable(context, R.drawable.default_event_image9)?.let { list.add(it) }
        }
        CreatePlace.list = list
        return list
    }

    private fun hideKeyboard(activity: FragmentActivity?) {
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
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        hideKeyboard(activity)
        val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager?
        val locationListener = this
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }

    /**
     * get the location with address postalCode and city from lat and long
     */
    private fun getAddressFromLocation(location: Location) {
        val addresses: List<Address>
        val geocoder = Geocoder(this.context, Locale.getDefault())

        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        val city = addresses[0].locality
        val postalCode = addresses[0].postalCode
        val street = addresses[0].thoroughfare
        val number = addresses[0].subThoroughfare

        locationProgressBar.visibility = GONE
        locationEditText.setText("$street $number $postalCode $city")
        /**
         * Save into DB-Object
         */
        CreatePlace.place.address?.city = city
        CreatePlace.place.address?.street = "${addresses[0].thoroughfare}  ${addresses[0].subThoroughfare}"
        CreatePlace.place.address?.zipCode = postalCode.toInt()

        CreatePlace.place.latitude = location.latitude
        CreatePlace.place.longitude = location.longitude
    }

    /**
     * converts address in gps coordinates
     */
    private fun getLocationFromAddress(address: String) {
        val geocoder = Geocoder(this.context)
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses.size > 0) {
            val location = Location("")
            location.latitude = addresses[0].latitude
            location.longitude = addresses[0].longitude
            getAddressFromLocation(location)
        } else {
            Log.e("Error", "address not found")
        }
    }

    /**
     * open the phone gallery to select the header image
     */
    private fun openPhoneGallery() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        this.activity?.startActivityForResult(galleryIntent, RESULT_GALLERY)
    }

    private fun openPhoneCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.activity?.startActivityForResult(cameraIntent, RESULT_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data?.data
                    val compressedImage = compressImage(File(selectedImageUri?.let { getPath(it) }))
                    val bitmap = BitmapFactory.decodeFile(compressedImage.absolutePath)
                    headerImageView.setImageBitmap(bitmap)
                    CreatePlace.place.titlePicture = compressedImage.absolutePath
                    bottomSheetDialog.dismiss()

                } else if (resultCode == RESULT_CAMERA) {
                    val image = data?.extras?.get("data") as Bitmap
                    headerImageView.setImageBitmap(image)
                    CreatePlace.place.titlePicture = data.extras?.get("data") as String
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
    }

    private fun getPath(uri: Uri): String {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.context?.let { context ->
            context.contentResolver?.query(uri, mediaStoreData,
                    null, null, null)
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
        fromDateEditText.hint = setCurrentDate()
        toDateEditText.hint = setCurrentDate()

        fromTimeEditText.hint = setCurrentTime()
        toTimeEditText.hint = setCurrentTime()

        val timestampFormatTime = SimpleDateFormat(timestampTimeFormat, Locale.US)
        startDate.time = timestampFormatTime.format(calendar.time)
        endDate.time = timestampFormatTime.format(calendar.time)
        val timestampFormatDate = SimpleDateFormat(timestampDateFormat, Locale.US)
        startDate.date = timestampFormatDate.format(calendar.time)
        endDate.date = timestampFormatDate.format(calendar.time)
    }

    private fun setCurrentDate(): String {
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        return sdf.format(calendar.time)
    }

    private fun setCurrentTime(): String {
        val sdf = SimpleDateFormat(timeFormat, Locale.US)
        return sdf.format(calendar.time)
    }

    private fun showCalendarView(isStartDate: Boolean) {
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateLabel(isStartDate, false)
        }

        DatePickerDialog(this.context,
                R.style.DatePickerDialogStyle,
                date, calendar
                .get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    private fun showTimeView(isStartDate: Boolean) {
        val time = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR, hour)
            calendar.set(Calendar.MINUTE, minute)
            updateLabel(isStartDate, true)
        }

        TimePickerDialog(this.context,
                R.style.TimePickerDialogStyle,
                time,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false)
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
                startDate.time = timestamp
            } else {
                endDate.time = timestamp
            }
        } else {
            sdf = SimpleDateFormat(dateFormat, Locale.US)
            timestampFormat = SimpleDateFormat(timestampDateFormat, Locale.US)
            timestamp = timestampFormat.format(calendar.time)
            if (isStartDate) {
                startDate.date = timestamp
            } else {
                endDate.date = timestamp
            }
        }


        /**
         * Save into DB-Object
         */
        if (isStartDate) {
            CreatePlace.place.startDate = Timestamp.valueOf("${startDate.date} ${startDate.time}").time.toString()
        } else {
            CreatePlace.place.endDate = Timestamp.valueOf("${endDate.date} ${endDate.time}").time.toString()
        }
        currentEditText.setText(sdf.format(calendar.time))
    }

    /**
     * below all location handler
     */
    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            if (!foundLocation) {
                foundLocation = true
                getAddressFromLocation(p0)
            }

        }
    }

    private fun compressImage(file: File): File {
        return Compressor(this.context)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).absolutePath + "/Quo")
                .compressToFile(file)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}
}