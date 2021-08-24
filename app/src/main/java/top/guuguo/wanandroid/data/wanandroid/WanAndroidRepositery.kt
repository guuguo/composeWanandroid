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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect

class WanAndroidsRepository(
    private val wanFetcher: WanApiFetcher,
    private val wanStore: WanAndroidStore,
    mainDispatcher: CoroutineDispatcher
) {
    var page = 0

    suspend fun loadArticles() {
        page++
        wanFetcher(page)
            .collect {
                when (it) {
                    is HomeArticleResponse.Success ->
                        wanStore.addData(it.resData.data.datas, it.resData.data.curPage >= it.resData.data.pageCount)
                    is HomeArticleResponse.Error -> throw it.throwable?: Throwable()
                }
            }
    }

    suspend fun refresh() {
        page = 0
        wanFetcher(page)
            .collect {
                when (it) {
                    is HomeArticleResponse.Success ->
                        wanStore.addData(it.resData.data.datas, it.resData.data.curPage >= it.resData.data.pageCount)
                    is HomeArticleResponse.Error -> throw it.throwable?: Throwable()
                }
            }

        wanFetcher.banner()
            .collect {
                when (it) {
                    is BannerResponse.Success ->
                        wanStore.refreshBanner(it.banner.data)
                    is BannerResponse.Error -> throw it.throwable?: Throwable()
                }
            }

    }


}
