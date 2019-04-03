package me.vastpeng.mall.core.system

import me.vastpeng.mall.core.util.SystemInfoPrinter
import java.util.LinkedHashMap
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


/**
 * 系统启动服务，用于检查系统状态及打印系统信息
 */
@Component
internal class SystemInistService {
    private var systemInistService: SystemInistService? = null
    @Autowired
    private lateinit var configService: ConfigService
    @Autowired
    private lateinit var environment: Environment

    private// 测试获取application-db.yml配置信息
    // 测试获取application-core.yml配置信息
    // 微信相关信息
    //测试获取System表配置信息
    val systemInfo: LinkedHashMap<String, String?>
        get() {

            val infos = LinkedHashMap<String, String?>()

            infos[SystemInfoPrinter.CREATE_PART_COPPER + 0] = "系统信息"
            infos["服务器端口"] = environment.getProperty("server.port")
            infos["数据库USER"] = environment.getProperty("spring.datasource.druid.username")
            infos["数据库地址"] = environment.getProperty("spring.datasource.druid.url")
            infos["调试级别"] = environment.getProperty("logging.level.org.linlinjava.mall.wx")
            infos[SystemInfoPrinter.CREATE_PART_COPPER + 1] = "模块状态"
            infos["邮件"] = environment.getProperty("mall.notify.mail.enable")
            infos["模版消息"] = environment.getProperty("mall.notify.wx.enable")
            infos["快递信息"] = environment.getProperty("mall.express.enable")
            infos["快递鸟ID"] = environment.getProperty("mall.express.appId")
            infos["对象存储"] = environment.getProperty("mall.storage.active")
            infos["本地对象存储路径"] = environment.getProperty("mall.storage.local.storagePath")
            infos["本地对象访问地址"] = environment.getProperty("mall.storage.local.address")
            infos["本地对象访问端口"] = environment.getProperty("mall.storage.local.port")
            infos[SystemInfoPrinter.CREATE_PART_COPPER + 2] = "微信相关"
            infos["微信APP KEY"] = environment.getProperty("mall.wx.app-id")
            infos["微信APP-SECRET"] = environment.getProperty("mall.wx.app-secret")
            infos["微信支付MCH-ID"] = environment.getProperty("mall.wx.mch-id")
            infos["微信支付MCH-KEY"] = environment.getProperty("mall.wx.mch-key")
            infos["微信支付通知地址"] = environment.getProperty("mall.wx.notify-url")
            infos[SystemInfoPrinter.CREATE_PART_COPPER + 3] = "系统设置"
            infos["自动创建朋友圈分享图"] = java.lang.Boolean.toString(SystemConfig.isAutoCreateShareImage())
            infos["商场名称"] = SystemConfig.getMallName()
            infos["首页显示记录数：NEW,HOT,BRAND,TOPIC,CatlogList,CatlogMore"] = SystemConfig.getNewLimit().toString() + "," + SystemConfig.getHotLimit() + "," + SystemConfig.getBrandLimit() + "," + SystemConfig.getTopicLimit() + "," + SystemConfig.getCatlogListLimit() + "," + SystemConfig.getCatlogMoreLimit()

            return infos
        }

    @PostConstruct
    private fun inist() {
        systemInistService = this

        try {
            SystemInfoPrinter.printInfo("mall 初始化信息", systemInfo)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}


