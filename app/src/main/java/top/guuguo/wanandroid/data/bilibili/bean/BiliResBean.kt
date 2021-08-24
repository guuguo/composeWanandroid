package top.guuguo.wanandroid.data.bilibili.bean

data class BiliResBean<T>(
    val code: Int,
    val message: String,
    val result: T
)