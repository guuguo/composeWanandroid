package top.guuguo.wanandroid.ui.bilihome

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.lightColors
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.guuguo.wanandroid.data.bilibili.bean.VideoBean
import top.guuguo.wanandroid.ext.fromJson
import top.guuguo.wanandroid.test.fakeBiliHomeJson
import top.guuguo.wanandroid.ui.common.StateView
import top.guuguo.wanandroid.ui.common.VerticalGrid

@Composable
fun BiliHome() {
    rememberSystemUiController().apply {
        setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }
    val viewModel = viewModel(BiliHomeViewModel::class.java)
    val pageState by viewModel.store.pageState.collectAsState()
    StateView(pageState) {
        viewModel.store.homeBean.value?.latest?.let {
            HomeList(it)
        }
    }
}


@Composable
private fun HomeList(videos: List<VideoBean>) {
    VerticalGrid(Modifier.verticalScroll(rememberScrollState()),2) {
        videos.forEach {
            ArticleItem(it)
        }
    }
}

@Composable
fun ArticleItem(video: VideoBean) {
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
            .wrapContentHeight()
            .clickable {}
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(horizontalAlignment=Alignment.Start) {
            Image(
                painter = rememberCoilPainter(video.cover, fadeIn = true),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(15.dp))
                    .shadow(4.dp, shape = RoundedCornerShape(15.dp))
                    .clickable {
                    }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = video.pub_index, style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = video.title)
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
        val list = fakeBiliHomeJson.fromJson<List<VideoBean>>()
        HomeList(
            videos = list
        )
    }
}


@Preview("文章条目")
@Composable
fun PreviewItem() {
    ArticleItem(fakeBiliHomeJson.fromJson())
}
