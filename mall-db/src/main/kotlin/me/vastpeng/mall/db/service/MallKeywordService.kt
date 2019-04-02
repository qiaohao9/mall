package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallKeywordMapper
import me.vastpeng.mall.db.domain.MallKeyword
import me.vastpeng.mall.db.domain.MallKeywordExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallKeywordService {
    @Resource
    private var keywordsMapper: MallKeywordMapper? = null

    fun queryDefault(): MallKeyword? {
        var example = MallKeywordExample()
        example.or().andIsDefaultEqualTo(true).andDeletedEqualTo(false)
        return keywordsMapper!!.selectOneByExample(example)
    }

    fun queryHots(): List<MallKeyword> {
        var example = MallKeywordExample()
        example.or().andIsHotEqualTo(true).andDeletedEqualTo(false)
        return keywordsMapper!!.selectByExample(example)
    }

    fun queryByKeyword(keyword: String, page: Int, size: Int): List<MallKeyword> {
        var example = MallKeywordExample()
        example.isDistinct = true
        example.or().andKeywordLike("%$keyword%").andDeletedEqualTo(false)
        PageHelper.startPage<Int>(page, size)
        return keywordsMapper!!.selectByExampleSelective(example, MallKeyword.Column.keyword)
    }

    fun querySelective(keyword: String, url: String, page: Int, limit: Int, sort: String, order: String): List<MallKeyword> {
        var example = MallKeywordExample()
        var criteria = example.createCriteria()

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%$keyword%")
        }
        if (!StringUtils.isEmpty(url)) {
            criteria.andUrlLike("%$url%")
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) example.orderByClause = "$sort $order"

        PageHelper.startPage<Int>(page, limit)
        return keywordsMapper!!.selectByExample(example)
    }

    fun add(keywords: MallKeyword) {
        keywords.addTime = LocalDateTime.now()
        keywords.updateTime = LocalDateTime.now()
        keywordsMapper!!.insertSelective(keywords)
    }

    fun findById(id: Int): MallKeyword {
        return keywordsMapper!!.selectByPrimaryKey(id)
    }

    fun updateById(keywords:MallKeyword):Int{
        keywords.updateTime= LocalDateTime.now()
        return keywordsMapper!!.updateByPrimaryKeySelective(keywords)
    }

    fun deleteById(id:Int){
        keywordsMapper!!.logicalDeleteByPrimaryKey(id)
    }
    
}