package top.guuguo.wanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.core.view.WindowCompat
import com.blankj.utilcode.util.NetworkUtils
import com.google.accompanist.insets.ProvideWindowInsets
import top.guuguo.wanandroid.ui.WanAndroidApp
import top.guuguo.wanandroid.ui.theme.WanAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WanAndroidTheme {
                ProvideWindowInsets {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        WanAndroidApp()
                    }
                }
            }
        }
//        lifecycleScope.launchWhenCreated {
//            val request = Request.Builder().url("https://www.wanandroid.com/banner/json").get().build();
//            OkHttpClient().newCall(request).enqueue(object :Callback{
//                override fun onFailure(call: Call, e: IOException) {
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    response.body?.string()
//                }
//            })
//        }
    }
}
