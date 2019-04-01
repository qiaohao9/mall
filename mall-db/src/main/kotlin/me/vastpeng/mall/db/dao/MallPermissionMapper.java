package me.vastpeng.mall.db.dao;

import java.util.List;
import me.vastpeng.mall.db.domain.MallPermission;
import me.vastpeng.mall.db.domain.MallPermissionExample;
import org.apache.ibatis.annotations.Param;

public interface MallPermissionMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     */
    long countByExample(MallPermissionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     */
    int deleteByExample(MallPermissionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     */
    int insert(MallPermission record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     */
    int insertSelective(MallPermission record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MallPermission selectOneByExample(MallPermissionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MallPermission selectOneByExampleSelective(@Param("example") MallPermissionExample example, @Param("selective") MallPermission.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<MallPermission> selectByExampleSelective(@Param("example") MallPermissionExample example, @Param("selective") MallPermission.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     */
    List<MallPermission> selectByExample(MallPermissionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") MallPermission record, @Param("example") MallPermissionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") MallPermission record, @Param("example") MallPermissionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_permission
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") MallPermissionExample example);
}