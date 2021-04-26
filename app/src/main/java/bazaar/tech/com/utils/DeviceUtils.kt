package bazaar.tech.com.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import bazaar.tech.com.AppApplication

fun isOnline(): Boolean {
    val connectivityManager =
        AppApplication.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun getWindowHeight(context: Context): Int {
    val displayMetrics = DisplayMetrics()
    (context as Activity?)?.windowManager?.defaultDisplay
        ?.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun getWindowWidth(context: Context): Int {
    val displayMetrics = DisplayMetrics()
    (context as Activity?)?.windowManager?.defaultDisplay
        ?.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}