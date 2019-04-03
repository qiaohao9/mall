package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.MallUserMapper
import me.vastpeng.mall.db.domain.MallUser
import me.vastpeng.mall.db.domain.MallUserExample
import org.springframework.stereotype.Service
import javax.annotation.Resource
import java.time.LocalDateTime
import com.github.pagehelper.PageHelper
import me.vastpeng.mall.db.domain.UserVo
import org.springframework.util.StringUtils


@Service
class MallUserService {
    @Resource
    private lateinit var userMapper: MallUserMapper

    fun findById(userId: Int?): MallUser? {
        return userMapper.selectByPrimaryKey(userId)
    }

    fun findUserVoById(userId: Int?): UserVo? {
        val user = findById(userId)
        val userVo = UserVo()
        if (user != null) {
            userVo.nickName = user.nickname
            userVo.avatar = user.avatar
        }
        return userVo
    }

    fun queryByOid(openId: String): MallUser? {
        val example = MallUserExample()
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false)
        return userMapper.selectOneByExample(example)
    }

    fun add(user: MallUser) {
        user.addTime = LocalDateTime.now()
        user.updateTime = LocalDateTime.now()
        userMapper.insertSelective(user)
    }

    fun updateById(user: MallUser): Int {
        user.updateTime = LocalDateTime.now()
        return userMapper.updateByPrimaryKeySelective(user)
    }

    fun querySelective(username: String, mobile: String, page: Int, size: Int, sort: String, order: String): List<MallUser> {
        val example = MallUserExample()
        val criteria = example.createCriteria()

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%$username%")
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileEqualTo(mobile)
        }
        criteria.andDeletedEqualTo(false)

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.orderByClause = "$sort $order"
        }

        PageHelper.startPage<Any>(page, size)
        return userMapper.selectByExample(example)
    }


    fun count(): Int {
        val example = MallUserExample()
        example.or().andDeletedEqualTo(false)

        return userMapper.countByExample(example) as Int
    }

    fun queryByUsername(username: String): List<MallUser> {
        val example = MallUserExample()
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false)
        return userMapper.selectByExample(example)
    }

    fun checkByUsername(username: String): Boolean {
        val example = MallUserExample()
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false)
        return userMapper.countByExample(example) !== 0L
    }

    fun queryByMobile(mobile: String): List<MallUser> {
        val example = MallUserExample()
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false)
        return userMapper.selectByExample(example)
    }

    fun queryByOpenid(openid: String): List<MallUser> {
        val example = MallUserExample()
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false)
        return userMapper.selectByExample(example)
    }

    fun deleteById(id: Int?) {
        userMapper.logicalDeleteByPrimaryKey(id)
    }


}