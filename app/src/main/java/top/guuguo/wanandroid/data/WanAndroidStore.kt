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

package top.guuguo.wanandroid.data

import kotlinx.coroutines.flow.MutableStateFlow
import top.guuguo.wanandroid.data.bean.article.Article
import top.guuguo.wanandroid.data.bean.banner.BannerBean

/**
 * A data repository for [Category] instances.
 */
class WanAndroidStore(
) {
    val list = MutableStateFlow<MutableList<Article>>(mutableListOf())
    val bannerList = MutableStateFlow<MutableList<BannerBean>>(mutableListOf())
    val loadingEnd = MutableStateFlow(false)
    fun refreshBanner(newData: List<BannerBean>) {
        bannerList.value = newData.toMutableList()
    }

    fun addData(newData: List<Article>,isEnd:Boolean) {
        list.value = list.value.apply { addAll(newData) }
        loadingEnd.value=isEnd
    }

    fun refreshData(newData: List<Article>) {
        list.value = newData.toMutableList()
    }
}
