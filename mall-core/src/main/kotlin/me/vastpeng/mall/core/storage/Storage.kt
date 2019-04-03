package me.vastpeng.mall.core.storage

import org.springframework.core.io.Resource
import java.io.InputStream
import java.nio.file.Path
import java.util.stream.Stream


/**
 * 对象存储接口
 */
interface Storage {

    /**
     * 存储一个文件对象
     *
     * @param inputStream   文件输入流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @param keyName       文件名
     */
    fun store(inputStream: InputStream, contentLength: Long, contentType: String, keyName: String)

    fun loadAll(): Stream<Path>

    fun load(keyName: String): Path

    fun loadAsResource(keyName: String): Resource

    fun delete(keyName: String)

    fun generateUrl(keyName: String): String
}