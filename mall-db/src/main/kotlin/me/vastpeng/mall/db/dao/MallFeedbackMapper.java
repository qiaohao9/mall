package me.vastpeng.mall.db.dao;

import java.util.List;
import me.vastpeng.mall.db.domain.MallFeedback;
import me.vastpeng.mall.db.domain.MallFeedbackExample;
import org.apache.ibatis.annotations.Param;

public interface MallFeedbackMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     */
    long countByExample(MallFeedbackExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     */
    int deleteByExample(MallFeedbackExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     */
    int insert(MallFeedback record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     */
    int insertSelective(MallFeedback record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MallFeedback selectOneByExample(MallFeedbackExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MallFeedback selectOneByExampleSelective(@Param("example") MallFeedbackExample example, @Param("selective") MallFeedback.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<MallFeedback> selectByExampleSelective(@Param("example") MallFeedbackExample example, @Param("selective") MallFeedback.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     */
    List<MallFeedback> selectByExample(MallFeedbackExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") MallFeedback record, @Param("example") MallFeedbackExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") MallFeedback record, @Param("example") MallFeedbackExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mall_feedback
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") MallFeedbackExample example);
}