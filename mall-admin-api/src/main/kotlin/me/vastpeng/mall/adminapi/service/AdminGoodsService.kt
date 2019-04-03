package me.vastpeng.mall.adminapi.service

import me.vastpeng.mall.core.qcode.QCodeService
import me.vastpeng.mall.db.service.*
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.HashMap
import com.github.pagehelper.PageInfo
import me.vastpeng.mall.adminapi.dao.GoodsAllinone
import me.vastpeng.mall.adminapi.util.AdminResponseCode.GOODS_NAME_EXIST
import me.vastpeng.mall.adminapi.util.AdminResponseCode.GOODS_UPDATE_NOT_ALLOWED
import me.vastpeng.mall.core.util.ResponseUtil
import me.vastpeng.mall.db.domain.MallGoods
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.util.ArrayList
import me.vastpeng.mall.adminapi.util.CatVo


@Service
class AdminGoodsService {
    private val logger = LogFactory.getLog(AdminGoodsService::class.java)

    @Autowired
    private lateinit var goodsService: MallGoodsService
    @Autowired
    private lateinit var specificationService: MallGoodsSpecificationService
    @Autowired
    private lateinit var attributeService: MallGoodsAttributeService
    @Autowired
    private lateinit var productService: MallGoodsProductService
    @Autowired
    private lateinit var categoryService: MallCategoryService
    @Autowired
    private lateinit var brandService: MallBrandService
    @Autowired
    private lateinit var cartService: MallCartService
    @Autowired
    private lateinit var orderGoodsService: MallOrderGoodsService
    @Autowired
    private lateinit var qCodeService: QCodeService

    fun list(goodsSn: String, name: String, page: Int?, limit: Int?, sort: String, order: String): Any {
        val goodsList = goodsService.querySelective(goodsSn, name, page, limit, sort, order)
        val total = PageInfo.of(goodsList).total
        val data = HashMap<String, Any>()
        data["total"] = total
        data["items"] = goodsList

        return ResponseUtil.ok(data)
    }

    private fun validate(goodsAllinone: GoodsAllinone): Any? {
        var goods = goodsAllinone.goods
        var name = goods.name
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument()
        }

        var goodsSn = goods.goodsSn
        if (StringUtils.isEmpty(goodsSn)) {
            return ResponseUtil.badArgument()
        }

        // 品牌商可以不设置，如果设置则需要验证品牌商存在
        val brandId = goods.brandId
        if (brandId != null && brandId != 0) {
            if (brandService.findById(brandId) == null) {
                return ResponseUtil.badArgumentValue()
            }
        }

        // 分类可以不设置，如果设置则需要验证分类存在
        val categoryId = goods.categoryId
        if (categoryId != null && categoryId != 0) {
            if (categoryService.findById(categoryId) == null) {
                return ResponseUtil.badArgumentValue()
            }
        }

