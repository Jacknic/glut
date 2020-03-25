package com.jacknic.glut.data.net

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * HTML DOM 转换器
 *
 * @author Jacknic
 */
class DomConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type == Document::class.java) {
            return Converter<ResponseBody, Document> {
                Jsoup.parse(it.string())
            }
        }
        return null
    }
}