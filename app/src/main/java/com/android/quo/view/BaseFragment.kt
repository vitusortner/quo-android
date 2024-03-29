package com.android.quo.view

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.util.Logger
import com.bumptech.glide.Glide
import id.zelory.compressor.Compressor
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 24.02.18.
 */
abstract class BaseFragment(@LayoutRes private val layout: Int) : Fragment() {

    val log = Logger(javaClass)

    val imageLoader by lazy { Glide.with(requireContext()) }

    val imageCompressor by lazy { Compressor(requireContext()) }

    val compositeDisposable by lazy { CompositeDisposable() }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(layout, container, false)
}