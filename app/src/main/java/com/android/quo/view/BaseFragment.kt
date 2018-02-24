package com.android.quo.view

import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.viewmodel.LoginViewModel

/**
 * Created by vitusortner on 24.02.18.
 */
abstract class BaseFragment(@LayoutRes private val layout: Int) : Fragment() {

    val TAG = javaClass.simpleName

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(layout, container, false)
}