package com.labijie.application

import org.springframework.util.Base64Utils

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-25
 */
object ForwardTokenCodec {

    /**
     *  编码游标
     *  @param queryResult 查询结果，一个列表
     *  @param offsetField 用于排序的字段（例如 A::createdTime）
     *  @param keyField 数据的 key （主键， 例如： A::id）
     */
    fun <TElement> encode(
        queryResult: List<TElement>,
        offsetField:(element:TElement)->Any?,
        keyField:(element:TElement)->Any?):String{

        if(queryResult.isEmpty()){
            return ""
        }
        val keys = mutableSetOf<String>()
        val last = queryResult.last()
        var lastIndex = queryResult.size - 1
        val lastOffset = offsetField(last)
        val lastKey = keyField(last)
        keys.add(lastKey.toString())
        while (lastIndex > 0) {
            lastIndex--
            val nextEntry = queryResult[lastIndex]
            val nextOffset = offsetField(nextEntry)
            if (lastOffset == nextOffset) {
                val nextKey = keyField(nextEntry)
                keys.add(nextKey.toString())
            } else {
                break
            }
        }
        val token = "${lastOffset}:${keys.joinToString(":")}"
        return Base64Utils.encodeToUrlSafeString(token.toByteArray(Charsets.US_ASCII))
    }

    fun decode(forwardToken:String): ForwardOffset {
        val tokenString = Base64Utils.decodeFromUrlSafeString(forwardToken).toString(Charsets.US_ASCII)
        val elements = tokenString.split(":")
        val offset = elements.first()
        val keys = elements.subList(1, elements.size)
        return ForwardOffset(offset, keys)
    }

    class ForwardOffset(val offsetValue: String, val excludeKeys:List<String>)
}