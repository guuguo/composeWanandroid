package top.guuguo.wanandroid.web

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import top.guuguo.wanandroid.WanApplication

object WebTools {
    /**
     * 将 Android5.0以下手机不能直接打开mp4后缀的链接
     *
     * @param url 视频链接
     */
    fun getVideoHtmlBody(url: String): String {
        return "<html>" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width\">" +
                "<style type=\"text/css\" abt=\"234\"></style>" +
                "</head>" +
                "<body>" +
                "<video controls=\"\" autoplay=\"\" name=\"media\">" +
                "<source src=\"" + url + "\" type=\"video/mp4\">" +
                "</video>" +
                "</body>" +
                "</html>"
    }

    /**
     * 实现文本复制功能
     *
     * @param content 复制的文本
     */
    fun copy(content: String?) {
        if (!TextUtils.isEmpty(content)) {
            val clipboard =
                WanApplication().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(content, content)
            clipboard.setPrimaryClip(clip)
        }
    }

    /**
     * 使用浏览器打开链接
     */
    fun openLink(context: Context, content: String) {
        if (!TextUtils.isEmpty(content) && content.startsWith("http")) {
            val issuesUrl = Uri.parse(content)
            val intent = Intent(Intent.ACTION_VIEW, issuesUrl)
            context.startActivity(intent)
        }
    }

    /**
     * 分享
     */
    fun share(context: Context, extraText: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
        intent.putExtra(Intent.EXTRA_TEXT, extraText)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, "分享"))
    }

    /**
     * 通过包名找应用,不需要权限
     */
    fun hasPackage(context: Context?, packageName: String?): Boolean {
        return if (null == context || TextUtils.isEmpty(packageName)) {
            false
        } else try {
            context.packageManager.getPackageInfo(packageName!!, PackageManager.GET_GIDS)
            true
        } catch (e: PackageManager.NameNotFoundException) { // 抛出找不到的异常，说明该程序已经被卸载
            false
        }
    }

    /**
     * 处理三方链接
     * 网页里可能唤起其他的app
     */
    fun handleThirdApp(activity: Activity, backUrl: String?): Boolean { /*http开头直接跳过*/
        if (backUrl == null) return false
        if ("http".startsWith(backUrl)) { // 可能有提示下载Apk文件
            if (".apk".contains(backUrl)) {
                startActivity(activity, backUrl)
                return true
            }
            return false
        }
        var isJump = false
        /*屏蔽以下应用唤起App，可根据需求 添加或取消*/if (backUrl.startsWith("tbopen:") // 淘宝
//                        || backUrl.startsWith("openapp.jdmobile:")// 京东
//                        || backUrl.startsWith("jdmobile:")//京东
//                        || backUrl.startsWith("alipay:")// 支付宝
//                        || backUrl.startsWith("alipays:")//支付宝
            || backUrl.startsWith("zhihu:") // 知乎
            || backUrl.startsWith("vipshop:") //
            || backUrl.startsWith("youku:") //优酷
            || backUrl.startsWith("uclink:") // UC
            || backUrl.startsWith("ucbrowser:") // UC
            || backUrl.startsWith("newsapp:") //
            || backUrl.startsWith("sinaweibo:") // 新浪微博
            || backUrl.startsWith("suning:") //
            || backUrl.startsWith("pinduoduo:") // 拼多多
            || backUrl.startsWith("baiduboxapp:") // 百度
            || backUrl.startsWith("qtt:") //
        ) {
            isJump = true
        }
        if (isJump) {
            startActivity(activity, backUrl)
        }
        return isJump
    }

    private fun startActivity(context: Context, url: String) {
        try { // 用于DeepLink测试
            if (url.startsWith("will://")) {
                val uri = Uri.parse(url)
                Log.e(
                    "---------scheme",
                    uri.scheme + "；host: " + uri.host + "；Id: " + uri.pathSegments[0]
                )
            }
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val uri = Uri.parse(url)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}