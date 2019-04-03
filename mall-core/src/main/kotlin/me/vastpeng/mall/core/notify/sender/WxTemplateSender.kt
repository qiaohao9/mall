package me.vastpeng.mall.core.notify.sender

import cn.binarywang.wx.miniapp.bean.WxMaTemplateData
import java.util.ArrayList
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage
import org.springframework.beans.factory.annotation.Autowired
import cn.binarywang.wx.miniapp.api.WxMaService
import me.vastpeng.mall.db.service.MallUserFormIdService
import org.apache.commons.logging.LogFactory


/**
 * 微信模版消息通知
 */
class WxTemplateSender {
    private val logger = LogFactory.getLog(WxTemplateSender::class.java)

    @Autowired
    private val wxMaService: WxMaService? = null

    @Autowired
    private val formIdService: MallUserFormIdService? = null

    /**
     * 发送微信消息(模板消息),不带跳转
     *
     * @param toUser    用户 OpenID
     * @param templateId 模板消息ID
     * @param parms     详细内容
     */
    fun sendWechatMsg(toUser: String, templateId: String, parms: Array<String>) {
        sendMsg(toUser, templateId, parms, "", "", "")
    }

    /**
     * 发送微信消息(模板消息),带跳转
     *
     * @param touser    用户 OpenID
     * @param templatId 模板消息ID
     * @param parms     详细内容
     * @param page      跳转页面
     */
    fun sendWechatMsg(touser: String, templatId: String, parms: Array<String>, page: String) {
        sendMsg(touser, templatId, parms, page, "", "")
    }

    private fun sendMsg(touser: String, templatId: String, parms: Array<String>, page: String, color: String, emphasisKeyword: String) {
        val userFormId = formIdService!!.queryByOpenId(touser)


        val msg = WxMaTemplateMessage()
        msg.templateId = templatId
        msg.toUser = touser
        msg.formId = userFormId.formid
        msg.page = page
        msg.color = color
        msg.emphasisKeyword = emphasisKeyword
        msg.data = createMsgData(parms)

        try {
            wxMaService!!.msgService.sendTemplateMsg(msg)
            if (formIdService!!.updateUserFormId(userFormId) === 0) {
                logger.warn("更新数据已失效")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun createMsgData(parms: Array<String>): List<WxMaTemplateData> {
        val dataList = ArrayList<WxMaTemplateData>()
        for (i in 1..parms.size) {
            dataList.add(WxMaTemplateData("keyword$i", parms[i - 1]))
        }

        return dataList
    }
}