package top.guuguo.wanandroid.ui.bilihome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import top.guuguo.wanandroid.data.wanandroid.WanAndroidsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import top.guuguo.wanandroid.Graph
import top.guuguo.wanandroid.data.bilibili.BiliRepository
import top.guuguo.wanandroid.data.bilibili.BiliStore
import top.guuguo.wanandroid.data.wanandroid.WanAndroidStore
import top.guuguo.wanandroid.data.wanandroid.bean.article.Article
import top.guuguo.wanandroid.data.wanandroid.bean.banner.BannerBean
import top.guuguo.wanandroid.data.internal.PageContent
import top.guuguo.wanandroid.data.internal.PageError
import top.guuguo.wanandroid.data.internal.PageState
import top.guuguo.wanandroid.ui.wanhome.HomeViewState

class BiliHomeViewModel(
    private val repository: BiliRepository = Graph.biliRepository,
    val store: BiliStore = Graph.biliStore
) : ViewModel() {

    init {
//        viewModelScope.launch {
//            store.homeBean.collect {
//
//            }
//            store.pageState.collect {
//
//            }
//        }

        refresh()
    }


    private fun refresh() {
        viewModelScope.launch {
            repository.loadData()
        }
    }
}
