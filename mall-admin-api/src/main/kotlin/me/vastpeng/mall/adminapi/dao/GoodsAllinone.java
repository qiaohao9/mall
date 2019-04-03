package me.vastpeng.mall.adminapi.dao;

import me.vastpeng.mall.db.domain.MallGoods;
import me.vastpeng.mall.db.domain.MallGoodsAttribute;
import me.vastpeng.mall.db.domain.MallGoodsProduct;
import me.vastpeng.mall.db.domain.MallGoodsSpecification;

public class GoodsAllinone {
    MallGoods goods;
    MallGoodsSpecification[] specifications;
    MallGoodsAttribute[] attributes;
    // 这里采用 Product 再转换到 MallGoodsProduct
    MallGoodsProduct[] products;

    public MallGoods getGoods() {
        return goods;
    }

    public void setGoods(MallGoods goods) {
        this.goods = goods;
    }

    public MallGoodsProduct[] getProducts() {
        return products;
    }

    public void setProducts(MallGoodsProduct[] products) {
        this.products = products;
    }

    public MallGoodsSpecification[] getSpecifications() {
        return specifications;
    }

    public void setSpecifications(MallGoodsSpecification[] specifications) {
        this.specifications = specifications;
    }

    public MallGoodsAttribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(MallGoodsAttribute[] attributes) {
        this.attributes = attributes;
    }

}
