package top.guuguo.wanandroid.web

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.compose.material.Colors
import androidx.core.content.ContextCompat
import top.guuguo.wanandroid.R
import top.guuguo.wanandroid.ext.navigationTo
import top.guuguo.wanandroid.web.WebTools
import android.util.TypedValue
import top.guuguo.wanandroid.ext.getThemeColor


/**
 * 网页可以处理:
 * 点击相应控件：
 * - 拨打电话、发送短信、发送邮件
 * - 上传图片(版本兼容)
 * - 全屏播放网络视频
 * - 进度条显示
 * - 返回网页上一层、显示网页标题
 * JS交互部分：
 * - 前端代码嵌入js(缺乏灵活性)
 * - 网页自带js跳转
 * 被作为第三方浏览器打开
 */
class WebViewActivity : Activity(), IWebPageView {
    // 进度条
    private var mProgressBar: WebProgress? = null

    // 全屏时视频加载view
    override var videoFullView: FrameLayout? = null
        private set

    // 加载视频相关
    private var mWebChromeClient: MyWebChromeClient? = null

    // 网页链接
    private var mUrl: String? = null
    private var webView: WebView? = null
    private var tvGunTitle: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = getThemeColor(android.R.attr.colorPrimary)

        intentData()
        initTitle()
        initWebView()
        handleLoadUrl()
        getDataFromBrowser(intent)

    }

    private fun handleLoadUrl() {
        mUrl?:return
        webView!!.loadUrl(mUrl!!)
    }

    private fun intentData() {
        mUrl = intent.getStringExtra(WEB_URL)
//        val token = MMKVUtils.getInstance().getString(
//            TOKEN
//        )
//        mUrl = mUrl?.addUrlParams("token" to token)
    }

    private fun initTitle() {
        webView = findViewById(R.id.web_view)
        mProgressBar = WebProgress(this).apply {
            visibility = View.GONE
            setColor(Color.BLUE)
            show()
        }
        initToolBar()
    }

    private fun initToolBar() { // 可滚动的title 使用简单 没有渐变效果，文字两旁有阴影
        val mTitleToolBar = findViewById<Toolbar>(R.id.title_tool_bar)
        tvGunTitle = findViewById(R.id.tv_gun_title)
        setActionBar(mTitleToolBar)
        val actionBar = getActionBar()
        actionBar?.setDisplayShowTitleEnabled(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTitleToolBar.overflowIcon =
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_more_vert_24)
        }
        tvGunTitle?.postDelayed({ tvGunTitle?.isSelected = true }, 1900)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_webview, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> handleFinish()// 返回键
//            R.id.actionbar_share -> { // 分享到
//                val shareText = webView!!.title + webView!!.url
//                WebTools.share(this@WebViewActivity, shareText)
//            }
//            R.id.actionbar_cope -> { // 复制链接
//                WebTools.copy(webView!!.url)
//                ToastUtils.show("复制成功")
//            }
//            R.id.actionbar_open -> { // 打开链接
//                WebTools.openLink(this@WebViewActivity, webView!!.url)
//            }
//            R.id.actionbar_webview_refresh -> webView?.reload()// 刷新页面
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {
        val ws = webView!!.settings
        // 保存表单数据
        ws.saveFormData = true
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true)
        ws.builtInZoomControls = true
        ws.displayZoomControls = false
        // 启动应用缓存
        ws.setAppCacheEnabled(true)
        // 设置缓存模式
        ws.cacheMode = WebSettings.LOAD_DEFAULT
        // setDefaultZoom  api19被弃用
        // 网页内容的宽度自适应屏幕
        ws.loadWithOverviewMode = true
        ws.useWideViewPort = true
        // 网页缩放至100，一般的网页达到屏幕宽度效果，个别除外
//        webView.setInitialScale(100);
// 关掉下滑弧形阴影
//        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
// 告诉WebView启用JavaScript执行。默认的是false。
        ws.javaScriptEnabled = true
        //  页面加载好以后，再放开图片
        ws.blockNetworkImage = false
        // 使用localStorage则必须打开
        ws.domStorageEnabled = true
        // 排版适应屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        } else {
            ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }
        // WebView是否新窗口打开(加了后可能打不开网页)
