package top.guuguo.wanandroid.web

import android.content.Intent

interface IWebPageView {
    /**
     * 显示webView
     */
    fun showWebView()

    /**
     * 隐藏webView
     */
    fun hindWebView()

    /**
     * 进度条变化时调用
     *
     * @param newProgress 进度0-100
     */
    fun startProgress(newProgress: Int)

    /**
     * 添加视频全屏view
     */
    fun fullViewAddView(view: android.view.View?)

    /**
     * 显示全屏view
     */
    fun showVideoFullView()

    /**
     * 隐藏全屏view
     */
    fun hindVideoFullView()

    /**
     * 设置横竖屏
     */
    fun setRequestedOrientation(screenOrientationPortrait: Int)

    /**
     * 得到全屏view
     */
    val videoFullView: android.widget.FrameLayout?

    /**
     * 加载视频进度条
     */
    val videoLoadingProgressView: android.view.View?

    /**
     * 返回标题处理
     */
    fun onReceivedTitle(view: android.webkit.WebView?, title: String?)

    /**
     * 上传图片打开文件夹
     */
    fun startFileChooserForResult(intent: Intent?, requestCode: Int)

    /**
     * 页面加载结束，添加js监听等
     */
    fun onPageFinished(view: android.webkit.WebView?, url: String?)

    /**
     * 是否处理打开三方app
     * @param url
     */
    fun isOpenThirdApp(url: String?): Boolean
}