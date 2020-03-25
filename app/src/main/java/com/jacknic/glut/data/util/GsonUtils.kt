package com.jacknic.glut.data.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Gson 工具类
 *
 * @author Jacknic
 */

val gson by lazy { Gson() }

fun toJSON(obj: Any?) = gson.toJson(obj)!!

inline fun <reified T : Any> fromJSON(str: String): T? {
    return try {
        gson.fromJson<T>(str, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        null
    }
}