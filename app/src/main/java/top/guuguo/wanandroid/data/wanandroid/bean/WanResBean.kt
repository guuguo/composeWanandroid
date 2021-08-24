package top.guuguo.wanandroid.data.wanandroid.bean

data class WanResBean<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String
)