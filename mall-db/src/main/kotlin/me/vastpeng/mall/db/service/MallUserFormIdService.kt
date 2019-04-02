package me.vastpeng.mall.db.service

import me.vastpeng.mall.db.dao.MallUserFormidMapper
import me.vastpeng.mall.db.domain.MallUserFormid
import me.vastpeng.mall.db.domain.MallUserFormidExample
import org.springframework.stereotype.Service
import javax.annotation.Resource
import java.time.LocalDateTime


@Service
class MallUserFormIdService {
    @Resource
    private var userFormIdMapper: MallUserFormidMapper? = null

    fun queryByOpenId(openId: String): MallUserFormid {
        val example = MallUserFormidExample()
        //符合找到该用户记录，且可用次数大于1，且还未过期
        example.or().andOpenidEqualTo(openId).andExpireTimeGreaterThan(LocalDateTime.now())
        return userFormIdMapper!!.selectOneByExample(example)
    }

    fun updateUserFormId(userFormid: MallUserFormid): Int {
        //更新或者删除缓存
        return if (userFormid.isprepay && userFormid.useamount > 1) {
            userFormid.useamount = userFormid.useamount - 1
            userFormid.updateTime = LocalDateTime.now()
            userFormIdMapper!!.updateByPrimaryKey(userFormid)
        } else {
            userFormIdMapper!!.deleteByPrimaryKey(userFormid.id)
        }
    }

    fun addUserFormid(userFormid: MallUserFormid) {
        userFormid.addTime = LocalDateTime.now()
        userFormid.updateTime = LocalDateTime.now()
        userFormIdMapper!!.insertSelective(userFormid)
    }
}