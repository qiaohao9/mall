package me.vastpeng.mall.db.mybatis

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.io.IOException
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class JsonNodeTypeHandler : BaseTypeHandler<JsonNode>() {
    companion object {
        private val mapper: ObjectMapper = ObjectMapper()
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String?): JsonNode? {
        var jsonSource: String = rs!!.getString(columnName) ?: return null
        try {
            var jsonNode: JsonNode = mapper.readTree(jsonSource)
            return jsonNode
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): JsonNode? {
        var jsonSource: String = rs!!.getString(columnIndex) ?: return null
        try {
            var jsonNode: JsonNode = mapper.readTree(jsonSource)
            return jsonNode
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): JsonNode? {
        var jsonSource: String = cs!!.getString(columnIndex) ?: return null
        try {
            var jsonNode: JsonNode = mapper.readTree(jsonSource)
            return jsonNode
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: JsonNode?, jdbcType: JdbcType?) {
        var str: String
        str = try {
            mapper.writeValueAsString(parameter)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            "{}"
        }
        ps!!.setString(i, str)
    }

}