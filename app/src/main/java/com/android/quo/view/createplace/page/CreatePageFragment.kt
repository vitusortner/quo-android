package com.android.quo.view.createplace.page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.CardView
import android.view.Gravity.TOP
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.android.quo.R
import com.android.quo.network.model.ServerComponent
import com.android.quo.util.Constants
import com.android.quo.util.Constants.Request.CREATE_PAGE_REQUEST_GALLERY
import com.android.quo.util.Constants.Request.PERMISSION_REQUEST_EXTERNAL_STORAGE
import com.android.quo.util.CreatePlace.components
import com.android.quo.util.extension.compressImage
import com.android.quo.util.extension.getImagePath
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.util.extension.subscribeOnComputation
import com.android.quo.view.BaseFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_create_page.floatingActionButton
import kotlinx.android.synthetic.main.fragment_create_page.generatedLayout
import kotlinx.android.synthetic.main.fragment_create_page.pagePreviewLayout
import kotlinx.android.synthetic.main.fragment_create_page.roundEditButton
import kotlinx.android.synthetic.main.fragment_create_page.roundGalleryButton
import java.io.File

/**
 * Created by Jung on 27.11.17.
 */
class CreatePageFragment : BaseFragment(R.layout.fragment_create_page) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                when (requestCode) {
                    CREATE_PAGE_REQUEST_GALLERY -> data?.data?.let { addImageToComponents(it) }
                }
            }
        } catch (e: Exception) {
            log.e("Error while selecting image.", e)
        }
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
                    .permissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .takeIf { it }
                    ?.run { openPhoneGallery() }
            }
        }
    }

    private fun setupClickListeners() {
        floatingActionButton.setOnClickListener {
            roundEditButton.visibility = VISIBLE
            roundGalleryButton.visibility = VISIBLE
        }

        roundEditButton.setOnClickListener {
            pagePreviewLayout.visibility = GONE
            roundEditButton.visibility = GONE
            roundGalleryButton.visibility = GONE

            val editText = createEditText().apply {
                setTag(id, generatedLayout.childCount)
            }
            generatedLayout.addView(createCardView(editText))
            components.add(
                ServerComponent(
                    null,
                    null,
                    editText.text.toString(),
                    generatedLayout.childCount - 1
                )
            )

            RxTextView.afterTextChangeEvents(editText)
                .subscribe { event ->
                    components
                        .filter { component -> component.position == event.view().getTag(id) }
                        .forEach { component -> component.text = event.view().text.toString() }
                }
                .addTo(compositeDisposable)

            pagePreviewLayout.visibility = GONE
        }

        pagePreviewLayout.setOnClickListener {
            roundEditButton.visibility = GONE
            roundGalleryButton.visibility = GONE
        }

        roundGalleryButton.setOnClickListener {
            roundEditButton.visibility = GONE
            roundGalleryButton.visibility = GONE

            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun createCardView(view: View) =
        CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                bottomMargin = 20
            }
            cardElevation = 15f
            addView(view)
        }

    private fun createEditText() =
        EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(10, 10, 10, 10)
            }
            imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
            gravity = TOP
            minLines = 10
            setSingleLine(false)
            setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
            setTextColor(ResourcesCompat.getColor(resources, R.color.oslo_gray, null))
        }

    private fun createImageView(drawable: Drawable) =
        ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 600)
            setImageDrawable(drawable)
        }

    private fun openPhoneGallery() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).let {
            requireActivity().startActivityForResult(it, CREATE_PAGE_REQUEST_GALLERY)
        }

    private fun addImageToComponents(uri: Uri) =
        uri.getImagePath(requireContext())?.let {
            val image = File(it)
            val imageDir =
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .absolutePath + Constants.IMAGE_DIR

            compressImage(image, imageDir) {
                pagePreviewLayout.visibility = GONE
                createBitmapDrawable(it).let { addComponentView(it) }
                addComponentToList(it.absolutePath)
            }
        }

    private fun addComponentToList(imagePath: String) =
        components.add(
            ServerComponent(
                id = null,
                picture = imagePath,
                text = null,
                position = generatedLayout.childCount - 1
            )
        )

    private fun addComponentView(bitmapDrawable: BitmapDrawable) {
        val cardView = createImageView(bitmapDrawable).let { createCardView(it) }
        generatedLayout.addView(cardView)
    }

    private fun createBitmapDrawable(file: File): BitmapDrawable {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        return BitmapDrawable(resources, bitmap)
    }

    private fun compressImage(image: File, imageDir: String, onSuccess: (File) -> Unit) {
        imageCompressor
            .compressImage(
                image,
                Constants.MAX_IMG_DIM,
                Constants.MAX_IMG_DIM,
                Constants.IMG_QUALITY,
                imageDir
            )
            .subscribeOnComputation()
            .observeOnUi()
            .subscribeBy(
                onNext = { onSuccess(it) },
                onError = { log.e("Error while compressing image", it) }
            )
    }

}
