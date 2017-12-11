package com.android.quo.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.quo.R
import com.android.quo.R.id.actionHome
import com.android.quo.R.id.actionPlaces
import com.android.quo.R.id.actionQrCode
import com.android.quo.view.home.HomeFragment
import com.android.quo.view.myplaces.MyPlacesFragment
import com.android.quo.view.place.PlaceFragment
import com.android.quo.view.qrcode.QrCodeScannerActivity
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigationView.selectedItemId = actionHome

        // set status bar color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBarSdkPre23)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.getStringExtra("extra") == "extra") {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, PlaceFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            actionQrCode -> {
                startActivity(Intent(this, QrCodeScannerActivity::class.java))
                false // because qr code scanner uses separate activity
                // TODO https://app.clickup.com/751518/751948/t/vtmj
            }
            actionHome -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content, HomeFragment())
                        .commit()
                true
            }
            actionPlaces -> {
                supportFragmentManager.beginTransaction()
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
