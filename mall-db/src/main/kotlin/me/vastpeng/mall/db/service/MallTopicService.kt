package me.vastpeng.mall.db.service

import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.dao.MallTopicMapper
import me.vastpeng.mall.db.domain.MallTopic
import me.vastpeng.mall.db.domain.MallTopicExample
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import javax.annotation.Resource
import java.time.LocalDateTime


@Service
class MallTopicService {
    @Resource
    private var topicMapper: MallTopicMapper? = null

    private var columns = arrayOf(MallTopic.Column.id, MallTopic.Column.title, MallTopic.Column.subtitle, MallTopic.Column.price, MallTopic.Column.picUrl, MallTopic.Column.readCount)

    fun queryList(offset: Int, limit: Int): List<MallTopic> {
        return this.queryList(offset, limit, "add_time", "desc");
    }

    fun queryList(offset: Int, limit: Int, sort: String, order: String): List<MallTopic> {
        var example = MallTopicExample()
        example.or().andDeletedEqualTo(false);
        example.orderByClause = "$sort $order";
        PageHelper.startPage<Int>(offset, limit);
        return topicMapper!!.selectByExampleSelective(example, *columns);
    }

    fun queryTotal(): Int {
        var example = MallTopicExample()
        example.or().andDeletedEqualTo(false)
        return topicMapper!!.countByExample(example).toInt()
    }

    fun findById(id: Int): MallTopic {
        var example = MallTopicExample()
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return topicMapper!!.selectOneByExampleWithBLOBs(example);
    }

    fun queryRelatedList(id: Int, offset: Int, limit: Int): List<MallTopic> {
        var example = MallTopicExample()
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        var topics = topicMapper!!.selectByExample(example);

        if (topics.size == 0) {
            return queryList(offset, limit, "add_time", "desc");
        }
        var topic = topics[0]
        example = MallTopicExample()
        example.or().andIdNotEqualTo(topic.id).andDeletedEqualTo(false)
        PageHelper.startPage<Any>(offset, limit)
        val relateds = topicMapper!!.selectByExampleWithBLOBs(example)
        return if (relateds.size != 0) {
            relateds
        } else queryList(offset, limit, "add_time", "desc")
    }

    fun querySelective(title: String, subtitle: String, page: Int, limit: Int, sort: String, order: String): List<MallTopic> {
        val example = MallTopicExample()
        val criteria = example.createCriteria()

        if (!StringUtils.isEmpty(title)) {
            criteria.andTitleLike("%$title%")
        }
        if (!StringUtils.isEmpty(subtitle)) {
            criteria.andSubtitleLike("%$subtitle%")
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Any>(page, limit)
        return topicMapper!!.selectByExampleWithBLOBs(example)
    }

    fun updateById(topic: MallTopic): Int {
        topic.updateTime = LocalDateTime.now()
        val example = MallTopicExample()
        example.or().andIdEqualTo(topic.id)
        return topicMapper!!.updateByExampleSelective(topic, example)
    }

    fun deleteById(id: Int?) {
        topicMapper!!.logicalDeleteByPrimaryKey(id)
    }

    fun add(topic: MallTopic) {
        topic.addTime = LocalDateTime.now()
        topic.updateTime = LocalDateTime.now()
        topicMapper!!.insertSelective(topic)
    }
}