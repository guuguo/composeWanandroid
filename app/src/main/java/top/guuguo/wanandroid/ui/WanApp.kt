package top.guuguo.wanandroid.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import kotlinx.coroutines.flow.MutableStateFlow
import top.guuguo.wanandroid.ui.wanhome.Home
import top.guuguo.wanandroid.R;
import top.guuguo.wanandroid.data.internal.PageContent
import top.guuguo.wanandroid.data.internal.PageError
import top.guuguo.wanandroid.ui.bilihome.BiliHome
import top.guuguo.wanandroid.ui.common.EmptyView
import top.guuguo.wanandroid.ui.common.ErrorPage
import top.guuguo.wanandroid.ui.wanhome.HomeViewModel


@Composable
//@Preview(showBackground = true)
fun WanAndroidApp() {
    val viewModel = viewModel(HomeViewModel::class.java)
    Column(modifier = Modifier.fillMaxSize()) {
        val pageState by viewModel.pageState.collectAsState()
        when (pageState) {
            PageContent -> {
                val tabIndex = rememberSaveable {
                    mutableStateOf(0)
                }
                Box(modifier = Modifier.weight(1f)) {
                    Crossfade(tabIndex.value) {
                        when (it) {
                            0 -> Home()
                            1 -> BiliHome()
                            else -> EmptyView()
                        }
                    }
                }
                Divider(Modifier.height(0.3.dp), color = Color.LightGray)
                Tab(tabIndex)
            }
            is PageError -> {
                ErrorPage(error = pageState as PageError, retry = viewModel::refresh)
            }
            else -> {
            }
        }
    }
}


@Composable
fun Tab(tabIndex: MutableState<Int>) {
    val viewModel = viewModel(HomeViewModel::class.java)
    val insets = LocalWindowInsets.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(insets.navigationBars.toPaddingValues())
            .height(Dp(70f)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val setIndex: (Int, () -> Unit) -> Unit = { index, reTap ->
            if (tabIndex.value == index) {
                reTap()
            } else {
                tabIndex.value = index
            }
        }
        FuncIcon("首页", R.drawable.ic_shouye) {
            setIndex(0) { viewModel.refresh() }
        }
        FuncIcon("小孩", R.drawable.ic_ertongpiao) {
            setIndex(1) { }
        }
        FuncIcon("发现", R.drawable.ic_pengyouquan) {
            setIndex(2) { }
        }
    }
}

@Composable
@Preview
fun PreviewTab() {
    MaterialTheme(colors = darkColors()) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .height(70.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FuncIcon("首页", R.drawable.ic_shouye) {}
            FuncIcon("小孩", R.drawable.ic_ertongpiao) {}
            FuncIcon("发现", R.drawable.ic_pengyouquan) {}
        }
    }

}

@Composable
fun RowScope.FuncIcon(title: String, icon: Int, onClick: () -> Unit) {
    val contentColor = contentColorFor(MaterialTheme.colors.surface)
    Box(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(30.dp)) {
                Icon(
                    painter = painterResource(id = icon),
                    tint = contentColor,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.caption.copy(color = contentColor), color = contentColor)
        }
    }
}
