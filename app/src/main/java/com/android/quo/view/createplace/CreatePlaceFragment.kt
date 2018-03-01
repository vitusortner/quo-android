package com.android.quo.view.createplace

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.android.quo.R
import com.android.quo.db.entity.User
import com.android.quo.network.model.ServerPlace
import com.android.quo.network.model.ServerSettings
import com.android.quo.util.Constants
import com.android.quo.util.Constants.QR_CODE_DIM
import com.android.quo.util.Constants.QR_CODE_URI
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_EXTERNAL_STORAGE
import com.android.quo.util.CreatePlace
import com.android.quo.util.extension.createAndReplaceFragment
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.view.BaseFragment
import com.android.quo.view.createplace.qrcode.QrCodeFragment
import com.android.quo.viewmodel.CreatePlaceViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
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

    private val viewModel by viewModel<CreatePlaceViewModel>(false)

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

        AsyncTask.execute { generateQrCode() }
    }

    override fun onResume() {
        super.onResume()
        setupStatusBar()
    }

    override fun onStop() {
        super.onStop()
        resetStatusBar()
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                context
                    ?.permissionsGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ?.takeIf { !it }
                    ?.run { fragmentManager?.popBackStack() }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setupStatusBar() =
        activity?.window?.apply {
            decorView.systemUiVisibility = 0
            statusBarColor = resources.getColor(R.color.colorSysBarCreatePlace)
        }

    private fun setupToolbar() =
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            inflateMenu(R.menu.create_place_menu)
            title = getString(R.string.new_place)
            setTitleTextColor(resources.getColor(R.color.colorTextWhite))

            setNavigationOnClickListener { activity?.onBackPressed() }

            setOnMenuItemClickListener {
                if (!CreatePlace.place.titlePicture.isNullOrEmpty() && !CreatePlace.place.title.isEmpty()
                    && !CreatePlace.place.latitude.isNaN() && !CreatePlace.place.longitude.isNaN()
                    && !CreatePlace.place.description.isNullOrEmpty()) {

                    viewModel.savePlace(CreatePlace.place)

                    fragmentManager?.createAndReplaceFragment(
                        Constants.FragmentTag.QR_CODE_FRAGMENT,
                        QrCodeFragment::class.java,
                        addToBackStack = true
                    )
                } else {
                    //TODO message please fill required boxes
                }
                true
            }
        }

    private fun resetStatusBar() =
        activity?.window?.apply {
            context?.let { context ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    statusBarColor = ContextCompat.getColor(context, R.color.colorStatusBarSdkPre23)
                }
            }
            statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }

    private fun generateQrCode() =
        viewModel.getUser()?.let {
            val qrCodeId = generateQrCodeId(it)
            val imageBitmap = generateQrCodeBitmap(qrCodeId)

            CreatePlace.place.qrCodeId = qrCodeId
            CreatePlace.qrCodeImage = imageBitmap
        }

    private fun generateQrCodeId(user: User): String {
        val timestamp = Timestamp(System.currentTimeMillis())
        return String(Hex.encodeHex(DigestUtils.md5(timestamp.toString() + user.id)))
    }

    private fun generateQrCodeBitmap(qrCodeId: String): Bitmap {
        val uri = QR_CODE_URI + qrCodeId
        val width = QR_CODE_DIM
        val height = QR_CODE_DIM
        val bitMatrix =
            MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, width, height)
        val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                imageBitmap.setPixel(x, y, color)
            }
        }
        return imageBitmap
    }
}