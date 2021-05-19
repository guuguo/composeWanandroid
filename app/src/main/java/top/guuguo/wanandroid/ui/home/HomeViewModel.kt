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

package top.guuguo.wanandroid.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.data.PodcastsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import top.guuguo.wanandroid.Graph
import top.guuguo.wanandroid.data.WanAndroidStore
import top.guuguo.wanandroid.data.bean.article.Article
import top.guuguo.wanandroid.data.bean.banner.BannerBean

class HomeViewModel(
    private val podcastsRepository: PodcastsRepository = Graph.wanRepository,
    private val podcastStore: WanAndroidStore = Graph.podcastStore
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeViewState())

    private val refreshing = MutableStateFlow(true)

    val state: StateFlow<HomeViewState> get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                podcastStore.list,
                podcastStore.bannerList,
                refreshing,
                podcastStore.loadingEnd
            ) { articles, banners, refreshing, loading ->
                HomeViewState(
                    articles = articles,
                    banners = banners,
                    refreshing = refreshing,
                    errorMessage = null,
                    loadingEnd = loading
                )
            }.catch { throwable ->
                throw throwable
            }.collect {
                _state.value = it
            }
        }

        refresh()
    }

    var refreshingJob: Job? = null
    fun refresh() {
        if (refreshingJob?.isActive != true) {
            refreshingJob = viewModelScope.launch {
                val d1 = async {
                    runCatching {
                        refreshing.value = true
                        podcastsRepository.refresh()
                    }
                }
                val d2 = async { delay(1000) }
                d1.await()
                d2.await()
                refreshing.value = false
            }
        }
    }

    var loadingJob: Job? = null
    fun load() {
        if (loadingJob?.isActive != true) {
            loadingJob = viewModelScope.launch {
                async {
                    runCatching {
                        podcastsRepository.loadArticles()
                    }
                }
            }
        }
    }
}


data class HomeViewState(
    val articles: List<Article> = emptyList(),
    val banners: List<BannerBean> = emptyList(),
    val refreshing: Boolean = true,
    val loadingEnd: Boolean = true,
    val errorMessage: String? = null
)
