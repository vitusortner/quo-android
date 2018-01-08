package com.android.quo.view.createplace

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_place.toolbar
import kotlinx.android.synthetic.main.fragment_qr_code_view.floatingActionButton
import kotlinx.android.synthetic.main.fragment_qr_code_view.qrCodeImageView


/**
 * Created by Jung on 05.12.17.
 */
class QrCodeFragment : Fragment() {
    private val compositDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_qr_code_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        qrCodeImageView.setImageBitmap(CreatePlace.qrCodeImage)

        compositDisposable.add(
                RxView.clicks(floatingActionButton)
                        .subscribe {
                            val sendIntent = Intent()
                            sendIntent.action = Intent.ACTION_SEND
                            sendIntent.type = "image/jpeg"
                            sendIntent.putExtra(Intent.EXTRA_STREAM, "uri")
                            this.startActivity(sendIntent)
                        })
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.qr_code_view_menu)
        toolbar.title = getString(R.string.qr_code)

        compositDisposable.add(
                RxToolbar.navigationClicks(toolbar)
                        .subscribe {
                            activity?.onBackPressed()
                        }
        )

        compositDisposable.add(
                RxToolbar.itemClicks(toolbar)
                        .subscribe {
                            //TODO open info box
                        }
        )
    }


    private fun generateQrCode(data: String): Bitmap {
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
        return imageBitmap
    }

    override fun onDestroy() {
        super.onDestroy()
        compositDisposable.dispose()
    }

}