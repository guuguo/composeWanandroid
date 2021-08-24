package top.guuguo.wanandroid.ui.wanhome

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.guuguo.wanandroid.data.wanandroid.bean.article.Article
import top.guuguo.wanandroid.data.wanandroid.bean.banner.BannerBean
import top.guuguo.wanandroid.ext.fromJson
import top.guuguo.wanandroid.test.fakeBannerJson
import top.guuguo.wanandroid.test.fakeJson
import top.guuguo.wanandroid.ui.common.LoadMoreView
import top.guuguo.wanandroid.ui.common.RefreshingView
import top.guuguo.wanandroid.web.WebViewActivity

@Composable
fun Home() {
    rememberSystemUiController().apply {
        setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }
    val viewModel = viewModel(HomeViewModel::class.java)
    val viewState by viewModel.state.collectAsState()
    Log.d("composeDebug", viewState.articles.joinToString { it.toString() + "\n" })
    HomeContent(
        isRefreshing = viewState.refreshing,
        loadingEnd = viewState.loadingEnd,
        articles = viewState.articles,
        banners = viewState.banners,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun HomeContent(isRefreshing: Boolean, loadingEnd: Boolean, articles: List<Article>, banners: List<BannerBean>, modifier: Modifier) {

    val viewModel = viewModel(HomeViewModel::class.java)
    if (isRefreshing) {
        RefreshingView()
    } else {
        HomeList(modifier, loadingEnd, banners, articles, loadMore = {
            viewModel.load()
        })
    }
}

@Composable
private fun HomeList(modifier: Modifier, loadEnd: Boolean, banners: List<BannerBean>, articles: List<Article>, loadMore: () -> Unit) {

    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item { Spacer(modifier = Modifier.statusBarsHeight()) }
        item { HomeBanner(banners) }
        itemsIndexed(articles, key = { i, it -> it.title + i }) { i, it ->
            if (i == articles.size - 1) {
                loadMore()
            }
            ArticleItem(it)
        }
        item { LoadMoreView(loadEnd) }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeBanner(banners: List<BannerBean>) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(
        pageCount = banners.size,
        initialOffscreenLimit = 2,
    )
    val context = LocalContext.current

    HorizontalPager(
        state = pagerState,
        Modifier
            .aspectRatio(876 / 486f)
    ) {
        val item = banners[it]
        Image(
            painter = rememberCoilPainter(item.imagePath, fadeIn = true),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp, 10.dp)
                .background(Color.Gray, shape = RoundedCornerShape(15.dp))
                .shadow(4.dp, shape = RoundedCornerShape(15.dp))
                .clickable {
                    WebViewActivity.load(context, item.url, item.title)
                }


        )
    }
}

@Composable
fun ArticleItem(article: Article) {
    val context = LocalContext.current
    Box(
        Modifier
            .padding(20.dp, 10.dp)
            .background(
                MaterialTheme.colors.surface,
                shape = RoundedCornerShape(15.dp),
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .clickable { WebViewActivity.load(context, article.link, article.title) }
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column {
            if (article.shareUser.isNotEmpty()) Text(text = article.shareUser, style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = article.title)
        }
    }

}


@Composable
@Preview(
    "主页",
    uiMode = UI_MODE_NIGHT_YES
)
fun PreviewPage() {
    MaterialTheme(colors = lightColors()) {
        val list = (0..2).map { i -> fakeJson.fromJson<Article>().also { it.id = it.id + i } }
        HomeList(
            articles = list,
            banners = listOf(),
            modifier = Modifier.fillMaxSize(),
            loadEnd = true
        ) {

        }
    }
}

@Preview("Banner")
@Composable
fun PreviewBanner() {
    HomeBanner((0..10).map { i -> fakeBannerJson.fromJson<BannerBean>().also { it.id = it.id + i } })
}

@Preview("文章条目")
@Composable
fun PreviewItem() {
    ArticleItem(fakeJson.fromJson())
}
