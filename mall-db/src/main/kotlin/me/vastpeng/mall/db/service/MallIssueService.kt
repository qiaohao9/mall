package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallIssueMapper
import me.vastpeng.mall.db.domain.MallIssue
import me.vastpeng.mall.db.domain.MallIssueExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallIssueService {
    @Resource
    private var issueMapper: MallIssueMapper? = null

    fun query(): List<MallIssue> {
        var example = MallIssueExample()
        example.or().andDeletedEqualTo(false)
        return issueMapper!!.selectByExample(example)
    }

    fun deleteById(id: Int) {
        issueMapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun add(issue: MallIssue) {
        issue.addTime = LocalDateTime.now()
        issue.updateTime = LocalDateTime.now()
        issueMapper!!.insertSelective(issue)
    }

    fun querySelective(question: String, page: Int, size: Int, sort: String, order: String): List<MallIssue> {
        var example = MallIssueExample()
        var criteria = example.createCriteria()

        if (!StringUtils.isEmpty(question)) {
            criteria.andQuestionLike("%$question%")
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Int>(page, size)
        return issueMapper!!.selectByExample(example)
    }

    fun updateById(issue: MallIssue): Int {
        issue.updateTime = LocalDateTime.now()
        return issueMapper!!.updateByPrimaryKeySelective(issue)
    }

    fun findById(id: Int): MallIssue {
        return issueMapper!!.selectByPrimaryKey(id)
    }
}