package top.guuguo.wanandroid.data.bean.banner

data class BannerBean(
    val desc: String,
    var id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)