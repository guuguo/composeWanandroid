package top.guuguo.wanandroid.web

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.widget.FrameLayout

class FullscreenHolder(ctx: Context?) : FrameLayout(ctx!!) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) = true

    init {
        setBackgroundResource(android.R.color.black)
    }
}