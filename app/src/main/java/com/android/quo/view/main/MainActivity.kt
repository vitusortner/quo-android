package com.android.quo.view.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.android.quo.R
import com.android.quo.R.id.actionHome
import com.android.quo.R.id.actionQrCode
import com.android.quo.R.id.actionPlaces
import com.android.quo.view.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigationView.selectedItemId = actionHome
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            actionQrCode -> true

            actionHome -> {
                val manager = supportFragmentManager
                manager.beginTransaction().replace(R.id.content, HomeFragment()).commit()
            }
            actionPlaces -> true
            else -> false
        }
        true
    }

}
