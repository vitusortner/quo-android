package com.android.quo.view.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.android.quo.R
import com.android.quo.R.id.*
import com.android.quo.view.timeline.HomeFragment
import kotlinx.android.synthetic.main.bottom_navigation_view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_navigation_view)

        val bottomNavigationView = bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavigationView.selectedItemId = actionHome
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            actionQrCode -> {

                return@OnNavigationItemSelectedListener true
            }
            actionHome -> {
                val manager = supportFragmentManager
                manager.beginTransaction().replace(R.id.frame, HomeFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            actionPlaces -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

}
