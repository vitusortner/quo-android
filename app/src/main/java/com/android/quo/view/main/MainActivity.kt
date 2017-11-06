package com.android.quo.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentActivity
import com.android.quo.R
import com.android.quo.R.id.*
import com.android.quo.view.home.HomeFragment
import com.android.quo.view.qrcode.QrCodeScannerFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigationView.selectedItemId = actionHome
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            actionQrCode -> {
                supportFragmentManager.beginTransaction().
                        replace(R.id.content, QrCodeScannerFragment()).
                        addToBackStack(null).commit()
                true
            }

            actionHome -> {
                supportFragmentManager.beginTransaction().
                        replace(R.id.content, HomeFragment()).
                        addToBackStack(null).commit()
                true
            }
            actionPlaces -> true
            else -> false
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
