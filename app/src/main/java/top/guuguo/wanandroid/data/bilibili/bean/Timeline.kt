package top.guuguo.wanandroid.data.bilibili.bean

data class Timeline(
    val date: String,
    val date_ts: Int,
    val day_of_week: Int,
    val episodes: List<VideoBean>,
    val is_today: Int
)