package com.android.quo.view.place

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.view.View
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants
import com.android.quo.util.Constants.Extra
import com.android.quo.util.Constants.IMG_QUALITY
import com.android.quo.util.Constants.MAX_IMG_DIM
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_CAMERA
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_EXTERNAL_STORAGE
import com.android.quo.util.Constants.Request.REQUEST_CAMERA
import com.android.quo.util.Constants.Request.REQUEST_GALLERY
import com.android.quo.util.extension.addFragment
import com.android.quo.util.extension.compressImage
import com.android.quo.util.extension.getImagePath
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.util.extension.subscribeOnComputation
import com.android.quo.util.extension.toPx
import com.android.quo.view.BaseFragment
import com.android.quo.view.place.info.InfoFragment
import com.android.quo.viewmodel.PlaceViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.bottom_sheet_add_image.view.cameraButton
import kotlinx.android.synthetic.main.bottom_sheet_add_image.view.galleryButton
import kotlinx.android.synthetic.main.fragment_place.appBarLayout
import kotlinx.android.synthetic.main.fragment_place.floatingActionButton
import kotlinx.android.synthetic.main.fragment_place.imageView
import kotlinx.android.synthetic.main.fragment_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar
import kotlinx.android.synthetic.main.fragment_place.viewPager
import org.koin.android.architecture.ext.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceFragment : BaseFragment(R.layout.fragment_place) {

    private val viewModel by viewModel<PlaceViewModel>()

    private var place: Place? = null
    private var currentPhotoPath: String? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val isPhotoUploadAllowed: Boolean by lazy {
        place?.let { place -> place.isPhotoUploadAllowed?.let { it || place.isHost } } ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Show bottom navigation bar when coming from fragment with hidden bottom nav bar
        requireActivity().bottomNavigationView
            .let {
                if (it.visibility == View.GONE) it.visibility = View.VISIBLE
            }
        place = arguments?.getParcelable(Extra.PLACE_EXTRA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout.setupWithViewPager(viewPager)

        setupToolbar()
        setupViewPager()
        setupFab()
        setupBottomSheetDialog()
    }

    private fun setupFab() {
        floatingActionButton.hide()

        if (isPhotoUploadAllowed) {
            floatingActionButton.setOnClickListener {
                bottomSheetDialog?.show()
            }
        }
    }

    private fun setupBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_image, null)

        setupBottomSheetButtons(view)
        bottomSheetDialog?.setContentView(view)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                requireContext()
                    .permissionsGranted(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .takeIf { it }
                    ?.run { openCamera() }
            }
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                requireContext()
                    .permissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .takeIf { it }
                    ?.run { openGallery() }
            }
        }
    }

    private fun setupBottomSheetButtons(view: View) {
        view.cameraButton.setOnClickListener {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                PERMISSION_REQUEST_CAMERA
            )
        }

        view.galleryButton.setOnClickListener {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun openGallery() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).let {
            requireActivity().startActivityForResult(it, REQUEST_GALLERY)
        }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { intent ->
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                val image = createImageFile()
                val imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.android.quo",
                    image
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                requireActivity().startActivityForResult(intent, REQUEST_CAMERA)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val path = "${Environment.DIRECTORY_PICTURES}${Constants.IMAGE_DIR}"
        val storageDir = Environment.getExternalStoragePublicDirectory(path)

        if (!storageDir.exists()) storageDir.mkdirs()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "IMG_$timeStamp"
        val image = File.createTempFile(imageName, ".jpg", storageDir)
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> data?.data?.getImagePath(requireContext())?.let {
                    compressAndUploadImage(it)
                }
                REQUEST_CAMERA -> currentPhotoPath?.let { compressAndUploadImage(it) }
            }
        }
    }

    private fun compressAndUploadImage(filePath: String) {
        place?.id?.let { placeId ->
            val image = File(filePath)

            imageCompressor
                .compressImage(image, MAX_IMG_DIM, MAX_IMG_DIM, IMG_QUALITY)
                .subscribeOnComputation()
                .observeOnUi()
                .subscribeBy(
                    onNext = {
                        // TODO refresh gallery
                        viewModel.uploadImage(it, placeId)
                        bottomSheetDialog?.hide()
                    },
                    onError = {
                        log.e("Error while compressing image: $it")
                    }
                )
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.menu_place)
        toolbar.title = place?.title ?: ""

        // TODO else show placeholder https://app.clickup.com/751518/751948/t/w5hm
        val imageUrl = place?.titlePicture ?: ""

        imageLoader
            .load(imageUrl)
            .into(imageView)

        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        toolbar.setOnMenuItemClickListener {
            val bundle = Bundle().apply { putParcelable(Extra.PLACE_EXTRA, place) }
            val fragment = InfoFragment().apply { arguments = bundle }
            requireFragmentManager().addFragment(fragment, true)
            true
        }

        // TODO resolve log spam https://stackoverflow.com/questions/38913215/requestlayout-improperly-called-by-collapsingtoolbarlayout
        // set tab layout colors denpendent on how far scrolled
        var scrollRange = -1

        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            // set shadow
            ViewCompat.setElevation(appBarLayout, 4f.toPx(requireContext()).toFloat())

            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (scrollRange + verticalOffset <= 150) {
                tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.black))
                tabLayout.setTabTextColors(
                    resources.getColor(R.color.black),
                    resources.getColor(R.color.black)
                )
            } else {
                tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.white))
                tabLayout.setTabTextColors(
                    resources.getColor(R.color.white),
                    resources.getColor(R.color.white)
                )
            }
        }
    }

    private fun setupViewPager() {
        place?.id?.let { placeId ->
            viewPager.adapter = PlacePagerAdapter(childFragmentManager, requireContext(), placeId)
        }
        if (isPhotoUploadAllowed) {
            viewPager.addOnPageChangeListener(observer)
        }
    }

    private val observer = object : ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            if (position == 0) {
                floatingActionButton.hide()
            } else {
                floatingActionButton.show()
            }
        }
    }

}