package com.android.quo.view.myplaces.createplace

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.android.quo.model.ServerComponent
import com.android.quo.view.myplaces.createplace.CreatePlace.components
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_create_page.floatingActionButton
import kotlinx.android.synthetic.main.fragment_create_page.generatedLayout
import kotlinx.android.synthetic.main.fragment_create_page.placePreviewCardView
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
                    placePreviewCardView.visibility = GONE
                    val editText = createEditText()
                    editText.setTag(id, view.generatedLayout.childCount + 1)
                    view.generatedLayout.addView(createCardView(editText))
                    components.add(ServerComponent(view.generatedLayout.childCount.toString(), "", "", view.generatedLayout.childCount - 1))

                    compositeDisposable.add(RxTextView.afterTextChangeEvents(editText)
                            .subscribe {
                                components
                                        .filter { c -> c.id == it.view().getTag(id).toString() }
                                        .forEach { c ->
                                            c.text = it.view().text.toString()
                                        }
                            })
                })

        compositeDisposable.add(RxView.clicks(roundGalleryButton)
                .subscribe { openPhoneGallery() })

        generateQrCodeObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    //TODO 
                }


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
        editText.setBackgroundColor(resources.getColor(R.color.colorTextWhite))
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

                    placePreviewCardView.visibility = GONE
                    generatedLayout.addView(createCardView(createImageView(bitmapDrawable)))
                    //TODO create name for image
                    components.add(ServerComponent(generatedLayout.childCount.toString(), "name", "", generatedLayout.childCount - 1))

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


    private fun generateQrCodeObservable(): Observable<Bitmap> {
        return Observable.create {
            var data = ""
            val width = 1024
            val height = 1024
            var multiFormatWriter = MultiFormatWriter()
            val bm = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, width, height)
            val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (i in 0 until width) {
                for (j in 0 until height) {
                    imageBitmap.setPixel(i, j, if (bm.get(i, j)) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}
