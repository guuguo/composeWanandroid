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

package top.guuguo.wanandroid.data.bilibili

import coil.network.HttpException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import top.guuguo.wanandroid.data.await
import top.guuguo.wanandroid.data.bilibili.bean.BiliResBean
import top.guuguo.wanandroid.data.bilibili.bean.BiliHomeBean
import top.guuguo.wanandroid.ext.fromJson
import java.util.concurrent.TimeUnit

class BiliApiFetcher(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(1, TimeUnit.SECONDS).build()
    }

    operator fun invoke(page:Int = 0): Flow<ApiResponse> {
        return flow<ApiResponse> {
            emit(fetchHomeData(page))
        }.catch { e ->
            emit(ApiResponse.Error(e))
        }
    }

    private suspend inline fun <reified T> get(url: String): T {
        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        val response = okHttpClient.newCall(request).await()

        if (!response.isSuccessful) throw HttpException(response)

        return withContext(ioDispatcher) {
            response.body!!.string().fromJson()
        }
    }

    private suspend fun fetchHomeData(page:Int = 0): ApiResponse.Success<BiliResBean<BiliHomeBean>> {
        val list: BiliResBean<BiliHomeBean> = get("https://api.bilibili.com/pgc/web/timeline/v2?season_type=1&day_before=1&day_after=5")
        return ApiResponse.Success(resData = list)
    }
}

sealed class ApiResponse {
    data class Error(
        val throwable: Throwable?,
    ) : ApiResponse()

    data class Success<T>(
        val resData: T,
    ) : ApiResponse()
}
