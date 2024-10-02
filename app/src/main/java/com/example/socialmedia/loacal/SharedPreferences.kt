package com.example.socialmedia.loacal

import android.content.Context
import android.content.SharedPreferences


class Preferences(var sharedPreferences: SharedPreferences? = null) {
    fun setString(key: String, string: String) {
        sharedPreferences?.edit()?.putString(key, string)?.apply()
    }

    fun setBoolean(key: String, boolean: Boolean) {
        sharedPreferences?.edit()?.putBoolean(key, boolean)?.apply()
    }

    fun setInt(key: String, int: Int) {
        sharedPreferences?.edit()?.putInt(key, int)?.apply()
    }

    fun setFloat(key: String, float: Float) {
        sharedPreferences?.edit()?.putFloat(key, float)?.apply()
    }

    fun setLong(key: String, long: Long) {
        sharedPreferences?.edit()?.putLong(key, long)?.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences?.getString(key, null)
    }

    fun getBoolean(key: String): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }

    fun getInt(key: String): Int? {
        return sharedPreferences?.getInt(key, 0)
    }

    fun getFloat(key: String): Long? {
        return sharedPreferences?.getLong(key, 0)
    }

    fun getLong(key: String): Float? {
        return sharedPreferences?.getFloat(key, 0f)
    }

    fun setValue(key: String, value: String) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    fun getValue(key: String): String? {
        return sharedPreferences?.getString(key, null)
    }


    fun setIntValue(key: String, value: Int) {
        sharedPreferences?.edit()?.putInt(key, value)?.apply()
    }

    fun removeString(key:String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
    }
  /*  fun getIntValue(key: String, i: Int): Int {
        return sharedPreferences.getInt(key, i.toInt())
    }
*/
    companion object {
        private val PREFS_NAME = "share_prefs"
        private var INSTANCE: Preferences? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(Preferences::class.java) {
            INSTANCE ?: Preferences(
                context.getSharedPreferences(PREFS_NAME, 0)
            )
                .also { INSTANCE = it }
        }
    }
}