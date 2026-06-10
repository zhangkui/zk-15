package com.waste.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.entity.FeeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FeeRecordMapper extends BaseMapper<FeeRecord> {

    @Select("SELECT f.*, w.waste_name, o.order_no FROM fee_record f " +
            "LEFT JOIN waste_info w ON f.waste_id = w.id " +
            "LEFT JOIN pickup_order o ON f.order_id = o.id " +
            "WHERE f.deleted = 0 " +
            "AND (#{keyword} IS NULL OR f.fee_no LIKE CONCAT('%', #{keyword}, '%') OR f.payer_name LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{status} IS NULL OR f.status = #{status}) " +
            "ORDER BY f.created_time DESC")
    IPage<FeeRecord> selectPageWithRelations(Page<FeeRecord> page,
                                             @Param("keyword") String keyword,
                                             @Param("status") Integer status);
}
