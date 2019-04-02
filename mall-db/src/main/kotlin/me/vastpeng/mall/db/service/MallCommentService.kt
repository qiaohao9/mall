package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallCommentMapper
import me.vastpeng.mall.db.domain.MallComment
import me.vastpeng.mall.db.domain.MallCommentExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.lang.RuntimeException
import java.time.LocalDateTime
import javax.annotation.Resource

@Service
class MallCommentService {
    @Resource
    private var commentMapper: MallCommentMapper? = null

    fun queryGoodsByGid(id: Int, offset: Int, limit: Int): List<MallComment> {
        var example: MallCommentExample = MallCommentExample()
        example.orderByClause = MallComment.Column.addTime.desc()
        example.or().andValueIdEqualTo(id).andTypeEqualTo(0).andDeletedEqualTo(false)
        PageHelper.startPage<Int>(offset, limit)
        return commentMapper!!.selectByExample(example)
    }

    fun query(type: Byte, valueId: Int, showType: Int, offset: Int, limit: Int): List<MallComment> {
        var example: MallCommentExample = MallCommentExample()
        example.orderByClause = MallComment.Column.addTime.desc()

        when (showType) {
            0 -> example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false)
            1 -> example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true).andDeletedEqualTo(false)
            else -> throw RuntimeException("showType 不支持")
        }

        PageHelper.startPage<Int>(offset, limit)
        return commentMapper!!.selectByExample(example)
    }

    fun count(type: Byte, valueId: Int, showType: Int): Int {
        var example: MallCommentExample = MallCommentExample()
        when (showType) {
            0 -> example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false)
            1 -> example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true).andDeletedEqualTo(false)
            else -> throw RuntimeException("showType 不支持")
        }

        return commentMapper!!.countByExample(example).toInt()
    }

    fun save(comment: MallComment): Int {
        comment.addTime = LocalDateTime.now()
        comment.updateTime = LocalDateTime.now()
        return commentMapper!!.insertSelective(comment)
    }

    fun querySelective(userId: String, valueId: String, page: Int, size: Int, sort: String, order: String): List<MallComment> {
        var example: MallCommentExample = MallCommentExample()
        var criteria: MallCommentExample.Criteria = example.createCriteria()

        // type=2 是订单商品回复，这里过滤
        criteria.andTypeNotEqualTo(2)
        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId))
        }
        if (!StringUtils.isEmpty(valueId)) {
            criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeEqualTo(0)
        }

        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }
        PageHelper.startPage<Int>(page, size)
        return commentMapper!!.selectByExample(example)
    }

    fun deleteById(id: Int) {
        commentMapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun queryReply(id: Int): String? {
        var example: MallCommentExample = MallCommentExample()
        example.or().andTypeEqualTo(2).andValueIdEqualTo(id)
        var commentReply: List<MallComment> = commentMapper!!.selectByExampleSelective(example, MallComment.Column.content)
        // 目前业务只支持回复一次
        if (commentReply.size == 1) {
            return commentReply[0].content
        }

        return null
    }

    fun findById(id: Int): MallComment {
        return commentMapper!!.selectByPrimaryKey(id)
    }

}