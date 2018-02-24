package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import com.android.quo.util.Logger

/**
 * Created by vitusortner on 24.02.18.
 */
abstract class BaseViewModel : ViewModel() {

    val log = Logger(javaClass)
}