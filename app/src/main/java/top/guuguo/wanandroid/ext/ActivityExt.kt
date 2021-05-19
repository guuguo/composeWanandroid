package top.guuguo.wanandroid.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KClass

inline fun <reified T : Activity> KClass<T>.navigationTo(context: Context, vararg args:Pair<String,*>) {
    val intent = Intent(context, this.java)
    pairArrayToIntent(args, intent)
    context.startActivity(intent)
}

 fun pairArrayToIntent(args: Array<out Pair<String, *>>, intent: Intent) {

    args.forEach {
        val value = it.second
        when (value) {
            is Boolean -> intent.putExtra(it.first, value)
            is Long -> intent.putExtra(it.first, value)
            is Int -> intent.putExtra(it.first, value)
            is Byte -> intent.putExtra(it.first, value)
            is Char -> intent.putExtra(it.first, value)
            is Float -> intent.putExtra(it.first, value)
            is Short -> intent.putExtra(it.first, value)
            is Double -> intent.putExtra(it.first, value)
            is Bundle -> intent.putExtra(it.first, value)
            is IntArray -> intent.putExtra(it.first, value)
            is ByteArray -> intent.putExtra(it.first, value)
            is CharArray -> intent.putExtra(it.first, value)
            is LongArray -> intent.putExtra(it.first, value)
            is FloatArray -> intent.putExtra(it.first, value)
            is ShortArray -> intent.putExtra(it.first, value)
            is DoubleArray -> intent.putExtra(it.first, value)
            is BooleanArray -> intent.putExtra(it.first, value)
            is CharSequence -> intent.putExtra(it.first, value)
            is Serializable -> intent.putExtra(it.first, value)
            is Parcelable -> intent.putExtra(it.first, value)
            is Array<*> -> intent.putExtra(it.first, value)
        }
    }
}
