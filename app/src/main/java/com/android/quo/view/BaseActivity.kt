package com.android.quo.view

import android.support.v7.app.AppCompatActivity
import com.android.quo.util.Logger
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 26.02.18.
 */
abstract class BaseActivity : AppCompatActivity() {

    val log = Logger(javaClass)

    val compositeDisposable by lazy { CompositeDisposable() }
}