package me.vastpeng.mall.core.util

import java.io.IOException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


/**
 * 向指定 URL 发送POST方法的请求
 *
 * @return 远程资源的响应结果
 */
class HttpUtil {
    companion object {
        /**
         * 向指定 URL 发送POST方法的请求
         *
         * @param url    发送请求的 URL
         * @param params 请求的参数集合
         * @return 远程资源的响应结果
         */
        @SuppressWarnings
        fun sendPost(url: String, params: Map<String, String>?): String {
            var out: OutputStreamWriter? = null
            var _in: BufferedReader? = null
            val result = StringBuilder()
            try {
                val realUrl = URL(url)
                val conn = realUrl.openConnection() as HttpURLConnection
                // 发送POST请求必须设置如下两行
                conn.doOutput = true
                conn.doInput = true
                // POST方法
                conn.requestMethod = "POST"
                // 设置通用的请求属性
                conn.setRequestProperty("accept", "*/*")
                conn.setRequestProperty("connection", "Keep-Alive")
                conn.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.connect()
                // 获取URLConnection对象对应的输出流
                out = OutputStreamWriter(conn.getOutputStream(), "UTF-8")
                // 发送请求参数
                if (params != null) {
                    val param = StringBuilder()
                    for ((key, value) in params) {
                        if (param.isNotEmpty()) {
                            param.append("&")
                        }
                        param.append(key)
                        param.append("=")
                        param.append(value)
                        //System.out.println(entry.getKey()+":"+entry.getValue());
                    }
                    //System.out.println("param:"+param.toString());
                    out.write(param.toString())
                }
                // flush输出流的缓冲
                out.flush()
                // 定义BufferedReader输入流来读取URL的响应
                _in = BufferedReader(
                        InputStreamReader(conn.getInputStream(), "UTF-8"))
                for (line in _in.readLine()){
                    result.append(line)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    out?.close()
                    _in?.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

            }//使用finally块来关闭输出流、输入流
            return result.toString()
        }
    }
}