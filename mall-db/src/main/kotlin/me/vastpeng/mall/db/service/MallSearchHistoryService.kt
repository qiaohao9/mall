package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallSearchHistoryMapper
import me.vastpeng.mall.db.domain.MallSearchHistory
import me.vastpeng.mall.db.domain.MallSearchHistoryExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallSearchHistoryService {
    @Resource
    private var searchHistoryMapper: MallSearchHistoryMapper? = null

    fun save(searchHistory: MallSearchHistory) {
        searchHistory.addTime = LocalDateTime.now()
        searchHistory.updateTime = LocalDateTime.now()
        searchHistoryMapper!!.insertSelective(searchHistory)
    }

    fun queryByUid(uid: Int): List<MallSearchHistory> {
        var example = MallSearchHistoryExample()
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false)
        example.isDistinct = true
        return searchHistoryMapper!!.selectByExampleSelective(example, MallSearchHistory.Column.keyword)
    }

    fun deleteByUid(uid: Int) {
        var example = MallSearchHistoryExample()
        example.or().andUserIdEqualTo(uid)
        searchHistoryMapper!!.logicalDeleteByExample(example)
    }

    fun querySelective(userId: String, keyword: String, page: Int, size: Int, sort: String, order: String): List<MallSearchHistory> {
        var example = MallSearchHistoryExample()
        var criteria = example.createCriteria()


        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId))
        }
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%$keyword%")
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, size)
        return searchHistoryMapper!!.selectByExample(example)
    }
}