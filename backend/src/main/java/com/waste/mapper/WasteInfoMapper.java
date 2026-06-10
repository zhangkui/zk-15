package com.waste.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.entity.WasteInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WasteInfoMapper extends BaseMapper<WasteInfo> {

    @Select("SELECT w.*, c.category_name, c.category_code FROM waste_info w " +
            "LEFT JOIN waste_category c ON w.category_id = c.id " +
            "WHERE w.deleted = 0 " +
            "AND (#{keyword} IS NULL OR w.waste_name LIKE CONCAT('%', #{keyword}, '%') OR w.waste_no LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{status} IS NULL OR w.status = #{status}) " +
            "ORDER BY w.created_time DESC")
    IPage<WasteInfo> selectPageWithCategory(Page<WasteInfo> page,
                                            @Param("keyword") String keyword,
                                            @Param("status") Integer status);
}
