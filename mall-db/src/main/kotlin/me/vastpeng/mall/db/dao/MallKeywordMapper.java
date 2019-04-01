package me.vastpeng.mall.db.dao;

import java.util.List;
import me.vastpeng.mall.db.domain.MallKeyword;
import me.vastpeng.mall.db.domain.MallKeywordExample;
import org.apache.ibatis.annotations.Param;

public interface MallKeywordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     */
    long countByExample(MallKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     */
    int deleteByExample(MallKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     */
    int insert(MallKeyword record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     */
    int insertSelective(MallKeyword record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MallKeyword selectOneByExample(MallKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MallKeyword selectOneByExampleSelective(@Param("example") MallKeywordExample example, @Param("selective") MallKeyword.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<MallKeyword> selectByExampleSelective(@Param("example") MallKeywordExample example, @Param("selective") MallKeyword.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     */
    List<MallKeyword> selectByExample(MallKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") MallKeyword record, @Param("example") MallKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") MallKeyword record, @Param("example") MallKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") MallKeywordExample example);
}