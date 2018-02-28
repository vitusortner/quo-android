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
import com.android.quo.util.extension.addTo
import com.android.quo.util.extension.compressImage
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.permissionsGranted
import com.android.quo.util.extension.subscribeOnComputation
import com.android.quo.view.BaseFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_create_page.floatingActionButton
import kotlinx.android.synthetic.main.fragment_create_page.generatedLayout
import kotlinx.android.synthetic.main.fragment_create_page.pagePreviewLayout
import kotlinx.android.synthetic.main.fragment_create_page.roundEditButton
import kotlinx.android.synthetic.main.fragment_create_page.roundGalleryButton
import kotlinx.android.synthetic.main.fragment_create_page.view.generatedLayout
import java.io.File

/**
 * Created by Jung on 27.11.17.
 */
class CreatePageFragment : BaseFragment(R.layout.fragment_create_page) {

    // TODO wtf make this nice
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        floatingActionButton.setOnClickListener {
            roundEditButton.visibility = VISIBLE
            roundGalleryButton.visibility = VISIBLE
        }

        roundEditButton.setOnClickListener {
            pagePreviewLayout.visibility = GONE
            roundEditButton.visibility = GONE
            roundGalleryButton.visibility = GONE

            val editText = createEditText()
            editText.setTag(id, view.generatedLayout.childCount)
            view.generatedLayout.addView(createCardView(editText))
            components.add(
                ServerComponent(
                    null,
                    null,
                    editText.text.toString(),
                    view.generatedLayout.childCount - 1
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun createCardView(view: View): CardView? {
        context?.let { context ->
            val layoutParams = LinearLayout.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT
            )
            layoutParams.bottomMargin = 20
            val cardView = CardView(context)
            cardView.layoutParams = layoutParams
            cardView.cardElevation = 15f
            cardView.addView(view)
            return cardView
        }
        return null
    }

    private fun createEditText(): EditText {
        val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        layoutParams.setMargins(10, 10, 10, 10)
        val editText = EditText(context)
        editText.layoutParams = layoutParams
        editText.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        editText.gravity = TOP
        editText.minLines = 10
        editText.setSingleLine(false)
        editText.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        editText.setTextColor(resources.getColor(R.color.colorTextGray))
        return editText
    }

    private fun createImageView(drawable: Drawable): ImageView {
        val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 600)
        val imageView = ImageView(context)
        imageView.layoutParams = layoutParams
        imageView.setImageDrawable(drawable)
        return imageView
    }

    private fun openPhoneGallery() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).let {
            activity?.startActivityForResult(it, CREATE_PAGE_REQUEST_GALLERY)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                when (requestCode) {
                    CREATE_PAGE_REQUEST_GALLERY -> {
                        data?.data?.let { imageUri ->
                            val image = File(getPath(imageUri))
                            val imageDir =
                                Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                    .absolutePath + Constants.IMAGE_DIR

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
                                    onNext = {
                                        val bitmap = BitmapFactory.decodeFile(it.absolutePath)
                                        val bitmapDrawable = BitmapDrawable(resources, bitmap)
                                        pagePreviewLayout.visibility = GONE
                                        generatedLayout.addView(
                                            createCardView(
                                                createImageView(
                                                    bitmapDrawable
                                                )
                                            )
                                        )
                                        components.add(
                                            ServerComponent(
                                                id = null,
                                                picture = it.absolutePath,
                                                text = null,
                                                position = generatedLayout.childCount - 1
                                            )
                                        )
                                    },
                                    onError = {
                                        log.e("Error while compressing image", it)
                                    }
                                )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log.e("Error while selecting image.", e)
        }
    }

    private fun getPath(uri: Uri): String {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver?.query(
            uri, mediaStoreData, null,
            null, null
        )

        cursor?.let { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(mediaStoreData[0])
                result = cursor.getString(columnIndex)
            }
        }
        cursor?.close()

        if (result == null) {
            result = getString(R.string.not_found)
        }
        return result as String
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
                    ?.permissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ?.takeIf { it }
                    ?.run { openPhoneGallery() }
            }
        }
    }
}
