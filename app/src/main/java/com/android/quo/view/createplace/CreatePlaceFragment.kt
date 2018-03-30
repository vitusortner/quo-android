package com.android.quo.view.createplace

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.android.quo.R
import com.android.quo.util.Constants
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_EXTERNAL_STORAGE
import com.android.quo.util.CreatePlace
import com.android.quo.util.QrCode
import com.android.quo.util.extension.async
import com.android.quo.util.extension.createAndReplaceFragment
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.view.BaseFragment
import com.android.quo.view.createplace.qrcode.QrCodeFragment
import com.android.quo.viewmodel.CreatePlaceViewModel
import kotlinx.android.synthetic.main.fragment_create_place.createPlaceViewPager
import kotlinx.android.synthetic.main.fragment_create_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar
import org.koin.android.architecture.ext.viewModel

/**
 * Created by Jung on 27.11.17.
 */
class CreatePlaceFragment : BaseFragment(R.layout.fragment_create_place) {

    private val viewModel by viewModel<CreatePlaceViewModel>(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createPlaceViewPager.adapter =
                CreatePlacePagerAdapter(childFragmentManager, requireContext())

        tabLayout.setupWithViewPager(createPlaceViewPager)

        setupToolbar()

        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_EXTERNAL_STORAGE
        )

        async { generateQrCode() }
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
        CreatePlace.reset()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                requireContext()
                    .permissionsGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .takeIf { !it }
                    ?.run { requireFragmentManager().popBackStack() }
            }
        }
    }

    // TODO required?
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setupStatusBar() =
        requireActivity().window.apply {
            decorView.systemUiVisibility = 0
            statusBarColor = resources.getColor(R.color.pine_green)
        }

    private fun setupToolbar() =
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            inflateMenu(R.menu.menu_create_place)
            title = getString(R.string.new_place)
            setTitleTextColor(resources.getColor(R.color.white))

            setNavigationOnClickListener { requireActivity().onBackPressed() }

            setOnMenuItemClickListener {
                if (!CreatePlace.place.titlePicture.isNullOrEmpty() && !CreatePlace.place.title.isEmpty()
                    && !CreatePlace.place.latitude.isNaN() && !CreatePlace.place.longitude.isNaN()
                    && !CreatePlace.place.description.isNullOrEmpty()
                ) {

                    viewModel.savePlace(CreatePlace.place)

                    requireFragmentManager().createAndReplaceFragment(
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
        requireActivity().window.apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_haze)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                statusBarColor =
                        ContextCompat.getColor(requireContext(), R.color.silver)
            }
            statusBarColor = resources.getColor(R.color.black_haze)
        }

    private fun generateQrCode() =
        viewModel.getUser()?.let {
            val qrCodeId = QrCode.createId(it)
            val imageBitmap = QrCode.createBitmap(qrCodeId)

            CreatePlace.place.qrCodeId = qrCodeId
            CreatePlace.qrCodeImage = imageBitmap
        }

}