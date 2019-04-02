package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallFeedbackMapper
import me.vastpeng.mall.db.domain.MallFeedback
import me.vastpeng.mall.db.domain.MallFeedbackExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallFeedbackService {
    @Resource
    private lateinit var feedbackMapper: MallFeedbackMapper

    fun add(feedBack: MallFeedback): Int {
        feedBack.addTime = LocalDateTime.now()
        feedBack.updateTime = LocalDateTime.now()
        return feedbackMapper.insertSelective(feedBack)
    }

    fun querySelective(userId: Int, userName: String, page: Int, limit: Int, sort: String, order: String): List<MallFeedback> {
        var example: MallFeedbackExample = MallFeedbackExample()
        var criteria: MallFeedbackExample.Criteria = example.createCriteria()

        if (userId != null) {
            criteria.andUserIdEqualTo(userId)
        }
        if (!StringUtils.isEmpty(userName)) {
            criteria.andUsernameLike("%$userName%")
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, limit)
        return feedbackMapper.selectByExample(example)
    }
}