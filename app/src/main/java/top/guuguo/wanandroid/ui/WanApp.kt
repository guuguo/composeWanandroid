package top.guuguo.wanandroid.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import top.guuguo.wanandroid.ui.home.HomeViewModel


@Composable
//@Preview(showBackground = true)
fun WanAndroidApp() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            Home()
        }
        Divider(Modifier.height(0.3.dp), color = Color.LightGray)
        Tab()
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
    Row(
        Modifier
            .fillMaxWidth()
            .height(70.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FuncIcon("首页", R.drawable.ic_shouye) {}
        FuncIcon("小孩", R.drawable.ic_ertongpiao) {}
        FuncIcon("发现", R.drawable.ic_pengyouquan) {}
    }
}

@Composable
fun RowScope.FuncIcon(title: String, icon: Int, onClick: () -> Unit) {
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
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.caption)
        }
    }

}
