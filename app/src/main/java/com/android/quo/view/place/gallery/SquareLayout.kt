package com.android.quo.view.place.gallery

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * Created by vitusortner on 19.11.17.
 */
class SquareLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}