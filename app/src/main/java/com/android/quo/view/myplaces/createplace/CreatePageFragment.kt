package com.android.quo.view.myplaces.createplace

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.util.Log
import android.view.Gravity.TOP
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.android.quo.R
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_create_page.floatingActionButton
import kotlinx.android.synthetic.main.fragment_create_page.generatedLayout
import kotlinx.android.synthetic.main.fragment_create_page.pagePreviewCardView
import kotlinx.android.synthetic.main.fragment_create_page.roundEditButton
import kotlinx.android.synthetic.main.fragment_create_page.roundGalleryButton
import kotlinx.android.synthetic.main.fragment_create_page.view.generatedLayout


/**
 * Created by Jung on 27.11.17.
 */

class CreatePageFragment : Fragment() {
    private var compositeDisposable = CompositeDisposable()
    private val RESULT_GALLERY = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable.add(RxView.clicks(floatingActionButton)
                .subscribe {
                    roundEditButton.visibility = VISIBLE
                    roundGalleryButton.visibility = VISIBLE
                })

        compositeDisposable.add(RxView.clicks(roundEditButton)
                .subscribe {
                    pagePreviewCardView.visibility = GONE
                    view.generatedLayout.addView(createCardView(createEditText()))
                })

        compositeDisposable.add(RxView.clicks(pagePreviewCardView)
                .subscribe {
                    roundEditButton.visibility = GONE
                    roundGalleryButton.visibility = GONE
                })

        compositeDisposable.add(RxView.clicks(roundGalleryButton)
                .subscribe { openPhoneGallery() })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun createCardView(view: View): CardView? {
        this.context?.let { context ->
            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT,
                    MATCH_PARENT)
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        this.activity?.let { activity ->
            activity.startActivityForResult(galleryIntent, RESULT_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == RESULT_GALLERY) {
                    val selectedImageUri = data?.data
                    val path = selectedImageUri?.let { getPath(it) }
                    val bitmap = BitmapFactory.decodeFile(path)
                    // set bitmap half size
                    val scaledBitmap = Bitmap.createScaledBitmap(
                            bitmap, bitmap.width / 2, bitmap.height / 2, false)
                    val bitmapDrawable = BitmapDrawable(resources, scaledBitmap)

                    pagePreviewCardView.visibility = GONE
                    generatedLayout.addView(createCardView(createImageView(bitmapDrawable)))

                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
    }

    private fun getPath(uri: Uri): String {
        var result: String? = null
        val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.context?.let { context ->
            context.contentResolver?.query(uri, mediaStoreData,
                    null, null, null)
        }

        cursor?.let { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(mediaStoreData[0])
                result = cursor.getString(columnIndex)
                cursor.close()
            }
        }

        if (result == null) {
            result = getString(R.string.not_found)
        }
        return result as String
    }

}