        var attributes = goodsAllinone.attributes
        for (attribute in attributes) {
            val attr = attribute.attribute
            if (StringUtils.isEmpty(attr)) {
                return ResponseUtil.badArgument()
            }
            val value = attribute.value
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument()
            }
        }

        var specifications = goodsAllinone.specifications
        for (specification in specifications) {
            val spec = specification.specification
            if (StringUtils.isEmpty(spec)) {
                return ResponseUtil.badArgument()
            }
            val value = specification.value
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument()
            }
        }

        var products = goodsAllinone.products
        for (product in products) {
            val number = product.number
            if (number == null || number < 0) {
                return ResponseUtil.badArgument()
            }

            val price = product.price ?: return ResponseUtil.badArgument()
            val productSpecifications = product.specifications
            if (productSpecifications.isEmpty()) {
                return ResponseUtil.badArgument()
            }
        }

        return null
    }

    /**
     * 编辑商品
     * <p>
     * TODO
     * 目前商品修改的逻辑是
     * 1. 更新 mall_goods表
     * 2. 逻辑删除 mall_goods_specification、 mall_goods_attribute、 mall_goods_product
     * 3. 添加 mall_goods_specification、 mall_goods_attribute、 mall_goods_product
     * <p>
     * 这里商品三个表的数据采用删除再添加的策略是因为
     * 商品编辑页面，支持管理员添加删除商品规格、添加删除商品属性，因此这里仅仅更新是不可能的，
     * 只能删除三个表旧的数据，然后添加新的数据。
     * 但是这里又会引入新的问题，就是存在订单商品货品ID指向了失效的商品货品表。
     * 因此这里会拒绝管理员编辑商品，如果订单或购物车中存在商品。
     * 所以这里可能需要重新设计。
     */
    @Transactional
    fun update(goodsAllinone: GoodsAllinone): Any? {
        var error = validate(goodsAllinone) ?: return null

        var goods = goodsAllinone.goods
        var attributes = goodsAllinone.attributes
        var specifications = goodsAllinone.specifications
        var products = goodsAllinone.products

        var id = goods.id
        // 检查是否存在购物车商品或者订单商品
        // 如果存在则拒绝修改商品。
        if (orderGoodsService.checkExist(id)) {
            return ResponseUtil.fail(GOODS_UPDATE_NOT_ALLOWED, "商品已经在订单中，不能修改")
        }
        if (cartService.checkExist(id)) {
            return ResponseUtil.fail(GOODS_UPDATE_NOT_ALLOWED, "商品已经在购物车中，不能修改")
        }
        //将生成的分享图片地址写入数据库
        val url = qCodeService.createGoodShareImage(goods.id.toString(), goods.picUrl, goods.name)
        goods.shareUrl = url

        // 商品基本信息表mall_goods
        if (goodsService.updateById(goods) == 0) {
            throw RuntimeException("更新数据失败")
        }

        var gid = goods.id
        specificationService.deleteByGid(gid)
        attributeService.deleteByGid(gid)
        productService.deleteByGid(gid)

        // 商品规格表mall_goods_specification
        for (specification in specifications) {
            specification.id = goods.id
            specificationService.add(specification)
        }

        // 商品参数表mall_goods_attribute
        for (attribute in attributes) {
            attribute.goodsId = goods.id
            attributeService.add(attribute)
        }

        // 商品货品表mall_product
        for (product in products) {
            product.goodsId = goods.id
            productService.add(product)
        }

        qCodeService.createGoodShareImage(goods.id.toString(), goods.picUrl, goods.name)
        return ResponseUtil.ok()
    }

    @Transactional
    fun delete(goods: MallGoods): Any {
        var id: Int? = goods.id ?: return ResponseUtil.badArgument()
        var gid = goods.id

        goodsService.deleteById(gid)
        specificationService.deleteByGid(gid)
        attributeService.deleteByGid(gid)
        productService.deleteByGid(gid)
        return ResponseUtil.ok()
    }

    @Transactional
    fun create(goodsAllinone: GoodsAllinone): Any? {
        var error = validate(goodsAllinone) ?: return null

        var goods = goodsAllinone.goods
        var attributes = goodsAllinone.attributes
        var specifications = goodsAllinone.specifications
        var products = goodsAllinone.products
        var name = goods.name

        if (goodsService.checkExistByName(name)) {
            return ResponseUtil.fail(GOODS_NAME_EXIST, "商品名已经存在")
        }

        // 商品基本信息表mall_goods
        goodsService.add(goods)

        //将生成的分享图片地址写入数据库
        var url = qCodeService.createGoodShareImage(goods.id.toString(), goods.picUrl, goods.name)
        if (!StringUtils.isEmpty(url)) {
            goods.shareUrl = url
            if (goodsService.updateById(goods) == 0) {
                throw RuntimeException("更新数据失败")
            }
        }
        // 商品规格表mall_goods_specification
        for (specification in specifications) {
            specification.goodsId = goods.id
            specificationService.add(specification)
        }

        // 商品参数表mall_goods_attribute
        for (attribute in attributes) {
            attribute.goodsId = goods.id
            attributeService.add(attribute)
        }

        // 商品货品表mall_product
        for (product in products) {
            product.goodsId = goods.id
            productService.add(product)
        }
        return ResponseUtil.ok()
    }

    fun list2(): Any {
        // http://element-cn.eleme.io/#/zh-CN/component/cascader
        // 管理员设置“所属分类”
        val l1CatList = categoryService.queryL1()
        val categoryList = ArrayList<CatVo>(l1CatList.size)

        for (l1 in l1CatList) {
            val l1CatVo = CatVo()
            l1CatVo.value = l1.id
            l1CatVo.label = l1.name

            val l2CatList = categoryService.queryByPid(l1.id)
            val children = ArrayList<CatVo>(l2CatList.size)
            for (l2 in l2CatList) {
                val l2CatVo = CatVo()
                l2CatVo.value = l2.id
                l2CatVo.label = l2.name
                children.add(l2CatVo)
            }
            l1CatVo.children = children

            categoryList.add(l1CatVo)
        }

        // http://element-cn.eleme.io/#/zh-CN/component/select
        // 管理员设置“所属品牌商”
        val list = brandService.all()
        val brandList = ArrayList<Map<String, Any>>(l1CatList.size)
        for (brand in list) {
            val b = HashMap<String, Any>(2)
            b["value"] = brand.id
            b["label"] = brand.name
            brandList.add(b)
        }

        val data = HashMap<String, Any>()
        data["categoryList"] = categoryList
        data["brandList"] = brandList
        return ResponseUtil.ok(data)
    }

    fun detail(id: Int): Any {
        val goods = goodsService.findById(id)
        val products = productService.queryByGid(id)
        val specifications = specificationService.queryByGid(id)
        val attributes = attributeService.queryByGid(id)

        val categoryId = goods.categoryId
        val category = categoryService.findById(categoryId)
        var categoryIds = arrayOf<Int>()
        if (category != null) {
            val parentCategoryId = category.pid
            categoryIds = arrayOf(parentCategoryId, categoryId)
        }

        val data = HashMap<String, Any>()
        data["goods"] = goods
        data["specifications"] = specifications
        data["products"] = products
        data["attributes"] = attributes
        data["categoryIds"] = categoryIds

        return ResponseUtil.ok(data)
    }
}