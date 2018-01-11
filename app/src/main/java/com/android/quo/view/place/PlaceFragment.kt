package com.android.quo.view.place

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.Application
import com.android.quo.R
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants
import com.android.quo.util.extension.toPx
import com.android.quo.view.place.info.InfoFragment
import com.android.quo.viewmodel.PlaceViewModel
import com.android.quo.viewmodel.factory.PlaceViewModelFactory
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.jakewharton.rxbinding2.view.RxView
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceFragment : Fragment() {

    private val TAG = javaClass.simpleName

    private val RESULT_GALLERY = 201
    private val RESULT_CAMERA = 202
    private val PERMISSION_REQUEST_CAMERA = 101
    private val PERMISSION_REQUEST_EXTERNAL_STORAGE = 102

    private var place: Place? = null
    private var currentPhotoPath: String? = null

    private val compositDisposable = CompositeDisposable()

    private val pictureRepository = Application.pictureRepository
    private val uploadService = Application.uploadService

    private lateinit var viewModel: PlaceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show bottom navigation bar when coming from fragment with hidden bottom nav bar
        if (activity?.bottomNavigationView?.visibility == View.GONE) {
            activity?.bottomNavigationView?.visibility = View.VISIBLE
        }

        viewModel = ViewModelProviders
                .of(this, PlaceViewModelFactory(uploadService, pictureRepository))
                .get(PlaceViewModel::class.java)

        place = arguments?.getParcelable("place")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout.setupWithViewPager(viewPager)

        setupToolbar()
        setupViewPager()
        setupFab()
    }

    private fun setupFab() {
        floatingActionButton.hide()

        compositDisposable.add(RxView.clicks(floatingActionButton)
                .subscribe {
                    openBottomSheet()
                }
        )
    }

    private fun openBottomSheet() {
        context?.let { context ->
            val bottomSheetDialog = BottomSheetDialog(context)
            val layout = activity?.layoutInflater?.inflate(R.layout.bottom_sheet_add_image, null)
            layout?.let {
                setupBottomSheetButtons(it)

                bottomSheetDialog.setContentView(it)
                bottomSheetDialog.show()
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                context?.let {
                    val cameraResult = ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA)
                    val readResult = ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)
                    val writeResult = ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    if (cameraResult == PackageManager.PERMISSION_GRANTED &&
                            readResult == PackageManager.PERMISSION_GRANTED &&
                            writeResult == PackageManager.PERMISSION_GRANTED) {
                        openCamera()
                    }
                }
            }
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                context?.let {
                    val result = ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        openGallery()
                    }
                }
            }
        }
    }

    private fun setupBottomSheetButtons(layout: View) {
        compositDisposable.add(RxView.clicks(layout.cameraButton)
                .subscribe {
                    requestPermissions(
                            arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                            ),
                            PERMISSION_REQUEST_CAMERA
                    )
                }
        )

        compositDisposable.add(RxView.clicks(layout.galleryButton)
                .subscribe {
                    requestPermissions(
                            arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            PERMISSION_REQUEST_EXTERNAL_STORAGE
                    )
                }
        )
    }

    private fun openGallery() {
        val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity?.startActivityForResult(galleryIntent, RESULT_GALLERY)
    }

    private fun openCamera() {
        context?.let { context ->
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (cameraIntent.resolveActivity(context.packageManager) != null) {
                val image = createImageFile()

                val imageUri = FileProvider.getUriForFile(
                        context,
                        "com.android.quo",
                        image
                )

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

                activity?.startActivityForResult(cameraIntent, RESULT_CAMERA)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val storageDir = Environment
                .getExternalStoragePublicDirectory("${Environment.DIRECTORY_PICTURES}${Constants.IMAGE_DIR}")

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "IMG_$timeStamp"
        val image = File.createTempFile(imageName, ".jpg", storageDir)

        currentPhotoPath = image.absolutePath

        return image
    }

    private fun compressImage(image: File, completionHandler: (File?) -> Unit) {
        Compressor(context)
                .setMaxWidth(2000)
                .setMaxHeight(2000)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFileAsFlowable(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    completionHandler(it)
                }, {
                    Log.e(TAG, "Error while compressing image: $it")
                    completionHandler(null)
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_GALLERY) {
                place?.id?.let { placeId ->
                    val selectedImageUri = data?.data
                    val image = File(selectedImageUri?.let { getPath(it) })

                    compressImage(image) {
                        it?.let {
                            viewModel.uploadImage(it, placeId)
                        }
                    }
                }
//                 bottomSheetDialog.hide()
            } else if (requestCode == RESULT_CAMERA) {
                place?.id?.let { placeId ->
                    currentPhotoPath?.let {
                        val image = File(it)

                        compressImage(image) {
                            it?.let {
                                viewModel.uploadImage(it, placeId)
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

        cursor?.let { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(mediaStoreData[0])
                result = cursor.getString(columnIndex)
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

        Glide.with(this.context)
                .load(imageUrl)
                .into(imageView)

        compositDisposable.add(
                RxToolbar.navigationClicks(toolbar)
                        .subscribe {
                            activity?.onBackPressed()
                        }
        )

        compositDisposable.add(
                RxToolbar.itemClicks(toolbar)
                        .subscribe {
                            val bundle = Bundle()
                            bundle.putParcelable("place", place)
                            val fragment = InfoFragment()
                            fragment.arguments = bundle

                            fragmentManager?.beginTransaction()
                                    ?.add(R.id.content, fragment)
                                    ?.addToBackStack(null)
                                    ?.commit()
                        }
        )

//        this.context?.let {
//            // TODO resolve log spam https://stackoverflow.com/questions/38913215/requestlayout-improperly-called-by-collapsingtoolbarlayout
//            // set tab layout colors denpendent on how far scrolled
//            var scrollRange = -1
//
//            appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//                // set shadow
//                ViewCompat.setElevation(appBarLayout, 4f.toPx(it).toFloat())
//
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.totalScrollRange
//                }
//                if (scrollRange + verticalOffset <= 150) {
//                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextBlack))
//                    tabLayout.setTabTextColors(resources.getColor(R.color.colorTextBlack),
//                            resources.getColor(R.color.colorTextBlack))
//                } else {
//                    tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTextWhite))
//                    tabLayout.setTabTextColors(resources.getColor(R.color.colorTextWhite),
//                            resources.getColor(R.color.colorTextWhite))
//                }
//            }
//        }
    }

    private fun setupViewPager() {
        this.context?.let { context ->
            place?.id?.let { placeId ->
                viewPager.adapter = PlacePagerAdapter(childFragmentManager, context, placeId)
            }
        }

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

    override fun onDestroy() {
        super.onDestroy()

        compositDisposable.dispose()
    }
}