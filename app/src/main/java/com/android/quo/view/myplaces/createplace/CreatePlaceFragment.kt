package com.android.quo.view.myplaces.createplace


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.networking.model.ServerPlace
import com.android.quo.viewmodel.CreatePlaceViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_create_place.createPlaceViewPager
import kotlinx.android.synthetic.main.fragment_create_place.tabLayout
import kotlinx.android.synthetic.main.fragment_place.toolbar
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.sql.Timestamp
import id.zelory.compressor.Compressor


/**
 * Created by Jung on 27.11.17.
 */

class CreatePlaceFragment : Fragment() {
    private val compositDisposable = CompositeDisposable()
    lateinit var place: ServerPlace
    private lateinit var viewModel: CreatePlaceViewModel


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(CreatePlaceViewModel::class.java)

        this.context?.let {
            createPlaceViewPager.adapter = CreatePlacePagerAdapter(childFragmentManager, it)
        }
        tabLayout.setupWithViewPager(createPlaceViewPager)

        /**
         * change status bar color
         */
        activity?.window?.statusBarColor = resources.getColor(R.color.colorAccentDark)

        setupToolbar()

        generateQrCodeObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.inflateMenu(R.menu.create_place_menu)
        toolbar.title = getString(R.string.new_place)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorTextWhite))

        compositDisposable.add(
                RxToolbar.navigationClicks(toolbar)
                        .subscribe {
                            activity?.onBackPressed()
                        }
        )

        compositDisposable.add(
                RxToolbar.itemClicks(toolbar)
                        .subscribe {
                            viewModel.savePlace()
                            fragmentManager?.beginTransaction()
                                    ?.replace(R.id.content, QrCodeFragment())
                                    ?.addToBackStack(null)
                                    ?.commit()
                        }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStop() {
        super.onStop()

        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
    }

    override fun onDestroy() {
        super.onDestroy()

        compositDisposable.dispose()
    }

    private fun generateQrCodeObservable(): Observable<Bitmap> {
        return Observable.create {
            val timestamp = Timestamp(System.currentTimeMillis())
            //TODO change with live data
            val userId = "10"
            val qrCodeId = String(Hex.encodeHex(DigestUtils.md5(timestamp.toString() + userId)))
            val uri = "quo://" + String(Hex.encodeHex(DigestUtils.md5(timestamp.toString() + userId)))
            val width = 1024
            val height = 1024
            val multiFormatWriter = MultiFormatWriter()
            val bm = multiFormatWriter.encode(uri, BarcodeFormat.QR_CODE, width, height)
            val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (i in 0 until width) {
                for (j in 0 until height) {
                    imageBitmap.setPixel(i, j, if (bm.get(i, j)) Color.BLACK else Color.WHITE)
                }
            }

            CreatePlace.qrCode = imageBitmap
            CreatePlace.place.qrCodeId = qrCodeId
        }
    }

}