//        ws.setSupportMultipleWindows(true);
// webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        /* 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/ws.textZoom = 100
        webView!!.scrollBarSize = WebTools.dp2px(this, 3f)
        mWebChromeClient = MyWebChromeClient(this)
        webView!!.webChromeClient = mWebChromeClient
        // 与js交互
        webView!!.addJavascriptInterface(MyJavascriptInterface(this), "jsProduct")
        webView!!.webViewClient = MyWebViewClient(this)
        webView!!.setOnLongClickListener { v: View? -> handleLongImage() }
    }

    override fun showWebView() {
        webView?.visibility = View.VISIBLE
    }

    override fun hindWebView() {
        webView?.visibility = View.INVISIBLE
    }

    override fun fullViewAddView(view: View?) {
        val decor = window.decorView as FrameLayout
        videoFullView = FullscreenHolder(this@WebViewActivity).apply { addView(view) }
        decor.addView(videoFullView)
    }

    override fun showVideoFullView() {
        videoFullView?.visibility = View.VISIBLE
    }

    override fun hindVideoFullView() {
        videoFullView?.visibility = View.GONE
    }

    override fun startProgress(newProgress: Int) {
        mProgressBar?.setWebProgress(newProgress)
    }

    private fun setTitle(mTitle: String?) {
        tvGunTitle?.text = mTitle
    }

    /**
     * android与js交互：
     * 前端注入js代码：不能加重复的节点，不然会覆盖
     * 前端调用js代码
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        if (!isNetworkConnected(this)) {
            mProgressBar!!.hide()
        }
        loadImageClickJS()
        loadTextClickJS()
        loadCallJS()
        loadWebsiteSourceCodeJS()
    }

    /**
     * 处理是否唤起三方app
     */
    override fun isOpenThirdApp(url: String?): Boolean {
        return WebTools.handleThirdApp(this, url)
    }

    /**
     * 前端注入JS：
     * 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
     */
    private fun loadImageClickJS() {
        loadJs(
            "javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"));}" +
                    "}" +
                    "})()"
        )
    }

    /**
     * 前端注入JS：
     * 遍历所有的 * 节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
     */
    private fun loadTextClickJS() {
        loadJs(
            "javascript:(function(){" +
                    "var objs =document.getElementsByTagName(\"li\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    "objs[i].onclick=function(){" +
                    "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                    "}" +
                    "})()"
        )
    }

    /**
     * 传应用内的数据给html，方便html处理
     */
    private fun loadCallJS() { // 无参数调用
        loadJs("javascript:javacalljs()")
        // 传递参数调用
        loadJs("javascript:javacalljswithargs('" + "android传入到网页里的数据，有参" + "')")
    }

    /**
     * get website source code
     * 获取网页源码
     */
    private fun loadWebsiteSourceCodeJS() {
        loadJs("javascript:window.injectedObject.showSource(document.getElementsByTagName('html')[0].innerHTML);")
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun hideCustomView() {
        mWebChromeClient!!.onHideCustomView()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override val videoLoadingProgressView: View
        get() = LayoutInflater.from(this).inflate(R.layout.video_loading_progress, null)

    override fun onReceivedTitle(view: WebView?, title: String?) {
        setTitle(title)
    }

    override fun startFileChooserForResult(intent: Intent?, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    /**
     * 上传图片之后的回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            MyWebChromeClient.FILECHOOSER_RESULTCODE -> {
                mWebChromeClient?.mUploadMessage(intent, resultCode)
            }
            MyWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5 -> {
                mWebChromeClient?.mUploadMessageForAndroid5(intent, resultCode)
            }
        }
    }

    /**
     * 使用singleTask启动模式的Activity在系统中只会存在一个实例。
     * 如果这个实例已经存在，intent就会通过onNewIntent传递到这个Activity。
     * 否则新的Activity实例被创建。
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getDataFromBrowser(intent)
    }

    /**
     * 作为三方浏览器打开传过来的值
     * Scheme: https
     * host: www.jianshu.com
     * path: /p/1cbaf784c29c
     * url = scheme + "://" + host + path;
     */
    private fun getDataFromBrowser(intent: Intent) {
        val data = intent.data
        if (data != null) {
            try {
                val scheme = data.scheme
                val host = data.host
                val path = data.path
                val text = "Scheme: $scheme\nhost: $host\npath: $path"
                Log.e("data", text)
                val url = "$scheme://$host$path"
                webView!!.loadUrl(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 直接通过三方浏览器打开时，回退到首页
     */
    private fun handleFinish() {
        finish()
    }

    /**
     * 4.4以上可用 evaluateJavascript 效率高
     */
    private fun loadJs(jsString: String) {
        webView?.evaluateJavascript(jsString, null)
    }

    /**
     * 长按图片事件处理
     */
    private fun handleLongImage(): Boolean {
        val hitTestResult = webView!!.hitTestResult
        // 如果是图片类型或者是带有图片链接的类型
        if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
            hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
        ) { // 弹出保存图片的对话框
            AlertDialog.Builder(this@WebViewActivity)
                .setItems(
                    arrayOf("查看大图", "保存图片到相册")
                ) { _: DialogInterface?, which: Int ->
                    val picUrl = hitTestResult.extra
                    //获取图片
//                    Log.e("picUrl", picUrl)
                    when (which) {
                        0 -> {
                        }
                        1 -> {
                        }
                        else -> {
                        }
                    }
                }
                .show()
            return true
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //全屏播放退出全屏
            when {
                mWebChromeClient!!.inCustomView() -> {
                    hideCustomView()
                    return true
                    //返回网页上一页
                }
                webView!!.canGoBack() -> {
                    webView!!.goBack()
                    return true
                    //退出网页
                }
                else -> {
                    handleFinish()
                }
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        webView!!.onPause()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        webView!!.onResume()
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        webView!!.resumeTimers()
        // 设置为横屏
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onDestroy() {
        if (videoFullView != null) {
            videoFullView!!.removeAllViews()
            videoFullView = null
        }
        if (webView != null) {
            val parent = webView!!.parent as ViewGroup
            parent?.removeView(webView)
            webView!!.removeAllViews()
            webView!!.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView!!.stopLoading()
            webView!!.webChromeClient = null
//            webView!!.webViewClient = null
            webView!!.destroy()
            webView = null
        }
        super.onDestroy()
    }

    companion object {
        final val WEB_URL = "WEB_URL"
        final val WEB_TITLE = "WEB_TITLE"

        /**
         * 打开网页:
         *
         * @param mContext 上下文
         * @param mUrl     要加载的网页url
         * @param mTitle   标题
         */
        fun load(mContext: Context, mUrl: String?, mTitle: String?) {
            WebViewActivity::class.navigationTo(mContext, WEB_URL to mUrl, WEB_TITLE to (mTitle?:"加载中..."))
        }

        /**
         * 判断网络是否连通
         */
        fun isNetworkConnected(context: Context?): Boolean {
            return try {
                if (context != null) {
                    val cm =
                        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val info = cm.activeNetworkInfo
                    info != null && info.isConnected
                } else { /*如果context为空，就返回false，表示网络未连接*/
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}