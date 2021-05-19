package top.guuguo.wanandroid.data.bean

data class WanResBean<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String
)
