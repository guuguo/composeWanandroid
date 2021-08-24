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

package top.guuguo.wanandroid.data.base

import kotlinx.coroutines.flow.MutableStateFlow
import top.guuguo.wanandroid.data.bilibili.bean.BiliHomeBean
import top.guuguo.wanandroid.data.internal.*
import top.guuguo.wanandroid.data.wanandroid.bean.article.Article
import top.guuguo.wanandroid.data.wanandroid.bean.banner.BannerBean

/**
 * A data repository for [Category] instances.
 */
abstract class BaseStore() {
    open val pageState = MutableStateFlow<PageState>(PageRefreshing)

    fun startFirstLoading() {
        pageState.value = PageRefreshing
    }

    fun restore() {
        pageState.value = PageContent
    }

    fun error(err: Throwable) {
        pageState.value = PageError(err)
    }
}
