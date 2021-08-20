package top.guuguo.wanandroid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.guuguo.wanandroid.ui.home.Home
import top.guuguo.wanandroid.R;
import top.guuguo.wanandroid.data.internal.PageContent
import top.guuguo.wanandroid.data.internal.PageError
import top.guuguo.wanandroid.data.internal.PageState
import top.guuguo.wanandroid.ui.common.ErrorPage
import top.guuguo.wanandroid.ui.home.HomeViewModel


@Composable
//@Preview(showBackground = true)
fun WanAndroidApp() {
    val viewModel = viewModel(HomeViewModel::class.java)

    Column(modifier = Modifier.fillMaxSize()) {
        val pageState by viewModel.pageState.collectAsState()
        when (pageState) {
            PageContent -> {
                Box(modifier = Modifier.weight(1f)) {
                    Home()
                }
                Divider(Modifier.height(0.3.dp), color = Color.LightGray)
                Tab()
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
fun Tab() {
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
        FuncIcon("首页", R.drawable.ic_shouye) {
            viewModel.refresh()
        }
        FuncIcon("小孩", R.drawable.ic_ertongpiao) {}
        FuncIcon("发现", R.drawable.ic_pengyouquan) {}
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
