package com.labijie.application.web.data.mapper;

import com.labijie.application.web.data.domain.RegionArea;
import com.labijie.application.web.data.domain.RegionAreaExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface RegionAreaMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    long countByExample(RegionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int deleteByExample(RegionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int insert(RegionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int insertSelective(RegionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    List<RegionArea> selectByExampleWithRowbounds(RegionAreaExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<RegionArea> selectByExampleSelective(@Param("example") RegionAreaExample example, @Param("selective") RegionArea.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    List<RegionArea> selectByExample(RegionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    RegionArea selectByPrimaryKeySelective(@Param("id") String id, @Param("selective") RegionArea.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    RegionArea selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") RegionArea record, @Param("example") RegionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") RegionArea record, @Param("example") RegionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RegionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RegionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsert(@Param("list") List<RegionArea> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core_region_area
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsertSelective(@Param("list") List<RegionArea> list, @Param("selective") RegionArea.Column ... selective);
}