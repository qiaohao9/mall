package me.vastpeng.mall.core.qcode

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import cn.binarywang.wx.miniapp.api.WxMaService
import me.vastpeng.mall.core.storage.StorageService
import java.io.IOException

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.RenderingHints
import java.awt.Font
import java.awt.Color
import javax.imageio.ImageIO
import org.springframework.core.io.ClassPathResource
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.io.FileNotFoundException
import me.chanjar.weixin.common.error.WxErrorException
import me.vastpeng.mall.core.system.SystemConfig
import me.vastpeng.mall.db.domain.MallGroupon
import sun.security.pkcs11.wrapper.Functions.getKeyName
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import sun.security.pkcs11.wrapper.Functions.getKeyName


@Service
class QCodeService {
    @Autowired
    var wxMaService: WxMaService? = null

    @Autowired
    private var storageService: StorageService? = null

    private fun getKeyName(goodId: String): String {
        return "GOOD_QCODE_$goodId.jpg"
    }

    fun createGrouponShareImage(goodName: String, goodPicUrl: String, groupon: MallGroupon): String {
        try {
            //创建该商品的二维码
            val file = wxMaService!!.qrcodeService.createWxaCodeUnlimit("groupon," + groupon.getId(), "pages/index/index")
            val inputStream = FileInputStream(file)
            //将商品图片，商品名字,商城名字画到模版图中
            val imageData = drawPicture(inputStream, goodPicUrl, goodName)
            val inputStream2 = ByteArrayInputStream(imageData)
            //存储分享图
            return storageService!!.store(inputStream2, imageData.size.toLong(), "image/jpeg", getKeyName(groupon.getId().toString()))
        } catch (e: WxErrorException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }


    /**
     * 创建商品分享图
     *
     * @param goodId
     * @param goodPicUrl
     * @param goodName
     */
    fun createGoodShareImage(goodId: String, goodPicUrl: String, goodName: String): String {
        if (!SystemConfig.isAutoCreateShareImage())
            return ""

        try {
            //创建该商品的二维码
            val file = wxMaService!!.qrcodeService.createWxaCodeUnlimit("goods,$goodId", "pages/index/index")
            val inputStream = FileInputStream(file)
            //将商品图片，商品名字,商城名字画到模版图中
            val imageData = drawPicture(inputStream, goodPicUrl, goodName)
            val inputStream2 = ByteArrayInputStream(imageData)
            //存储分享图
            return storageService!!.store(inputStream2, imageData.size.toLong(), "image/jpeg", getKeyName(goodId.toLong()))

        } catch (e: WxErrorException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }


    /**
     * 将商品图片，商品名字画到模版图中
     *
     * @param qrCodeImg  二维码图片
     * @param goodPicUrl 商品图片地址
     * @param goodName   商品名称
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun drawPicture(qrCodeImg: InputStream, goodPicUrl: String, goodName: String): ByteArray {
        //底图
        val redResource = ClassPathResource("back.png")
        val red = ImageIO.read(redResource.inputStream)


        //商品图片
        val goodPic = URL(goodPicUrl)
        val goodImage = ImageIO.read(goodPic)

        //小程序二维码
        val qrCodeImage = ImageIO.read(qrCodeImg)

        // --- 画图 ---

        //底层空白 bufferedImage
        val baseImage = BufferedImage(red.width, red.height, BufferedImage.TYPE_4BYTE_ABGR_PRE)

        //画上图片
        drawImgInImg(baseImage, red, 0, 0, red.width, red.height)

        //画上商品图片
        drawImgInImg(baseImage, goodImage, 71, 69, 660, 660)

        //画上小程序二维码
        drawImgInImg(baseImage, qrCodeImage, 448, 767, 300, 300)

        //写上商品名称
        drawTextInImg(baseImage, goodName, 65, 867)

        //写上商城名称
        //        drawTextInImgCenter(baseImage, shopName, 98);


        //转jpg
        val result = BufferedImage(baseImage.width, baseImage
                .height, BufferedImage.TYPE_3BYTE_BGR)
        result.graphics.drawImage(baseImage, 0, 0, null)
        val bs = ByteArrayOutputStream()
        ImageIO.write(result, "jpg", bs)

        //最终byte数组
        return bs.toByteArray()
    }

    private fun drawTextInImgCenter(baseImage: BufferedImage, textToWrite: String, y: Int) {
        val g2D = baseImage.graphics as Graphics2D
        g2D.color = Color(167, 136, 69)

        val fontName = "Microsoft YaHei"

        val f = Font(fontName, Font.PLAIN, 28)
        g2D.font = f
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 计算文字长度，计算居中的x点坐标
        val fm = g2D.getFontMetrics(f)
        val textWidth = fm.stringWidth(textToWrite)
        val widthX = (baseImage.width - textWidth) / 2
        // 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。

        g2D.drawString(textToWrite, widthX, y)
        // 释放对象
        g2D.dispose()
    }

    private fun drawTextInImg(baseImage: BufferedImage, textToWrite: String, x: Int, y: Int) {
        val g2D = baseImage.graphics as Graphics2D
        g2D.color = Color(167, 136, 69)

        //TODO 注意，这里的字体必须安装在服务器上
        g2D.font = Font("Microsoft YaHei", Font.PLAIN, 28)
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2D.drawString(textToWrite, x, y)
        g2D.dispose()
    }

    private fun drawImgInImg(baseImage: BufferedImage, imageToWrite: BufferedImage, x: Int, y: Int, width: Int, heigth: Int) {
        val g2D = baseImage.graphics as Graphics2D
        g2D.drawImage(imageToWrite, x, y, width, heigth, null)
        g2D.dispose()
    }

}