package me.vastpeng.mall.db.mybatis

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.lang.Exception
import java.lang.RuntimeException
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class JsonIntegerArrayTypeHandler : BaseTypeHandler<IntArray>() {
    companion object {
        private val mapper: ObjectMapper = ObjectMapper()
    }

    override fun getNullableResult(rs: ResultSet?, columnName: String?): IntArray? {
        return this.toObject(rs!!.getString(columnName))
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): IntArray? {
        return this.toObject(rs!!.getString(columnIndex))
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): IntArray? {
        return this.toObject(cs!!.getString(columnIndex))
    }

    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: IntArray?, jdbcType: JdbcType?) {
        ps!!.setString(i, this.toJson(parameter))
    }

    private fun toJson(params: IntArray?): String {
        try {
            return mapper.writeValueAsString(params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "[]"
    }

    private fun toObject(content: String): IntArray? {
        return if (!content.isEmpty()) {
            try {
                mapper.readValue(content, IntArray::class.java)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        } else {
            null
        }
    }

}