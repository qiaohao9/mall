package me.vastpeng.mall.core.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException
import com.fasterxml.jackson.databind.ObjectMapper


class JacksonUtil {
    companion object {
        fun parseString(body: String, field: String): String? {
            val mapper = ObjectMapper()
            var node: JsonNode?
            try {
                node = mapper.readTree(body)
                val leaf = node!!.get(field)
                if (leaf != null)
                    return leaf!!.asText()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }


        fun parseStringList(body: String, field: String): List<String>? {
            val mapper = ObjectMapper()
            var node: JsonNode? = null
            try {
                node = mapper.readTree(body)
                val leaf = node!!.get(field)

                if (leaf != null)
                    return mapper.convertValue(leaf, object : TypeReference<List<String>>() {

                    })
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun parseInteger(body: String, field: String): Int? {
            val mapper = ObjectMapper()
            var node: JsonNode? = null
            try {
                node = mapper.readTree(body)
                val leaf = node!!.get(field)
                if (leaf != null)
                    return leaf!!.asInt()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun parseIntegerList(body: String, field: String): List<Int>? {
            val mapper = ObjectMapper()
            var node: JsonNode? = null
            try {
                node = mapper.readTree(body)
                val leaf = node!!.get(field)

                if (leaf != null)
                    return mapper.convertValue(leaf, object : TypeReference<List<Int>>() {

                    })
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }


        fun parseBoolean(body: String, field: String): Boolean? {
            val mapper = ObjectMapper()
            var node: JsonNode?
            try {
                node = mapper.readTree(body)
                val leaf = node!!.get(field)
                if (leaf != null)
                    return leaf!!.asBoolean()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun parseShort(body: String, field: String): Short? {
            val mapper = ObjectMapper()
            var node: JsonNode?
            try {
                node = mapper.readTree(body)
                val leaf = node!!.get(field)
                if (leaf != null) {
                    val value = leaf!!.asInt()
                    return value!!.toShort()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun parseByte(body: String, field: String): Byte? {
            val mapper = ObjectMapper()
            var node: JsonNode?
            try {
                node = mapper.readTree(body)
                val leaf = node!!.get(field)
                if (leaf != null) {
                    val value = leaf!!.asInt()
                    return value!!.toByte()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun <T> parseObject(body: String, field: String, clazz: Class<T>): T? {
            val mapper = ObjectMapper()
            var node: JsonNode?
            try {
                node = mapper.readTree(body)
                node = node!!.get(field)
                return mapper.treeToValue(node!!, clazz)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun toNode(json: String?): Any? {
            if (json == null) {
                return null
            }
            val mapper = ObjectMapper()
            try {
                return mapper.readTree(json)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }
    }
}