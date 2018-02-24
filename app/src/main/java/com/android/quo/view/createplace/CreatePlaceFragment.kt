package com.android.quo.view.createplace

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.android.quo.R
import com.android.quo.network.model.ServerPlace
import com.android.quo.network.model.ServerSettings
import com.android.quo.util.CreatePlace
import com.android.quo.view.BaseFragment
import com.android.quo.view.createplace.qrcode.QrCodeFragment
import com.android.quo.viewmodel.CreatePlaceViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_create_place.createPlaceViewPager
import kotlinx.android.synthetic.main.fragment_create_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.koin.android.architecture.ext.viewModel
import java.sql.Timestamp

/**
 * Created by Jung on 27.11.17.
 */
class CreatePlaceFragment : BaseFragment(R.layout.fragment_create_place) {

    private val viewModel by viewModel<CreatePlaceViewModel>()

    private val compositDisposable = CompositeDisposable()

    private val PERMISSION_REQUEST_EXTERNAL_STORAGE = 102

    lateinit var place: ServerPlace

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            createPlaceViewPager.adapter = CreatePlacePagerAdapter(childFragmentManager, it)
        }
        tabLayout.setupWithViewPager(createPlaceViewPager)

        setupToolbar()

        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_EXTERNAL_STORAGE
        )

        generateQrCodeObservable()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    /**
     * change status bar and status bar text color
     */
    private fun setupStatusBar() {
        activity?.window?.decorView?.systemUiVisibility = 0
        activity?.window?.statusBarColor = resources.getColor(R.color.colorSysBarCreatePlace)
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.inflateMenu(R.menu.create_place_menu)
        toolbar.title = getString(R.string.new_place)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorTextWhite))

        compositDisposable.add(
            RxToolbar.navigationClicks(toolbar)
                .subscribe {
                    activity?.onBackPressed()
                }
        )

        compositDisposable.add(
            RxToolbar.itemClicks(toolbar)
                .subscribe {
                    if (!CreatePlace.place.titlePicture.isNullOrEmpty() && !CreatePlace.place.title.isNullOrEmpty()
                        && !CreatePlace.place.latitude.isNaN() && !CreatePlace.place.longitude.isNaN()
                        && !CreatePlace.place.description.isNullOrEmpty()) {

                        viewModel.savePlace()
                        fragmentManager?.beginTransaction()
                            ?.replace(R.id.content, QrCodeFragment())
                            ?.addToBackStack(null)
                            ?.commit()

                    } else {
                        //TODO add message please fill required boxes
                    }
                }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                this.context?.let {
                    val result = ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    if (result == PackageManager.PERMISSION_DENIED) {
                        fragmentManager?.popBackStack()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        setupStatusBar()
    }

    override fun onStop() {
        super.onStop()

        activity?.window?.let { window ->
            this.context?.let { context ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    window.statusBarColor =
                            ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.statusBarColor =
                            ContextCompat.getColor(context, R.color.colorStatusBarSdkPre23)
                }
            }
        }
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
    }

    override fun onDestroy() {
        super.onDestroy()
        //set place to default
        CreatePlace.place = ServerPlace(
            host = "",
            title = "",
            startDate = "",
            latitude = -1.0,
            longitude = -1.0,
            settings = ServerSettings(false, false),
            titlePicture = "quo_default_1.png",
            qrCodeId = "",
            timestamp = ""
        )
        compositDisposable.dispose()
    }

    private fun generateQrCodeObservable(): Observable<Bitmap> {
        return Observable.create {
            val timestamp = Timestamp(System.currentTimeMillis())

            viewModel.getUser {
                it?.let { userId ->
                    val qrCodeId =
                        String(Hex.encodeHex(DigestUtils.md5(timestamp.toString() + userId)))
                    val uri = "quo://" + qrCodeId
                    val width = 1024
                    val height = 1024
                    val multiFormatWriter = MultiFormatWriter()
                    val bm = multiFormatWriter.encode(uri, BarcodeFormat.QR_CODE, width, height)
                    val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                    for (i in 0 until width) {
                        for (j in 0 until height) {
                            imageBitmap.setPixel(
                                i,
                                j,
                                if (bm.get(i, j)) Color.BLACK else Color.WHITE
                            )
                        }
                    }

                    CreatePlace.place.qrCodeId = qrCodeId
                    CreatePlace.qrCodeImage = imageBitmap
                }
            }
        }
    }
}