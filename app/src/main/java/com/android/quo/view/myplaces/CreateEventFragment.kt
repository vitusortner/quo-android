package com.android.quo.view.myplaces

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
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
import com.android.quo.view.myplaces.createplace.EventDefaultImagesAdapter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_create_event.descriptionEditText
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
import kotlinx.android.synthetic.main.layout_bottom_sheet_select_foto.view.defaultImageListView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Jung on 27.11.17.
 */

class CreateEventFragment : Fragment(), LocationListener {

    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1
    private val DRAWABLE_RIGHT = 2
    private val RESULT_GALLERY = 0
    private lateinit var calendar: Calendar
    private lateinit var currentEditText: EditText
    private val dateFormat = "E, MMM dd yyyy"
    private val timeFormat = "h:mm a"
    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_event, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE
        )

        calendar = Calendar.getInstance()

        setDefaultValuesOnStart()

        /**
         * will show the calendar view
         */
        compositeDisposable.add(RxView.clicks(fromDateEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentEditText = fromDateEditText
                    showCalendarView()
                })

        /**
         * will show the calendar view
         */
        compositeDisposable.add(RxView.clicks(toDateEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentEditText = toDateEditText
                    showCalendarView()
                })

        /**
         * will show the time view
         */
        compositeDisposable.add(RxView.clicks(fromTimeEditText)
                .observeOn((AndroidSchedulers.mainThread()))
                .subscribe {
                    currentEditText = fromTimeEditText
                    showTimeView()
                })

        /**
         * will show the time view
         */
        compositeDisposable.add(RxView.clicks(toTimeEditText)
                .observeOn((AndroidSchedulers.mainThread()))
                .subscribe {
                    currentEditText = toTimeEditText
                    showTimeView()
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
                    } else {
                        toDateEditText.isEnabled = true
                        toTimeEditText.isEnabled = true
                    }
                })

        /**
         * handle to open the gallery for the header image
         */
        compositeDisposable.add(RxView.clicks(galleryButton)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //openPhoneGallery()
                    this.context.let { context ->
                        val mBottomSheetDialog = BottomSheetDialog(context!!)
                        val sheetView = activity?.layoutInflater!!.inflate(R.layout.layout_bottom_sheet_select_foto, null)
                        sheetView.defaultImageListView.adapter = EventDefaultImagesAdapter(getDefaultImageList())
                        val linearLayoutManager = LinearLayoutManager(this.context)
                        linearLayoutManager.orientation = LinearLayout.HORIZONTAL
                        sheetView.defaultImageListView.layoutManager = linearLayoutManager
                        mBottomSheetDialog.setContentView(sheetView)
                        mBottomSheetDialog.show()

                    }
                })


        /**
         * handle click on location icon on the right side of the edit box
         */
        compositeDisposable.add(RxView.touches(locationEditText, { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (locationEditText.right - locationEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
//                  //TODO get gps location and convert in address
                    println("location icon pressed")
                    locationProgressBar.visibility = VISIBLE
                    locationEditText.clearFocus()
                    getLocation()
                }
            }
            false
        })
                .subscribe())

        /**
         * disable main scroller if user will scroll in description box
         */
        compositeDisposable.addAll(RxView.touches(descriptionEditText, { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                eventScrollView.requestDisallowInterceptTouchEvent(true)
            }
            false
        }).subscribe())

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun getDefaultImageList(): ArrayList<Drawable> {
        val list = ArrayList<Drawable>()
        list.add(resources.getDrawable(R.drawable.default_event_image1))
        list.add(resources.getDrawable(R.drawable.default_event_image2))
        list.add(resources.getDrawable(R.drawable.default_event_image3))
        list.add(resources.getDrawable(R.drawable.default_event_image4))
        list.add(resources.getDrawable(R.drawable.default_event_image5))
        list.add(resources.getDrawable(R.drawable.default_event_image6))
        list.add(resources.getDrawable(R.drawable.default_event_image7))
        list.add(resources.getDrawable(R.drawable.default_event_image8))
        list.add(resources.getDrawable(R.drawable.default_event_image9))

        return list


    }

    // fun createBitmap(image: Int): Bitmap = BitmapFactory.decodeResource(context?.resources,image)

    fun hideKeyboard(activity: FragmentActivity?) {
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
    }

    /**
     * get the location with address postalCode and city from lat and long
     */
    private fun getAddressFromLocation(location: Location) {
        val addresses: List<Address>
        val geocoder = Geocoder(this.context, Locale.getDefault())

        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName

        locationProgressBar.visibility = GONE
        locationEditText.setText("$address $postalCode $city")
    }

    /**
     * open the phone gallery to select the header image
     */
    private fun openPhoneGallery() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        this.activity?.let { activity ->
            activity.startActivityForResult(galleryIntent, RESULT_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data?.data
                    val path = selectedImageUri?.let { getPath(it) }
                    val bitmap = BitmapFactory.decodeFile(path)
                    headerImageView.setImageBitmap(bitmap)
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

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(mediaStoreData[0])
                result = cursor.getString(columnIndex)
                cursor.close()
            }
        }

        if (result == null) {
            result = "Not found"
        }
        return result
    }

    private fun setDefaultValuesOnStart() {
        fromDateEditText.hint = setCurrentDate()
        toDateEditText.hint = setCurrentDate()

        fromTimeEditText.hint = setCurrentTime()
        toTimeEditText.hint = setCurrentTime()
    }

    private fun setCurrentDate(): String {
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        return sdf.format(calendar.time)
    }

    private fun setCurrentTime(): String {
        val sdf = SimpleDateFormat(timeFormat, Locale.US)
        return sdf.format(calendar.time)
    }

    private fun showCalendarView() {
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateCalendarLabel()
        }

        DatePickerDialog(this.context,
                R.style.DatePickerDialogStyle,
                date, calendar
                .get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    private fun updateCalendarLabel() {
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        currentEditText.setText(sdf.format(calendar.time))
    }

    private fun showTimeView() {
        val time = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR, hour)
            calendar.set(Calendar.MINUTE, minute)
            updateTimeLabel()
        }

        TimePickerDialog(this.context,
                R.style.TimePickerDialogStyle,
                time,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false)
                .show()
    }

    private fun updateTimeLabel() {
        val sdf = SimpleDateFormat(timeFormat, Locale.US)
        currentEditText.setText(sdf.format(calendar.time))
    }

    /**
     * below all location handler
     */
    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            println("#####################" + p0.latitude)
            getAddressFromLocation(p0)
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}
}