package top.guuguo.wanandroid.web

import android.webkit.WebViewClient
import android.webkit.SslErrorHandler
import android.net.http.SslError
import android.text.TextUtils
import android.webkit.WebView

/**
 * 监听网页链接:
 * - 根据标识:打电话、发短信、发邮件
 * - 进度条的显示
 * - 添加javascript监听
 * - 唤起京东，支付宝，微信原生App
 */
class MyWebViewClient(private val mIWebPageView: IWebPageView) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return if (TextUtils.isEmpty(url)) {
            false
        } else mIWebPageView.isOpenThirdApp(url)
    }

    // html加载完成之后，添加监听图片的点击js函数
    override fun onPageFinished(view: WebView, url: String) {
        mIWebPageView.onPageFinished(view, url)
        super.onPageFinished(view, url)
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        if (errorCode == 404) { //用javascript隐藏系统定义的404页面信息
            val data = "Page NO FOUND！"
            view.loadUrl("javascript:document.body.innerHTML=\"$data\"")
        }
    }

    // SSL Error. Failed to validate the certificate chain,error: java.security.cert.CertPathValidatorExcept
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.proceed() //解决方案, 不要调用super.xxxx
    }

    // 视频全屏播放按返回页面被放大的问题
    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        super.onScaleChanged(view, oldScale, newScale)
        if (newScale - oldScale > 7) {
            view.setInitialScale((oldScale / newScale * 100).toInt()) //异常放大，缩回去。
        }
    }
}