package top.guuguo.wanandroid.web

import android.app.Activity
import android.webkit.JavascriptInterface

/**
 * js通信接口
 */
class MyJavascriptInterface(private val activity: Activity) {
    @JavascriptInterface
    fun myanswers(value: String) {
    }
}