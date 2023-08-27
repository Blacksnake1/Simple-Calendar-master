package com.simplemobiletools.calendar.pro

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class AppSharedPreferences(context: Context)  {

    var appPreferences: SharedPreferences? = null

    companion object {
        private const val APP_PREFERENCES_NAME = "APPFRELANCE"
        private const val SESSION_PREFERENCES_NAME = "APPFRELANCE-UserCache"
        private const val MODE = Context.MODE_PRIVATE
        var sInstance: AppSharedPreferences? = null
        fun getInstance(context: Context): AppSharedPreferences? {
            if (sInstance == null) {
                sInstance = AppSharedPreferences(context.applicationContext)
            }
            return sInstance
        }
    }


    init {
        appPreferences = context.getSharedPreferences(APP_PREFERENCES_NAME, MODE)
    }
    private val sessionPreferences: SharedPreferences =
        context.getSharedPreferences(SESSION_PREFERENCES_NAME, MODE)
    private val gson = GsonBuilder().create()

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    fun clearPreferences() {
        sessionPreferences.edit {
            it.clear().apply()
        }
    }


    // Template for get/set object
    fun <T> putObject(key: String?, value: T) {
        val editor: SharedPreferences.Editor = appPreferences!!.edit()
        editor.putString(key, gson.toJson(value))
        editor.apply()
    }

    fun <T> getObject(key: String, clazz: Class<T>?): T? {
        val value = appPreferences?.getString(key, null)
        return if (value != null) {
            try {
                return gson.fromJson(value, clazz)
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }
        } else {
            null
        }
    }
    inline fun <reified T> saveObjectToSharePreference(key: String, data: T) {
        val gson = Gson()
        val json = gson.toJson(data)
        appPreferences!!.edit()?.putString(key, json)?.apply()
    }

    inline fun <reified T> getObjectFromSharePreference(key: String): T? {
        val serializedObject: String? = appPreferences!!.getString(key, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<T?>() {}.type
            return gson.fromJson(serializedObject, type)
        }
        return null
    }
    inline fun <reified T> saveListToSharePreference(key: String, list: MutableList<T>) {
        val gson = Gson()
        val json = gson.toJson(list)
        appPreferences?.edit()?.putString(key, json)?.apply()
    }

    inline fun <reified T> getListFromSharePreference(key: String): MutableList<T> {
        var arrayItems = mutableListOf<T>()
        val serializedObject: String? = appPreferences?.getString(key, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<MutableList<T?>?>() {}.type
            arrayItems = gson.fromJson(serializedObject, type)
        }
        return arrayItems
    }

    fun saveInt(str: String?, i: Int) {
        appPreferences!!.edit().putInt(str, i).apply()
    }

    fun getInt(str: String?, defValue: Int?): Int {
        return appPreferences!!.getInt(str, defValue!!)
    }

    fun removeInt(str: String?) {
        appPreferences!!.edit().remove(str).apply()
    }

    fun saveBoolean(str: String?, z: Boolean) {
        appPreferences!!.edit().putBoolean(str, z).apply()
    }

    fun getBoolean(str: String?, z: Boolean): Boolean {
        return appPreferences!!.getBoolean(str, z)
    }

    fun getString(str: String?, defValue: String): String? {
        return appPreferences!!.getString(str, defValue)
    }

    fun saveString(str: String?, str2: String?) {
        appPreferences!!.edit().putString(str, str2).apply()
    }

    fun getLong(str: String?, defValue: Long?): Long {
        return appPreferences!!.getLong(str, defValue!!)
    }

    fun saveLong(str: String?, str2: Long) {
        appPreferences!!.edit().putLong(str, str2).apply()
    }
}
