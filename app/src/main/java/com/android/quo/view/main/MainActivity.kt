package com.android.quo.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.android.quo.R
import com.android.quo.R.id.actionHome
import com.android.quo.R.id.actionPlaces
import com.android.quo.R.id.actionQrCode
import com.android.quo.view.home.HomeFragment
import com.android.quo.view.myplaces.MyPlacesFragment
import com.android.quo.view.qrcode.QrCodeScannerFragment
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigationView.selectedItemId = actionHome
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val manager = supportFragmentManager

        when (item.itemId) {
            actionQrCode -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, QrCodeScannerFragment())
                        .addToBackStack(null)
                        .commit()
                true
            }

            actionHome -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, HomeFragment())
                        .commit()
                true
            }
            actionPlaces -> {
                manager.beginTransaction()
                        .replace(R.id.content, MyPlacesFragment())
                        .commit()
                true
            }
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
