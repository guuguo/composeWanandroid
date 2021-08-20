package top.guuguo.wanandroid

import android.content.Context
import top.guuguo.wanandroid.data.PodcastsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import top.guuguo.wanandroid.data.ApiFetcher
import top.guuguo.wanandroid.data.WanAndroidStore
import java.io.File

object Graph {
    lateinit var okHttpClient: OkHttpClient
    val wanRepository by lazy {
        PodcastsRepository(
            wanFetcher = wanFetcher,
            wanStore = podcastStore,
            mainDispatcher = mainDispatcher
        )
    }
    private val wanFetcher by lazy {
        ApiFetcher(
            okHttpClient = okHttpClient,
            ioDispatcher = ioDispatcher
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