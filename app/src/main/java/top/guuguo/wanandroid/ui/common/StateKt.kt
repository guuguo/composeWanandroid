package top.guuguo.wanandroid.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.guuguo.wanandroid.data.internal.*

@Composable
fun LoadMoreView(loadEnd: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp), contentAlignment = Alignment.Center
    ) {

        if (loadEnd) {
            Text("---------   底线   ---------")
        } else {
            CircularProgressIndicator(Modifier.size(25.dp))
        }
    }
}

@Composable
fun RefreshingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
@Composable
fun EmptyView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("没有数据")
    }
}

@Composable
fun ErrorPage(error: PageError, retry: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = error.error.message ?: "出错了"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = retry) {
                Text(
                    text = "重试",
                )
            }
        }

    }
}

@Composable
fun StateView(pageState: PageState, content: @Composable () -> Unit) {
    when (pageState) {
        is PageEmpty -> EmptyView()
        is PageContent -> content()
        is PageError->  ErrorPage(PageError(pageState.error))
        is PageRefreshing->  RefreshingView()
    }
}

@Preview("错误页面")
@Composable
fun PreviewError() {
    MaterialTheme(colors = darkColors()) {
        ErrorPage(PageError(Throwable("网络出错了")))
    }
}

@Preview("第一次刷新页面")
@Composable
fun PreviewLoading() {
    MaterialTheme(colors = darkColors()) {
        RefreshingView()
    }
}

@Preview("刷新页面")
@Composable
fun PreviewRefresh() {
    MaterialTheme(colors = darkColors()) {
        RefreshingView()
    }
}

@Preview("加载更多")
@Composable
fun PreviewLoadMore() {
    MaterialTheme(colors = darkColors()) {
        LoadMoreView(true)
    }
}