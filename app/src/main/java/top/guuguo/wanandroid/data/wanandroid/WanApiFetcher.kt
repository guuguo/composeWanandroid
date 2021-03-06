/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.guuguo.wanandroid.data.wanandroid

import coil.network.HttpException
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import top.guuguo.wanandroid.data.await
import top.guuguo.wanandroid.data.wanandroid.bean.WanListBean
import top.guuguo.wanandroid.data.wanandroid.bean.article.Article
import top.guuguo.wanandroid.data.wanandroid.bean.WanResBean
import top.guuguo.wanandroid.data.wanandroid.bean.banner.BannerBean
import top.guuguo.wanandroid.ext.fromJson
import java.util.concurrent.TimeUnit

/**
 * A class which fetches some selected podcast RSS feeds.
 *
 * @param okHttpClient [OkHttpClient] to use for network requests
 * @param syndFeedInput [SyndFeedInput] to use for parsing RSS feeds.
 * @param ioDispatcher [CoroutineDispatcher] to use for running fetch requests.
 */
class WanApiFetcher(
    private val okHttpClient: OkHttpClient,
//    private val syndFeedInput: SyndFeedInput,
    private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * It seems that most podcast hosts do not implement HTTP caching appropriately.
     * Instead of fetching data on every app open, we instead allow the use of 'stale'
     * network responses (up to 8 hours).
     */
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(1, TimeUnit.SECONDS).build()
    }

    /**
     * Returns a [Flow] which fetches each podcast feed and emits it in turn.
     *
     * The feeds are fetched concurrently, meaning that the resulting emission order may not
     * match the order of [feedUrls].
     */
    operator fun invoke(page:Int = 0): Flow<HomeArticleResponse> {
        // We use flatMapMerge here to achieve concurrent fetching/parsing of the feeds.
        return flow {
            emit(fetchHomeData(page))
        }.catch { e ->
            // If an exception was caught while fetching the podcast, wrap it in
            // an Error instance.
            emit(HomeArticleResponse.Error(e))
        }
    }

    fun banner(): Flow<BannerResponse> {
        // We use flatMapMerge here to achieve concurrent fetching/parsing of the feeds.
        return flow {
            emit(fetchBannerData())
        }.catch { e ->
            // If an exception was caught while fetching the podcast, wrap it in
            // an Error instance.
            emit(BannerResponse.Error(e))
        }
    }

    private suspend inline fun <reified T> get(url: String): T {
        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        val response = okHttpClient.newCall(request).await()

        // If the network request wasn't successful, throw an exception
        if (!response.isSuccessful) throw HttpException(response)

        // Otherwise we can parse the response using a Rome SyndFeedInput, then map it
        // to a Podcast instance. We run this on the IO dispatcher since the parser is reading
        // from a stream.
        return withContext(ioDispatcher) {
            response.body!!.string().fromJson()
        }
    }

    private suspend fun fetchBannerData(): BannerResponse {
        val banner: WanResBean<List<BannerBean>> = get("https://www.wanandroid.com/banner/json")
        return BannerResponse.Success(banner = banner)
    }

    private suspend fun fetchHomeData(page:Int = 0): HomeArticleResponse {
        val list: WanResBean<WanListBean<Article>> = get("https://www.wanandroid.com/article/list/${page}/json")
        return HomeArticleResponse.Success(resData = list)
    }
}

sealed class BannerResponse {
    data class Error(
        val throwable: Throwable?,
    ) : BannerResponse()

    data class Success(
        val banner: WanResBean<List<BannerBean>>,
    ) : BannerResponse()
}

sealed class HomeArticleResponse {
    data class Error(
        val throwable: Throwable?,
    ) : HomeArticleResponse()

    data class Success(
        val resData: WanResBean<WanListBean<Article>>,
    ) : HomeArticleResponse()
}


/**
 * Most feeds use the following DTD to include extra information related to
 * their podcast. Info such as images, summaries, duration, categories is sometimes only available
 * via this attributes in this DTD.
 */
private const val PodcastModuleDtd = "http://www.itunes.com/dtds/podcast-1.0.dtd"
