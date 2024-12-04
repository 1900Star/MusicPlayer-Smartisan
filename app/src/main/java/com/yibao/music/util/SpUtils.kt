package com.yibao.music.util

import android.content.Context
import android.content.SharedPreferences
import com.yibao.music.util.SpUtils.ContentValue

/**
 * @author luoshipeng
 * createDate：2020/4/2 0002 10:38
 * className   SharedPreferencesUtil
 * Des：TODO
 */
class SpUtils(context: Context, fileName: String?) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    class ContentValue(var key: String, var value: Any)

    fun putValues(vararg contentValues: ContentValue) {
        val editor = sharedPreferences.edit()
        for (contentValue in contentValues) {
            if (contentValue.value is String) {
                editor.putString(contentValue.key, contentValue.value.toString()).apply()
            } else if (contentValue.value is Int) {
                editor.putInt(contentValue.key, contentValue.value.toString().toInt()).apply()
            } else if (contentValue.value is Long) {
                editor.putLong(contentValue.key, contentValue.value.toString().toLong()).apply()
            } else if (contentValue.value is Boolean) {
                editor.putBoolean(
                    contentValue.key,
                    java.lang.Boolean.parseBoolean(contentValue.value.toString())
                ).apply()
            }
        }
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getBoolean(key: String?, b: Boolean?): Boolean {
        return sharedPreferences.getBoolean(key, b!!)
    }

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getLong(key: String?): Long {
        return sharedPreferences.getLong(key, -1)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}