package com.waste.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    @Select("SELECT * FROM operation_log " +
            "WHERE (#{businessType} IS NULL OR business_type = #{businessType}) " +
            "AND (#{businessId} IS NULL OR business_id = #{businessId}) " +
            "ORDER BY created_time DESC")
    IPage<OperationLog> selectPageWithConditions(Page<OperationLog> page,
                                                 @Param("businessType") String businessType,
                                                 @Param("businessId") Long businessId);
}
