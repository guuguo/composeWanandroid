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

package com.example.jetcaster.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import top.guuguo.wanandroid.data.ApiFetcher
import top.guuguo.wanandroid.data.BannerResponse
import top.guuguo.wanandroid.data.HomeArticleResponse
import top.guuguo.wanandroid.data.WanAndroidStore

/**
 * Data repository for Podcasts.
 */
class PodcastsRepository(
    private val wanFetcher: ApiFetcher,
    private val wanStore: WanAndroidStore,
    mainDispatcher: CoroutineDispatcher
) {
    var page = 0

    suspend fun loadArticles() {
        page++
        // Now fetch the podcasts, and add each to each store
        wanFetcher(page)
            .filter { it is HomeArticleResponse.Success }
            .map { it as HomeArticleResponse.Success }
            .collect { (resData) ->
                wanStore.addData(resData.data.datas,resData.data.curPage>=resData.data.pageCount)
            }
    }

    suspend fun refresh() {
        page = 0
        wanFetcher(page)
            .filter { it is HomeArticleResponse.Success }
            .map { it as HomeArticleResponse.Success }
            .collect { (resData) ->
                wanStore.refreshData(resData.data.datas)
            }
        wanFetcher.banner()
            .filter { it is BannerResponse.Success }
            .map { it as BannerResponse.Success }
            .collect { (resData) ->
                wanStore.refreshBanner(resData.data)
            }
    }


}
