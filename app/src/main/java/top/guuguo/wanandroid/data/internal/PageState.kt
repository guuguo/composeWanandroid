package top.guuguo.wanandroid.data.internal

sealed class PageState
class PageError(var error: Throwable) : PageState()
object PageContent : PageState()