package com.android.quo

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.ContextCompat
import android.view.View
import com.android.quo.R.id.actionHome
import com.android.quo.R.id.actionPlaces
import com.android.quo.R.id.actionQrCode
import com.android.quo.db.entity.Place
import com.android.quo.util.Constants.Extra
import com.android.quo.util.Constants.FragmentTag.HOME_FRAGMENT
import com.android.quo.util.Constants.FragmentTag.MY_PLACES_FRAGMENT
import com.android.quo.util.Constants.FragmentTag.PLACE_FRAGMENT
import com.android.quo.util.extension.createAndReplaceFragment
import com.android.quo.view.BaseActivity
import com.android.quo.view.home.HomeFragment
import com.android.quo.view.login.LoginActivity
import com.android.quo.view.myplaces.MyPlacesFragment
import com.android.quo.view.place.PlaceFragment
import com.android.quo.view.qrcode.QrCodeScannerActivity
import com.android.quo.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import org.koin.android.architecture.ext.viewModel

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigationView.selectedItemId = actionHome

        // set status bar color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black_haze)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.silver)
        }
    }

    override fun onResume() {
        super.onResume()
        validateLoginState()
    }

    private fun validateLoginState() =
        viewModel.validateLoginState(
            onSuccess = {
                log.i("User logged in")
            },
            onError = {
                Intent(this, LoginActivity::class.java).let {
                    startActivity(it)
                }
            }
        )

    override fun onNewIntent(intent: Intent) {
        intent.getParcelableExtra<Place>(Extra.PLACE_EXTRA)?.let { place ->
            log.d("PLACE: $place")

            val bundle = Bundle()
            bundle.putParcelable(Extra.PLACE_EXTRA, place)

            val fragment = PlaceFragment()
            fragment.arguments = bundle

            supportFragmentManager.createAndReplaceFragment(
                PLACE_FRAGMENT,
                PlaceFragment::class.java,
                bundle,
                true,
                allowStateLoss = true
            )
        }
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                actionQrCode -> {
                    startActivity(Intent(this, QrCodeScannerActivity::class.java))
                    false // because qr code scanner uses separate activity
                    // TODO https://app.clickup.com/751518/751948/t/vtmj
                }
                actionHome -> {
                    supportFragmentManager.createAndReplaceFragment(
                        HOME_FRAGMENT,
                        HomeFragment::class.java
                    )
                    true
                }
                actionPlaces -> {
                    supportFragmentManager.createAndReplaceFragment(
                        MY_PLACES_FRAGMENT,
                        MyPlacesFragment::class.java
                    )
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
