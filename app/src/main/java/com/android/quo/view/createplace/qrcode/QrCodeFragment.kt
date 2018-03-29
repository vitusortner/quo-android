package com.android.quo.view.createplace.qrcode

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.quo.R
import com.android.quo.util.CreatePlace
import com.android.quo.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_place.toolbar
import kotlinx.android.synthetic.main.fragment_qr_code_view.floatingActionButton
import kotlinx.android.synthetic.main.fragment_qr_code_view.qrCodeImageView

/**
 * Created by Jung on 05.12.17.
 */
class QrCodeFragment : BaseFragment(R.layout.fragment_qr_code_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        qrCodeImageView.setImageBitmap(CreatePlace.qrCodeImage)

        floatingActionButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, "uri")
            }.let {
                startActivity(it)
            }
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.inflateMenu(R.menu.qr_code_view_menu)
        toolbar.title = getString(R.string.qr_code)

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        toolbar.setOnMenuItemClickListener {
            // TODO
            true
        }
    }
}