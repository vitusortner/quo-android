package com.android.quo.view.createplace.qrcode

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.util.CreatePlace
import kotlinx.android.synthetic.main.fragment_place.toolbar
import kotlinx.android.synthetic.main.fragment_qr_code_view.floatingActionButton
import kotlinx.android.synthetic.main.fragment_qr_code_view.qrCodeImageView

/**
 * Created by Jung on 05.12.17.
 */
class QrCodeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_qr_code_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        qrCodeImageView.setImageBitmap(CreatePlace.qrCodeImage)

        floatingActionButton.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type = "image/jpeg"
            sendIntent.putExtra(Intent.EXTRA_STREAM, "uri")
            startActivity(sendIntent)
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.qr_code_view_menu)
        toolbar.title = getString(R.string.qr_code)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        toolbar.setOnMenuItemClickListener {
            // TODO
            true
        }
    }
}