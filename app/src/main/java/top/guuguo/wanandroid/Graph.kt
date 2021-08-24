package top.guuguo.wanandroid

import android.content.Context
import top.guuguo.wanandroid.data.wanandroid.WanAndroidsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import top.guuguo.wanandroid.data.bilibili.BiliApiFetcher
import top.guuguo.wanandroid.data.bilibili.BiliRepository
import top.guuguo.wanandroid.data.bilibili.BiliStore
import top.guuguo.wanandroid.data.wanandroid.WanAndroidStore
import top.guuguo.wanandroid.data.wanandroid.WanApiFetcher
import java.io.File

object Graph {
    lateinit var okHttpClient: OkHttpClient
    val wanRepository by lazy {
        WanAndroidsRepository(
            wanFetcher = wanFetcher,
            wanStore = podcastStore,
            mainDispatcher = mainDispatcher
        )
    }
    private val wanFetcher by lazy {
        WanApiFetcher(
            okHttpClient = okHttpClient,
            ioDispatcher = ioDispatcher
        )
    }

    ///bili
    val biliStore by lazy {
        BiliStore( )
    }
    private val biliFetcher by lazy {
        BiliApiFetcher(
            okHttpClient = okHttpClient,
            ioDispatcher = ioDispatcher
        )
    }
    val biliRepository by lazy {
        BiliRepository(
            fetcher = biliFetcher,
            store = biliStore,
            mainDispatcher = mainDispatcher
        )
    }




    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val podcastStore by lazy {
        WanAndroidStore( )
    }
    fun provide(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "http_cache"), 20 * 1024 * 1024))
            .apply {
                if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
            }
            .build()

    }
}