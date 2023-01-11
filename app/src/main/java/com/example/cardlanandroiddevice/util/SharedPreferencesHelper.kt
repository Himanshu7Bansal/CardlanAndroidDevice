package com.example.cardlanandroiddevice.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Save the information configuration.
 */
class SharedPreferencesHelper(context: Context, FILE_NAME: String?) {

    private val sharedPreferences: SharedPreferences

    /*
     * Save your phone's name
     */
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
    }

    /**
     * put the saved data
     * @param key The name of the preference to modify.
     * @param object
     */
    fun put(key: String?, `object`: Any) {
        if (`object` is String) {
            editor.putString(key, `object`)
        } else if (`object` is Int) {
            editor.putInt(key, `object`)
        } else if (`object` is Boolean) {
            editor.putBoolean(key, `object`)
        } else if (`object` is Float) {
            editor.putFloat(key, `object`)
        } else if (`object` is Long) {
            editor.putLong(key, `object`)
        } else {
            editor.putString(key, `object`.toString())
        }
        editor.commit()
    }

    /**
     * Get the saved data
     * @param key
     * @param defaultObject
     * @return
     */
    fun getSharedPreference(key: String?, defaultObject: Any?): Any? {
        return if (defaultObject is String) {
            sharedPreferences.getString(key, defaultObject as String?)
        } else if (defaultObject is Int) {
            sharedPreferences.getInt(key, (defaultObject as Int?)!!)
        } else if (defaultObject is Boolean) {
            sharedPreferences.getBoolean(key, (defaultObject as Boolean?)!!)
        } else if (defaultObject is Float) {
            sharedPreferences.getFloat(key, (defaultObject as Float?)!!)
        } else if (defaultObject is Long) {
            sharedPreferences.getLong(key, (defaultObject as Long?)!!)
        } else {
            sharedPreferences.getString(key, null)
        }
    }

    /**
     * Removes a value that already corresponds to a key value
     */
    fun remove(key: String?) {
        editor.remove(key)
        editor.commit()
    }

    /**
     * Clear all data
     */
    fun clear() {
        editor.clear()
        editor.commit()
    }

    /**
     * Query if a key exists
     */
    fun contain(key: String?): Boolean {
        return sharedPreferences.contains(key)
    }

    /**
     * Returns all key pairs
     */
    val all: Map<String, *>
        get() = sharedPreferences.all
}