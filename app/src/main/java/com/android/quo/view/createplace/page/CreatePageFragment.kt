package com.android.quo.view.createplace.page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
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
import com.android.quo.util.CreatePlace.components
import com.android.quo.view.BaseFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import id.zelory.compressor.Compressor
import io.reactivex.disposables.CompositeDisposable
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

    private var compositeDisposable = CompositeDisposable()
    private val RESULT_GALLERY = 2
    private val PERMISSION_REQUEST_EXTERNAL_STORAGE = 101

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

            compositeDisposable.add(RxTextView.afterTextChangeEvents(editText)
                .subscribe {
                    components
                        .filter { c -> c.position == it.view().getTag(id) }
                        .forEach { c ->
                            c.text = it.view().text.toString()
                        }
                })
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
        this.context?.let { context ->
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
        val editText = EditText(this.context)
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
        val imageView = ImageView(this.context)
        imageView.layoutParams = layoutParams
        imageView.setImageDrawable(drawable)
        return imageView

    }

    /**
     * open the phone gallery to select the header image
     */
    private fun openPhoneGallery() {

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity?.startActivityForResult(galleryIntent, RESULT_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data?.data

                    val compressedImage = compressImage(File(selectedImageUri?.let { getPath(it) }))
                    val bitmap = BitmapFactory.decodeFile(compressedImage.absolutePath)
                    val bitmapDrawable = BitmapDrawable(resources, bitmap)
                    pagePreviewLayout.visibility = GONE
                    generatedLayout.addView(createCardView(createImageView(bitmapDrawable)))
                    components.add(
                        ServerComponent(
                            null,
                            compressedImage.absolutePath,
                            null,
                            generatedLayout.childCount - 1
                        )
                    )
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

    private fun compressImage(file: File): File {
        return Compressor(this.context)
            .setMaxWidth(640)
            .setMaxHeight(480)
            .setQuality(75)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .setDestinationDirectoryPath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).absolutePath + "/Quo"
            )
            .compressToFile(file)
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
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        openPhoneGallery()
                    }
                }
            }
        }
    }

}