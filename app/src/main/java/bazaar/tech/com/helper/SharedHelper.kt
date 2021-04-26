package bazaar.tech.com.helper

import android.content.Context
import android.content.SharedPreferences
import bazaar.tech.com.AppApplication

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SharedHelper {
    companion object {
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor

        fun putKey(key: String, value: String) {
            sharedPreferences = AppApplication.appContext.getSharedPreferences("Cache", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getKey(key: String): String? {
            sharedPreferences = AppApplication.appContext.getSharedPreferences("Cache", Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, "")
        }

        fun clearSharedPreferences() {
            sharedPreferences = AppApplication.appContext.getSharedPreferences("Cache", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
        }

        fun removeData(key: String) {
            editor = sharedPreferences.edit()
            editor.remove(key)
            editor.apply()
        }
    }
}