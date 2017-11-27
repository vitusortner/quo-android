package com.android.quo.view.myplaces

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.android.quo.R
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_create_event.expirationCheckBox
import kotlinx.android.synthetic.main.fragment_create_event.fromDateEditText
import kotlinx.android.synthetic.main.fragment_create_event.fromTimeEditText
import kotlinx.android.synthetic.main.fragment_create_event.locationEditText
import kotlinx.android.synthetic.main.fragment_create_event.toDateEditText
import kotlinx.android.synthetic.main.fragment_create_event.toTimeEditText
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jung on 27.11.17.
 */

class CreateEventFragment : Fragment() {
    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1
    private val DRAWABLE_RIGHT = 2
    private lateinit var calendar: Calendar
    private lateinit var currentEditText: EditText
    private val dateFormat = "E, MMM dd yyyy"
    private val timeFormat = "h:mm a"

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

        RxView.clicks(fromDateEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentEditText = fromDateEditText
                    showCalendarView()
                }

        RxView.clicks(toDateEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    currentEditText = toDateEditText
                    showCalendarView()
                }

        RxView.clicks(fromTimeEditText)
                .observeOn((AndroidSchedulers.mainThread()))
                .subscribe {
                    currentEditText = fromTimeEditText
                    showTimeView()
                }

        RxView.clicks(toTimeEditText)
                .observeOn((AndroidSchedulers.mainThread()))
                .subscribe {
                    currentEditText = toTimeEditText
                    showTimeView()
                }

        RxView.clicks(expirationCheckBox as View)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (expirationCheckBox.isChecked) {
                        toDateEditText.isEnabled = false
                        toTimeEditText.isEnabled = false
                    } else {
                        toDateEditText.isEnabled = true
                        toTimeEditText.isEnabled = true
                    }
                }



        RxView.touches(locationEditText, { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (locationEditText.right - locationEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
//                  //TODO get gps location and convert in address
                    println("location icon pressed")
                }
            }
            false
        })
                .subscribe()
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
}