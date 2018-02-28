package com.android.quo.view.place

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_CAMERA
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_EXTERNAL_STORAGE
import com.android.quo.util.Constants.Request.REQUEST_CAMERA
import com.android.quo.util.Constants.Request.REQUEST_GALLERY
import com.android.quo.util.extension.addFragment
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.util.extension.toPx
import com.android.quo.view.BaseFragment
import com.android.quo.view.place.info.InfoFragment
import com.android.quo.viewmodel.PlaceViewModel
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    private val viewModel by viewModel<PlaceViewModel>(false)

    private var place: Place? = null
    private var currentPhotoPath: String? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val isPhotoUploadAllowed: Boolean by lazy {
        place?.let { place ->
            place.isPhotoUploadAllowed?.let { it || place.isHost }
        } ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Show bottom navigation bar when coming from fragment with hidden bottom nav bar
        if (activity?.bottomNavigationView?.visibility == View.GONE) {
            activity?.bottomNavigationView?.visibility = View.VISIBLE
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

    private fun setupBottomSheetDialog() =
        context?.let { context ->
            bottomSheetDialog = BottomSheetDialog(context)
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
                context
                    ?.permissionsGranted(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    ?.takeIf { it }
                    ?.run { openCamera() }
            }
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                context
                    ?.permissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ?.takeIf { it }
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
            activity?.startActivityForResult(it, REQUEST_GALLERY)
        }

    private fun openCamera() =
        context?.let { context ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { intent ->
                if (intent.resolveActivity(context.packageManager) != null) {
                    val image = createImageFile()
                    val imageUri = FileProvider.getUriForFile(
                        context,
                        "com.android.quo",
                        image
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    activity?.startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }


    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val storageDir = Environment
            .getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}${Constants.IMAGE_DIR}")

        if (!storageDir.exists()) storageDir.mkdirs()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "IMG_$timeStamp"
        val image = File.createTempFile(imageName, ".jpg", storageDir)
        currentPhotoPath = image.absolutePath
        return image
    }

    // TODO make this a static helper function
    private fun compressImage(image: File, completionHandler: (File?) -> Unit) {
        Compressor(context)
            .setMaxWidth(640)
            .setMaxHeight(640)
            .setQuality(75)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .compressToFileAsFlowable(image)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { completionHandler(it) },
                {
                    log.e("Error while compressing image: $it")
                    completionHandler(null)
                }
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    place?.id?.let { placeId ->
                        val selectedImageUri = data?.data
                        val image = File(selectedImageUri?.let { getPath(it) })

                        compressImage(image) {
                            it?.let {
                                viewModel.uploadImage(it, placeId)
                                // TODO refresh gallery
                                bottomSheetDialog?.hide()
                            }
                        }
                    }
                }
                REQUEST_CAMERA -> {
                    place?.id?.let { placeId ->
                        currentPhotoPath?.let {
                            val image = File(it)

                            compressImage(image) {
                                it?.let {
                                    viewModel.uploadImage(it, placeId)
                                    // TODO refresh gallery
                                    // https://app.clickup.com/751518/751948/t/xazx
                                    // maybe composit completionHandler to uploadImage function and update gallery then
                                    bottomSheetDialog?.hide()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getPath(uri: Uri): String? {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver?.query(uri, mediaStoreData, null, null, null)

        cursor?.apply {
            if (moveToFirst()) {
                val columnIndex = getColumnIndexOrThrow(mediaStoreData[0])
                result = getString(columnIndex)
            }
        }
        cursor?.close()
        return result
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.place_menu)
        toolbar.title = place?.title ?: ""

        // TODO else show placeholder https://app.clickup.com/751518/751948/t/w5hm
        val imageUrl = place?.titlePicture ?: ""

        imageLoader
            .load(imageUrl)
            .into(imageView)

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        toolbar.setOnMenuItemClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Extra.PLACE_EXTRA, place)
            val fragment = InfoFragment()
            fragment.arguments = bundle

            fragmentManager?.addFragment(fragment, true)
            true
        }

        context?.let {
            // TODO resolve log spam https://stackoverflow.com/questions/38913215/requestlayout-improperly-called-by-collapsingtoolbarlayout
            // set tab layout colors denpendent on how far scrolled
            var scrollRange = -1

            appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                // set shadow
                ViewCompat.setElevation(appBarLayout, 4f.toPx(it).toFloat())

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset <= 150) {
                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextBlack))
                    tabLayout.setTabTextColors(
                        resources.getColor(R.color.colorTextBlack),
                        resources.getColor(R.color.colorTextBlack)
                    )
                } else {
                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextWhite))
                    tabLayout.setTabTextColors(
                        resources.getColor(R.color.colorTextWhite),
                        resources.getColor(R.color.colorTextWhite)
                    )
                }
            }
        }
    }

    private fun setupViewPager() {
        context?.let { context ->
            place?.id?.let { placeId ->
                viewPager.adapter = PlacePagerAdapter(childFragmentManager, context, placeId)
            }
        }

        if (isPhotoUploadAllowed) {
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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
            })
        }
    }
}